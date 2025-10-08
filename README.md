Asynchronous Tax Transaction Orchestration (CaSP → dbTax Flow)
Overview

This document describes the architecture for handling tax-related transactions in the CaSP service, which communicates asynchronously with dbTax over Kafka.

When a transaction occurs in CaSP:

Multiple tax calculation requests are published to dbTax via Kafka.

dbTax processes and responds asynchronously with calculated tax values.

CaSP waits up to 10 minutes for responses.

For received responses, CaSP applies the actual tax values.

For missing responses, CaSP applies default tax values after the timeout.

This approach ensures reliability, scalability, and correctness in tax computation without blocking the main transaction workflow.

--
Problem Statement

Transactions in CaSP involve multiple tax calculation requests to dbTax.
Each transaction can produce multiple Kafka messages (based on transaction_id).

We need to:

Send multiple tax computation requests for a given transaction.

Wait for up to 10 minutes for dbTax responses.

Apply tax values for responses received.

Use default values for any missing responses after timeout.

Ensure all of this happens asynchronously without blocking other transactions.

---

Challenges
Challenge	Description
Partial responses	Some tax requests may return while others may not. We must handle both cases gracefully.
Timeout handling	Need deterministic completion after 10 minutes regardless of pending responses.
Parallel execution	Multiple transactions can be in “waiting” state simultaneously — each should operate independently.
Persistence & recovery	Must persist state to resume transactions even if CaSP restarts.

--

Solution Overview

The transaction is divided into two stages:

Stage 1 – Initiation:

Execute initial steps of the transaction.

Publish tax requests to Kafka.

Persist tracking data in DB.

Schedule completion after 10 minutes.

Stage 2 – Completion:

Collect responses asynchronously as they arrive.

Once all responses are received, or 10 minutes have elapsed:

Apply actual tax values for responses received.

Apply default tax values for missing ones.

Complete the transaction.

--

Database Design
1. transaction_tracker

Stores metadata per transaction for tracking its progress.

Column	Type	Description
id	BIGINT (PK)	Auto-generated primary key
txn_id	VARCHAR	Unique identifier for transaction
req_count	INT	Number of tax requests published
sent_at	TIMESTAMP	When the requests were sent
completed	BOOLEAN	True if transaction finalized
all_responses_received	BOOLEAN	True if all expected responses received
timeout_at	TIMESTAMP	Scheduled time for fallback execution

---

Detailed Flow
Stage 1: Transaction Initiation (CaSP)

public void processTransaction(String txnId) {
    step1();
    step2();

    int requestCount = kafkaProducerService.publishTaxRequests(txnId);

    TransactionTracker tracker = new TransactionTracker();
    tracker.setTxnId(txnId);
    tracker.setReqCount(requestCount);
    tracker.setSentAt(Instant.now());
    tracker.setCompleted(false);
    tracker.setAllResponsesReceived(false);
    trackerRepo.save(tracker);

    taskScheduler.schedule(
        () -> completeTransaction(txnId),
        Date.from(Instant.now().plus(10, ChronoUnit.MINUTES))
    );
}

Stage 2: Receiving Responses (dbTax → Kafka → CaSP)
@KafkaListener(topics = "dbtax-response-topic")
public void handleTaxResponse(TaxResponse response) {
    String txnId = response.getTxnId();
    taxResponseRepo.save(response);

    TransactionTracker tracker = trackerRepo.findByTxnId(txnId);
    long receivedCount = taxResponseRepo.countByTxnId(txnId);

    if (receivedCount == tracker.getReqCount()) {
        tracker.setAllResponsesReceived(true);
        trackerRepo.save(tracker);
        completeTransaction(txnId);  // Early trigger if all received
    }
}

Stage 3: Transaction Completion

@Async
public void completeTransaction(String txnId) {
    TransactionTracker tracker = trackerRepo.findByTxnId(txnId);
    if (tracker.isCompleted()) return;

    List<TaxResponse> responses = taxResponseRepo.findByTxnId(txnId);
    Map<String, BigDecimal> taxMap = buildTaxMap(responses, tracker.getReqCount());

    step3_applyTaxes(taxMap);  // Apply actual + default values
    step4_finalizeTransaction();

    tracker.setCompleted(true);
    trackerRepo.save(tracker);
}

Helper:
private Map<String, BigDecimal> buildTaxMap(List<TaxResponse> responses, int expectedCount) {
    Map<String, BigDecimal> map = new HashMap<>();
    for (TaxResponse r : responses) map.put(r.getReqRef(), r.getTaxValue());
    for (int i = 1; i <= expectedCount; i++)
        map.putIfAbsent("REQ_" + i, DEFAULT_TAX_VALUE);
    return map;
}

Spring Configuration
@Configuration
@EnableAsync
@EnableScheduling
public class AppConfig {
    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }
}

--

Key Advantages
Feature	Benefit
Non-blocking	Each transaction runs asynchronously without holding threads for 10 minutes.
Partial response handling	Applies received tax values while defaulting missing ones.
Resilience	Persistent tracking ensures recovery after restarts or failures.
Scalability	Supports thousands of concurrent transactions independently.
Timeout enforcement	Ensures every transaction completes deterministically within 10 minutes.
Early completion	Automatically finishes early if all dbTax responses are received sooner.

--

Conclusion

This design enables CaSP to handle multi-request tax computations with dbTax safely and efficiently.
It ensures:

Each transaction is independently tracked,

No blocking or thread starvation occurs,

Partial responses are handled intelligently, and

Every transaction deterministically completes after the timeout window.

This balances reliability, scalability, and simplicity — ideal for production in a Kafka-based distributed system.
