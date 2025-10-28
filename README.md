package com.consumerservice.pocconsumer.service;

import com.consumerservice.pocconsumer.entity.RequestResponse;
import com.consumerservice.pocconsumer.repo.RequestResponseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

/**
 * JoinProcessor
 * ---------------------
 * This service listens to:
 *  1. producer_kafka_topic → to receive requests
 *  2. external_service_topic → to receive enriched responses
 *
 * It joins them based on txnId and maintains a 10-minute processing window.
 *
 * Example timeline to visualize:
 *  --------------------------------------------------------------------------
 *  6:30 - Request (t1, t2, t3) received → stored in DB with req_json
 *  6:32 - Response for t1 & t2 arrives → immediately updates DB for those two
 *  6:40 - Still no response for t3 → checkAndProcess() sees it's 10 mins old
 *         → updates DB for t3 with default taxRate=5
 *  --------------------------------------------------------------------------
 */

@Service
@RequiredArgsConstructor
public class JoinProcessor {

    private final RequestResponseRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    // Map to store when each request arrived → used to track 10-minute window
    private final Map<String, Long> requestTimestamps = new HashMap<>();

    // Map to store responses temporarily in memory
    // Example:
    // responses = {
    //   "TXN101": {"txnId":"TXN101", "amount":23, "taxRate":10},
    //   "TXN102": {"txnId":"TXN102", "amount":13, "taxRate":12}
    // }
    private final Map<String, Map<String, Object>> responses = new HashMap<>();


    /**
     * Called whenever a new message (request) is published to producer_kafka_topic.
     * Simulates: incoming transaction request from Producer service.
     */
    @KafkaListener(topics = "producer_kafka_topic", groupId = "consumer-group")
    public void consumeRequest(String message) throws Exception {

        // Incoming message example (raw JSON string):
        // {"txnId": "TXN101", "amount": 5000}

        // Convert JSON → Java Map
        Map<String, Object> req = mapper.readValue(message, Map.class);
        // req = { "txnId" : "TXN101", "amount" : 5000 }

        String txnId = (String) req.get("txnId");

        // Record when this request was seen
        requestTimestamps.put(txnId, Instant.now().toEpochMilli());

        // Store the request in DB if not already stored
        RequestResponse rr = repository.findByTransactionId(txnId);
        if (rr == null) {
            repository.save(RequestResponse.builder()
                    .transactionId(txnId)
                    .uniqueIdentifier(UUID.randomUUID().toString())
                    .reqJson(mapper.writeValueAsString(req)) // store raw JSON as string
                    .taxRate(null) // no tax yet
                    .build());
        }

        System.out.println("🟢 [6:30] Received request: " + txnId);
//        the req for all 3 would be populated in the talbe
    }


    /**
     * Called whenever a new message (response) is published to external_service_topic.
     * Simulates: external system enriched transaction and sent back with taxRate.
     */
    @KafkaListener(topics = "external_service_topic", groupId = "consumer_group")
    public void consumeResponse(String message) throws Exception {
        System.out.println("\n📥 [Consumer] Received response message: " + message);

        Map<String, Object> responseMap;

        try {
            // Sometimes the producer sends a raw JSON string (e.g. "{\"txnId\":\"t1\",\"amount\":23,\"taxRate\":10}")
            // Sometimes it’s nested (e.g. "\"{...}\""). We handle both cases.
            if (message.startsWith("{")) {
                responseMap = mapper.readValue(message, Map.class);
            } else {
                String innerJson = mapper.readValue(message, String.class);
                responseMap = mapper.readValue(innerJson, Map.class);
            }

            String txnId = (String) responseMap.get("txnId");
            responses.put(txnId, responseMap); // Save in-memory for later reference
            // responses = {
            //   "TXN101": {"txnId":"TXN101", "amount":23, "taxRate":10},
            //   "TXN102": {"txnId":"TXN102", "amount":13, "taxRate":12}
            // }
            System.out.println("🧩 [Response Parsed] " + responseMap);

            Long reqTime = requestTimestamps.get(txnId); // When did we receive the request?

            // ---------------------------------------------------------------
            // CASE 1: Happy Path
            // Request exists AND response came within 10 minutes.
            // ---------------------------------------------------------------
            if (reqTime != null && Instant.now().toEpochMilli() - reqTime < 600_000) {

                RequestResponse rr = repository.findByTransactionId(txnId);
                if (rr != null) {
                    rr.setResJson(mapper.writeValueAsString(responseMap));  // Save the full response
                    rr.setTaxRate(Double.parseDouble(responseMap.get("taxRate").toString())); // Extract taxRate
                    repository.save(rr);
                    System.out.println("✅ [Matched + Updated Immediately] txn: " + txnId + " | taxRate: " + rr.getTaxRate());
                }

            }
            // ---------------------------------------------------------------
            // CASE 2: Response arrived but request not yet recorded
            // (Async timing issue)
            // ---------------------------------------------------------------
            else if (reqTime == null) {
                // This happens if the external service responded faster than the DB commit of the producer.
                // We do NOT update DB here because request row may appear in a few milliseconds.
                // Instead, `checkAndProcess()` will catch and fix this later.
                System.out.println("⏳ [Response Before Request] txn: " + txnId + " — skipping now, will retry in next scheduled check");

            }
            // ---------------------------------------------------------------
            // CASE 3: Response arrived after the 10-min window
            // (Late response — we missed the window)
            // ---------------------------------------------------------------
            else {
                // You *could* apply the default tax rate here,
                // but we avoid doing that directly because:
                // - Some responses come milliseconds before 10 min cutoff but get delayed in processing.
                // - We prefer all delayed handling to go through `checkAndProcess()` for consistency.
                System.out.println("[Late Response] txn: " + txnId + " — came after 10-min cutoff, will be handled in reconciliation");
            }

        } catch (Exception e) {
            System.err.println("Error while processing response: " + e.getMessage());
        }
    }


    /**
     * This scheduled method runs every minute.
     * It checks for requests that are waiting for a response beyond 10 minutes.
     * For each expired request:
     *  - If a response exists → update with it (late but valid)
     *  - If no response → update DB with default taxRate=5
     * Finally, removes the entry from in-memory maps.
     */
    @Scheduled(fixedRate = 60000)
    public void checkAndProcess() {
        long now = Instant.now().toEpochMilli();

        for (String txnId : new HashSet<>(requestTimestamps.keySet())) {
            long reqTime = requestTimestamps.get(txnId);

            // Check if request is older than 10 minutes
            if (now - reqTime > 600_000) { // 10 mins = 600000 ms
                try {
                    Map<String, Object> res = responses.get(txnId);
                    RequestResponse rr = repository.findByTransactionId(txnId);

                    if (rr == null) continue;

                    if (res != null) {
                        // A response exists (maybe it came earlier)
                        rr.setResJson(mapper.writeValueAsString(res));
                        rr.setTaxRate(Double.parseDouble(res.get("taxRate").toString()));
                        System.out.println("✅ [6:40] Response matched for txn " + txnId);
                    } else {
                        // No response received even after 10 mins → fallback to default taxRate=5
                        rr.setResJson(null);
                        rr.setTaxRate(5.0);
                        System.out.println("[6:40] Response missing after 10 min for txn " + txnId);
                    }

                    repository.save(rr);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // Clean up memory — this txn is done (either success or timeout)
                    requestTimestamps.remove(txnId);
                    responses.remove(txnId);
                }
            }
        }
    }
}
