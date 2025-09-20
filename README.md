package com.example.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.CorporateActionDistribution;

import java.util.List;

public interface CorporateActionDistributionRepository extends JpaRepository<CorporateActionDistribution, Long> {

    @Query("""
        SELECT cad.id AS distributionId,
               cad.transactionId AS transactionId,
               cad.securitiesPositionId AS securitiesPositionId,
               tt.typeAbbrv AS typeAbbrv,
               caDis.paymentDate AS paymentDate,
               acc.revenueProductCode AS revenueProductCode,
               acc.legacyAccountId AS legacyAccountId,
               le.legalEntityCode AS legalEntityCode,
               org.crdsId AS crdsId,
               sm.cusip AS cusip,
               sm.isin AS isin
        FROM CorporateActionDistribution cad
        LEFT JOIN CorporateActionDisbursement caDis 
               ON cad.transactionId = caDis.transactionId
        LEFT JOIN CaspTransaction ct 
               ON cad.transactionId = ct.transactionId
        LEFT JOIN TransactionType tt 
               ON ct.transactionTypeId = tt.id
        LEFT JOIN Account acc 
               ON cad.accountId = acc.id
        LEFT JOIN LegalEntity le 
               ON acc.legalEntityId = le.id
        LEFT JOIN DealParty dp 
               ON acc.dealId = dp.dealId
        LEFT JOIN Org org 
               ON dp.orgId = org.id
        LEFT JOIN SecuritiesMaster sm 
               ON caDis.securityId = sm.id
        WHERE cad.transactionId = :transactionId
    """)
    List<DistributionProjection> findDetailsByTransactionId(@Param("transactionId") Long transactionId);
}
package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.repository.CorporateActionDistributionRepository;
import com.example.demo.repository.DistributionProjection;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class CaspResponseService {

    private final CorporateActionDistributionRepository repository;

    public CaspResponseService(CorporateActionDistributionRepository repository) {
        this.repository = repository;
    }

    public List<CaspResponseDto> buildResponses(Long transactionId) {
        List<DistributionProjection> rows = repository.findDetailsByTransactionId(transactionId);

        List<CaspResponseDto> responses = new ArrayList<>();

        for (DistributionProjection row : rows) {
            CaspResponseDto resp = new CaspResponseDto();
            resp.setMessageId(row.getTransactionId() + "-" + row.getSecuritiesPositionId());
            resp.setMessageTimestamp(OffsetDateTime.now());
            resp.setRequestingSystemId("CaSP");

            CaspCashflowDto cashflow = new CaspCashflowDto();
            cashflow.setCashflowId(String.valueOf(row.getDistributionId()));
            cashflow.setCashflowType(row.getTypeAbbrv());
            cashflow.setValueDate(row.getPaymentDate());
            cashflow.setPrdSid(row.getRevenueProductCode());

            CaspPartyDto party = new CaspPartyDto();
            party.setSystemAccountId(row.getLegacyAccountId());
            party.setSystemId(null);
            party.setEntityId(row.getLegalEntityCode());
            party.setCrdSId(row.getCrdsId());

            CaspInstrumentDto instrument = new CaspInstrumentDto();
            if (row.getCusip() != null && row.getIsin() != null) {
                instrument.setType("CUSIP");  // your business rule
                instrument.setId(row.getCusip());
            } else if (row.getCusip() != null) {
                instrument.setType("CUSIP");
                instrument.setId(row.getCusip());
            } else if (row.getIsin() != null) {
                instrument.setType("ISIN");
                instrument.setId(row.getIsin());
            } else {
                instrument.setType(null);
                instrument.setId(null);
            }

            CaspDataDto data = new CaspDataDto();
            data.setCashflows(List.of(cashflow));
            data.setParty(party);
            data.setInstrument(instrument);
            data.setAdditionalAttributes(new HashMap<>());

            resp.setData(data);
            responses.add(resp);
        }

        return responses;
    }
}
