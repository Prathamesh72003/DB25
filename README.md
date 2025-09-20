1. DTOs
// CaspResponseDto.java
package com.example.demo.dto;

import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaspResponseDto {
    private String messageId;
    private OffsetDateTime messageTimestamp;
    private String requestingSystemId;
    private DataDto data;
}

// DataDto.java
package com.example.demo.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataDto {
    private List<CashflowDto> cashflows;
    private PartyDto party;
    private InstrumentDto instrument;
    private Map<String, Object> additionalAttributes; // always {}
}

// CashflowDto.java
package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashflowDto {
    private Long cashflowId;
    private String cashflowType;
    private String valueDate;
    private String prdSid;
}

// PartyDto.java
package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartyDto {
    private String systemAccountId;
    private String systemId;
    private String entityId;
    private String crdSId;
}

// InstrumentDto.java
package com.example.demo.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentDto {
    private String type;
    private String id;
}

2. Repository

We’ll fetch all distributions for a transactionId in one query:

// CaspProjection.java
package com.example.demo.repository;

public interface CaspProjection {
    Long getDistributionId();
    Long getSecuritiesPositionId();
    Long getTransactionId();

    String getTransactionTypeAbbrv();
    String getPaymentDate();

    String getRevenueProductCode();
    String getLegacyAccountId();

    String getLegalEntityCode();
    String getCrdsId();

    String getCusip();
    String getIsin();
}

// CaspRepository.java
package com.example.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.CorporateActionDistribution;

import java.util.List;

@Repository
public interface CaspRepository extends JpaRepository<CorporateActionDistribution, Long> {

    @Query(value = """
        SELECT 
            cad.id as distributionId,
            cad.securities_position_id as securitiesPositionId,
            cad.transaction_id as transactionId,
            
            tt.type_abbrv as transactionTypeAbbrv,
            dis.payment_date as paymentDate,

            acc.revenue_product_code as revenueProductCode,
            acc.legacy_account_id as legacyAccountId,

            le.legal_entity_code as legalEntityCode,
            org.crds_id as crdsId,

            sm.cusip as cusip,
            sm.isin as isin

        FROM corporate_action_distribution cad
        JOIN casp_transaction tx ON cad.transaction_id = tx.id
        JOIN transaction_type tt ON tx.transaction_type_id = tt.id
        LEFT JOIN corporate_action_disbursement dis ON dis.transaction_id = cad.transaction_id
        LEFT JOIN account acc ON cad.account_id = acc.id
        LEFT JOIN legal_entity le ON acc.legal_entity_id = le.id
        LEFT JOIN deal_party dp ON acc.deal_id = dp.deal_id
        LEFT JOIN org ON dp.org_id = org.id
        LEFT JOIN securities_master sm ON dis.security_id = sm.id

        WHERE cad.transaction_id = :transactionId
        """, nativeQuery = true)
    List<CaspProjection> fetchCaspData(@Param("transactionId") Long transactionId);
}

3. Service
// CaspService.java
package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.repository.CaspProjection;
import com.example.demo.repository.CaspRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CaspService {

    private final CaspRepository caspRepository;

    public CaspResponseDto buildResponse(Long transactionId) {
        List<CaspProjection> rows = caspRepository.fetchCaspData(transactionId);
        if (rows.isEmpty()) {
            throw new RuntimeException("No data found for transactionId " + transactionId);
        }

        CaspProjection first = rows.get(0);

        // messageId: combine securities_position_id + transaction_id (take from first row)
        String messageId = first.getSecuritiesPositionId() + "-" + first.getTransactionId();

        // cashflows
        List<CashflowDto> cashflows = rows.stream()
                .map(r -> CashflowDto.builder()
                        .cashflowId(r.getDistributionId())
                        .cashflowType(r.getTransactionTypeAbbrv())
                        .valueDate(r.getPaymentDate())
                        .prdSid(r.getRevenueProductCode())
                        .build())
                .collect(Collectors.toList());

        // party (same across all rows for given transactionId, so take from first)
        PartyDto party = PartyDto.builder()
                .systemAccountId(first.getLegacyAccountId())
                .systemId(null)
                .entityId(first.getLegalEntityCode())
                .crdSId(first.getCrdsId())
                .build();

        // instrument
        String type = null;
        String id = null;
        if (first.getCusip() != null && first.getIsin() != null) {
            type = "CUSIP"; // business rule: prefer CUSIP
            id = first.getCusip();
        } else if (first.getCusip() != null) {
            type = "CUSIP";
            id = first.getCusip();
        } else if (first.getIsin() != null) {
            type = "ISIN";
            id = first.getIsin();
        }

        InstrumentDto instrument = InstrumentDto.builder()
                .type(type)
                .id(id)
                .build();

        // build response
        return CaspResponseDto.builder()
                .messageId(messageId)
                .messageTimestamp(OffsetDateTime.now())
                .requestingSystemId("CaSP")
                .data(DataDto.builder()
                        .cashflows(cashflows)
                        .party(party)
                        .instrument(instrument)
                        .additionalAttributes(Collections.emptyMap())
                        .build())
                .build();
    }
}

4. Controller
// CaspController.java
package com.example.demo.controller;

import com.example.demo.dto.CaspResponseDto;
import com.example.demo.service.CaspService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/casp")
@RequiredArgsConstructor
public class CaspController {

    private final CaspService caspService;

    @GetMapping("/{transactionId}")
    public CaspResponseDto getCaspMessage(@PathVariable Long transactionId) {
        return caspService.buildResponse(transactionId);
    }
}
