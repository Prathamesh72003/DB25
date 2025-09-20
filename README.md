// DTO Classes

// Main Response DTO
package com.casp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateActionResponseDto {
    @JsonProperty("messageId")
    private String messageId;
    
    @JsonProperty("messageTimestamp")
    private String messageTimestamp;
    
    @JsonProperty("requestingSystemId")
    private String requestingSystemId;
    
    @JsonProperty("data")
    private DataDto data;
}

// Data DTO
package com.casp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataDto {
    @JsonProperty("cashflows")
    private List<CashflowDto> cashflows;
    
    @JsonProperty("party")
    private PartyDto party;
    
    @JsonProperty("instrument")
    private InstrumentDto instrument;
    
    @JsonProperty("additionalAttributes")
    private Map<String, Object> additionalAttributes;
}

// Cashflow DTO
package com.casp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashflowDto {
    @JsonProperty("cashflowId")
    private String cashflowId;
    
    @JsonProperty("cashflowType")
    private String cashflowType;
    
    @JsonProperty("valueDate")
    private String valueDate;
    
    @JsonProperty("prdSid")
    private String prdSid;
}

// Party DTO
package com.casp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyDto {
    @JsonProperty("systemAccountId")
    private String systemAccountId;
    
    @JsonProperty("systemId")
    private String systemId;
    
    @JsonProperty("entityId")
    private String entityId;
    
    @JsonProperty("crdSId")
    private String crdSId;
}

// Instrument DTO
package com.casp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentDto {
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("id")
    private String id;
}

// Request DTO
package com.casp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaspTransactionRequestDto {
    private String transactionId;
}

// Entity DTOs for database mapping
package com.casp.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateActionDistributionDto {
    private String id;
    private String securitiesPositionId;
    private String transactionId;
    private String accountId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaspTransactionDto {
    private String transactionId;
    private String transactionTypeId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionTypeDto {
    private String transactionTypeId;
    private String typeAbbrv;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateActionDisbursementDto {
    private String transactionId;
    private String paymentDate;
    private String securityId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private String accountId;
    private String revenueProductCode;
    private String legacyAccountId;
    private String legalEntityId;
    private String dealId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalEntityDto {
    private String legalEntityId;
    private String legalEntityCode;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealPartyDto {
    private String dealId;
    private String orgId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgDto {
    private String orgId;
    private String crdsId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecuritiesMasterDto {
    private String securityId;
    private String cusip;
    private String isin;
}

// Repository Interface
package com.casp.repository;

import com.casp.dto.entity.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CorporateActionRepository {
    
    @Query(value = "SELECT id, securities_position_id as securitiesPositionId, transaction_id as transactionId, account_id as accountId " +
                   "FROM corporate_action_distribution WHERE transaction_id = :transactionId", nativeQuery = true)
    CorporateActionDistributionDto findCorporateActionDistributionByTransactionId(@Param("transactionId") String transactionId);
    
    @Query(value = "SELECT transaction_id as transactionId, transaction_type_id as transactionTypeId " +
                   "FROM casp_transaction WHERE transaction_id = :transactionId", nativeQuery = true)
    CaspTransactionDto findCaspTransactionByTransactionId(@Param("transactionId") String transactionId);
    
    @Query(value = "SELECT transaction_type_id as transactionTypeId, type_abbrv as typeAbbrv " +
                   "FROM transaction_type WHERE transaction_type_id = :transactionTypeId", nativeQuery = true)
    TransactionTypeDto findTransactionTypeById(@Param("transactionTypeId") String transactionTypeId);
    
    @Query(value = "SELECT transaction_id as transactionId, payment_date as paymentDate, security_id as securityId " +
                   "FROM corporate_action_disbursement WHERE transaction_id = :transactionId", nativeQuery = true)
    CorporateActionDisbursementDto findCorporateActionDisbursementByTransactionId(@Param("transactionId") String transactionId);
    
    @Query(value = "SELECT account_id as accountId, revenue_product_code as revenueProductCode, " +
                   "legacy_account_id as legacyAccountId, legal_entity_id as legalEntityId, deal_id as dealId " +
                   "FROM account WHERE account_id = :accountId", nativeQuery = true)
    AccountDto findAccountById(@Param("accountId") String accountId);
    
    @Query(value = "SELECT legal_entity_id as legalEntityId, legal_entity_code as legalEntityCode " +
                   "FROM legal_entity WHERE legal_entity_id = :legalEntityId", nativeQuery = true)
    LegalEntityDto findLegalEntityById(@Param("legalEntityId") String legalEntityId);
    
    @Query(value = "SELECT deal_id as dealId, org_id as orgId " +
                   "FROM deal_party WHERE deal_id = :dealId", nativeQuery = true)
    DealPartyDto findDealPartyByDealId(@Param("dealId") String dealId);
    
    @Query(value = "SELECT org_id as orgId, crds_id as crdsId " +
                   "FROM org WHERE org_id = :orgId", nativeQuery = true)
    OrgDto findOrgById(@Param("orgId") String orgId);
    
    @Query(value = "SELECT security_id as securityId, cusip, isin " +
                   "FROM securities_master WHERE security_id = :securityId", nativeQuery = true)
    SecuritiesMasterDto findSecuritiesMasterBySecurityId(@Param("securityId") String securityId);
}

// Service Interface
package com.casp.service;

import com.casp.dto.response.CorporateActionResponseDto;

public interface CorporateActionService {
    CorporateActionResponseDto getCorporateActionResponse(String transactionId);
}

// Service Implementation
package com.casp.service.impl;

import com.casp.dto.entity.*;
import com.casp.dto.response.*;
import com.casp.mapping.CorporateActionMapper;
import com.casp.repository.CorporateActionRepository;
import com.casp.service.CorporateActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorporateActionServiceImpl implements CorporateActionService {

    private final CorporateActionRepository repository;
    private final CorporateActionMapper mapper;

    @Override
    public CorporateActionResponseDto getCorporateActionResponse(String transactionId) {
        log.info("Processing corporate action response for transaction ID: {}", transactionId);

        try {
            // Step 1: Get corporate action distribution data
            CorporateActionDistributionDto distributionData = repository.findCorporateActionDistributionByTransactionId(transactionId);
            if (distributionData == null) {
                log.warn("No corporate action distribution found for transaction ID: {}", transactionId);
                return createEmptyResponse();
            }

            // Step 2: Get transaction type data
            CaspTransactionDto caspTransaction = repository.findCaspTransactionByTransactionId(transactionId);
            String cashflowType = null;
            if (caspTransaction != null && caspTransaction.getTransactionTypeId() != null) {
                TransactionTypeDto transactionType = repository.findTransactionTypeById(caspTransaction.getTransactionTypeId());
                if (transactionType != null) {
                    cashflowType = transactionType.getTypeAbbrv();
                }
            }

            // Step 3: Get disbursement data
            CorporateActionDisbursementDto disbursementData = repository.findCorporateActionDisbursementByTransactionId(transactionId);

            // Step 4: Get account data
            AccountDto accountData = null;
            if (distributionData.getAccountId() != null) {
                accountData = repository.findAccountById(distributionData.getAccountId());
            }

            // Step 5: Get legal entity data
            LegalEntityDto legalEntityData = null;
            if (accountData != null && accountData.getLegalEntityId() != null) {
                legalEntityData = repository.findLegalEntityById(accountData.getLegalEntityId());
            }

            // Step 6: Get deal party and org data
            String crdsId = null;
            if (accountData != null && accountData.getDealId() != null) {
                DealPartyDto dealPartyData = repository.findDealPartyByDealId(accountData.getDealId());
                if (dealPartyData != null && dealPartyData.getOrgId() != null) {
                    OrgDto orgData = repository.findOrgById(dealPartyData.getOrgId());
                    if (orgData != null) {
                        crdsId = orgData.getCrdsId();
                    }
                }
            }

            // Step 7: Get securities master data
            InstrumentDto instrumentData = null;
            if (disbursementData != null && disbursementData.getSecurityId() != null) {
                SecuritiesMasterDto securitiesData = repository.findSecuritiesMasterBySecurityId(disbursementData.getSecurityId());
                instrumentData = mapper.mapToInstrumentDto(securitiesData);
            }

            // Build response
            return mapper.mapToCorporateActionResponse(
                    distributionData,
                    cashflowType,
                    disbursementData,
                    accountData,
                    legalEntityData,
                    crdsId,
                    instrumentData
            );

        } catch (Exception e) {
            log.error("Error processing corporate action response for transaction ID: {}", transactionId, e);
            return createEmptyResponse();
        }
    }

    private CorporateActionResponseDto createEmptyResponse() {
        return CorporateActionResponseDto.builder()
                .messageId(null)
                .messageTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .requestingSystemId("CaSP")
                .data(DataDto.builder()
                        .cashflows(Collections.emptyList())
                        .party(PartyDto.builder().build())
                        .instrument(InstrumentDto.builder().build())
                        .additionalAttributes(new HashMap<>())
                        .build())
                .build();
    }
}

// Mapper Interface
package com.casp.mapping;

import com.casp.dto.entity.*;
import com.casp.dto.response.*;

public interface CorporateActionMapper {
    CorporateActionResponseDto mapToCorporateActionResponse(
            CorporateActionDistributionDto distributionData,
            String cashflowType,
            CorporateActionDisbursementDto disbursementData,
            AccountDto accountData,
            LegalEntityDto legalEntityData,
            String crdsId,
            InstrumentDto instrumentData
    );
    
    InstrumentDto mapToInstrumentDto(SecuritiesMasterDto securitiesData);
}

// Mapper Implementation
package com.casp.mapping.impl;

import com.casp.dto.entity.*;
import com.casp.dto.response.*;
import com.casp.mapping.CorporateActionMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;

@Component
public class CorporateActionMapperImpl implements CorporateActionMapper {

    @Override
    public CorporateActionResponseDto mapToCorporateActionResponse(
            CorporateActionDistributionDto distributionData,
            String cashflowType,
            CorporateActionDisbursementDto disbursementData,
            AccountDto accountData,
            LegalEntityDto legalEntityData,
            String crdsId,
            InstrumentDto instrumentData) {

        // Create messageId from securities_position_id and transaction_id
        String messageId = null;
        if (distributionData != null) {
            messageId = (distributionData.getSecuritiesPositionId() != null ? distributionData.getSecuritiesPositionId() : "") +
                       (distributionData.getTransactionId() != null ? distributionData.getTransactionId() : "");
            if (messageId.isEmpty()) {
                messageId = null;
            }
        }

        // Create cashflow
        CashflowDto cashflow = CashflowDto.builder()
                .cashflowId(distributionData != null ? distributionData.getId() : null)
                .cashflowType(cashflowType)
                .valueDate(disbursementData != null ? disbursementData.getPaymentDate() : null)
                .prdSid(accountData != null ? accountData.getRevenueProductCode() : null)
                .build();

        // Create party
        PartyDto party = PartyDto.builder()
                .systemAccountId(accountData != null ? accountData.getLegacyAccountId() : null)
                .systemId(null) // As per requirement, this is null
                .entityId(legalEntityData != null ? legalEntityData.getLegalEntityCode() : null)
                .crdSId(crdsId)
                .build();

        // Create data
        DataDto data = DataDto.builder()
                .cashflows(Collections.singletonList(cashflow))
                .party(party)
                .instrument(instrumentData != null ? instrumentData : InstrumentDto.builder().build())
                .additionalAttributes(new HashMap<>())
                .build();

        // Create main response
        return CorporateActionResponseDto.builder()
                .messageId(messageId)
                .messageTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .requestingSystemId("CaSP")
                .data(data)
                .build();
    }

    @Override
    public InstrumentDto mapToInstrumentDto(SecuritiesMasterDto securitiesData) {
        if (securitiesData == null) {
            return InstrumentDto.builder().build();
        }

        String type = null;
        String id = null;

        // Check cusip and isin according to business rules
        if (securitiesData.getCusip() != null && securitiesData.getIsin() != null) {
            // Both present, return CUSIP
            type = "CUSIP";
            id = securitiesData.getCusip();
        } else if (securitiesData.getCusip() != null) {
            // Only CUSIP present
            type = "CUSIP";
            id = securitiesData.getCusip();
        } else if (securitiesData.getIsin() != null) {
            // Only ISIN present
            type = "ISIN";
            id = securitiesData.getIsin();
        }
        // If both are null, type and id remain null

        return InstrumentDto.builder()
                .type(type)
                .id(id)
                .build();
    }
}

// Controller
package com.casp.controller;

import com.casp.dto.request.CaspTransactionRequestDto;
import com.casp.dto.response.CorporateActionResponseDto;
import com.casp.service.CorporateActionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/corporate-action")
@RequiredArgsConstructor
@Slf4j
public class CorporateActionController {

    private final CorporateActionService corporateActionService;

    @PostMapping("/transaction-response")
    public ResponseEntity<CorporateActionResponseDto> getCorporateActionResponse(
            @RequestBody CaspTransactionRequestDto request) {
        
        log.info("Received corporate action request for transaction ID: {}", request.getTransactionId());
        
        CorporateActionResponseDto response = corporateActionService.getCorporateActionResponse(request.getTransactionId());
        
        log.info("Returning corporate action response for transaction ID: {}", request.getTransactionId());
        
        return ResponseEntity.ok(response);
    }
}
