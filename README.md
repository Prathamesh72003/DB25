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
    private Long transactionId;
}

// Entity DTOs for database mapping - Using Long for IDs
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
    private Long id;
    private Long securitiesPositionId;
    private Long transactionId;
    private Long accountId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaspTransactionDto {
    private Long transactionId;
    private Long transactionTypeId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionTypeDto {
    private Long transactionTypeId;
    private String typeAbbrv;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateActionDisbursementDto {
    private Long transactionId;
    private String paymentDate;
    private Long securityId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long accountId;
    private String revenueProductCode;
    private String legacyAccountId;
    private Long legalEntityId;
    private Long dealId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegalEntityDto {
    private Long legalEntityId;
    private String legalEntityCode;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealPartyDto {
    private Long dealId;
    private Long orgId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgDto {
    private Long orgId;
    private String crdsId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecuritiesMasterDto {
    private Long securityId;
    private String cusip;
    private String isin;
}

// Repository Interface
package com.casp.repository;

import com.casp.dto.entity.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorporateActionRepository {
    
    // Get ALL corporate action distributions for a transaction_id
    @Query(value = "SELECT id, securities_position_id as securitiesPositionId, transaction_id as transactionId, account_id as accountId " +
                   "FROM corporate_action_distribution WHERE transaction_id = :transactionId", nativeQuery = true)
    List<CorporateActionDistributionDto> findCorporateActionDistributionsByTransactionId(@Param("transactionId") Long transactionId);
    
    @Query(value = "SELECT transaction_id as transactionId, transaction_type_id as transactionTypeId " +
                   "FROM casp_transaction WHERE transaction_id = :transactionId LIMIT 1", nativeQuery = true)
    CaspTransactionDto findCaspTransactionByTransactionId(@Param("transactionId") Long transactionId);
    
    @Query(value = "SELECT transaction_type_id as transactionTypeId, type_abbrv as typeAbbrv " +
                   "FROM transaction_type WHERE transaction_type_id = :transactionTypeId", nativeQuery = true)
    TransactionTypeDto findTransactionTypeById(@Param("transactionTypeId") Long transactionTypeId);
    
    // Get ALL disbursements for a transaction_id
    @Query(value = "SELECT transaction_id as transactionId, payment_date as paymentDate, security_id as securityId " +
                   "FROM corporate_action_disbursement WHERE transaction_id = :transactionId", nativeQuery = true)
    List<CorporateActionDisbursementDto> findCorporateActionDisbursementsByTransactionId(@Param("transactionId") Long transactionId);
    
    @Query(value = "SELECT account_id as accountId, revenue_product_code as revenueProductCode, " +
                   "legacy_account_id as legacyAccountId, legal_entity_id as legalEntityId, deal_id as dealId " +
                   "FROM account WHERE account_id = :accountId", nativeQuery = true)
    AccountDto findAccountById(@Param("accountId") Long accountId);
    
    @Query(value = "SELECT legal_entity_id as legalEntityId, legal_entity_code as legalEntityCode " +
                   "FROM legal_entity WHERE legal_entity_id = :legalEntityId", nativeQuery = true)
    LegalEntityDto findLegalEntityById(@Param("legalEntityId") Long legalEntityId);
    
    @Query(value = "SELECT deal_id as dealId, org_id as orgId " +
                   "FROM deal_party WHERE deal_id = :dealId LIMIT 1", nativeQuery = true)
    DealPartyDto findDealPartyByDealId(@Param("dealId") Long dealId);
    
    @Query(value = "SELECT org_id as orgId, crds_id as crdsId " +
                   "FROM org WHERE org_id = :orgId", nativeQuery = true)
    OrgDto findOrgById(@Param("orgId") Long orgId);
    
    @Query(value = "SELECT security_id as securityId, cusip, isin " +
                   "FROM securities_master WHERE security_id = :securityId", nativeQuery = true)
    SecuritiesMasterDto findSecuritiesMasterBySecurityId(@Param("securityId") Long securityId);
}

// Service Interface
package com.casp.service;

import com.casp.dto.response.CorporateActionResponseDto;

public interface CorporateActionService {
    CorporateActionResponseDto getCorporateActionResponse(Long transactionId);
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorporateActionServiceImpl implements CorporateActionService {

    private final CorporateActionRepository repository;
    private final CorporateActionMapper mapper;

    @Override
    public CorporateActionResponseDto getCorporateActionResponse(Long transactionId) {
        log.info("Processing corporate action response for transaction ID: {}", transactionId);

        try {
            // Step 1: Get ALL corporate action distributions for this transaction_id
            List<CorporateActionDistributionDto> distributionDataList = repository.findCorporateActionDistributionsByTransactionId(transactionId);
            if (distributionDataList == null || distributionDataList.isEmpty()) {
                log.warn("No corporate action distributions found for transaction ID: {}", transactionId);
                return createEmptyResponse();
            }

            // Step 2: Get transaction type data (should be same for all, so get once)
            CaspTransactionDto caspTransaction = repository.findCaspTransactionByTransactionId(transactionId);
            String cashflowType = null;
            if (caspTransaction != null && caspTransaction.getTransactionTypeId() != null) {
                TransactionTypeDto transactionType = repository.findTransactionTypeById(caspTransaction.getTransactionTypeId());
                if (transactionType != null) {
                    cashflowType = transactionType.getTypeAbbrv();
                }
            }

            // Step 3: Get ALL disbursements for this transaction_id
            List<CorporateActionDisbursementDto> disbursementDataList = repository.findCorporateActionDisbursementsByTransactionId(transactionId);
            
            // Create a map for quick disbursement lookup if needed
            Map<Long, CorporateActionDisbursementDto> disbursementMap = new HashMap<>();
            if (disbursementDataList != null && !disbursementDataList.isEmpty()) {
                // For simplicity, we'll use the first disbursement's payment date for all
                // You can modify this logic if needed
                CorporateActionDisbursementDto firstDisbursement = disbursementDataList.get(0);
                disbursementMap.put(transactionId, firstDisbursement);
            }

            // Step 4: Process each corporate action distribution
            List<CashflowDto> cashflows = new ArrayList<>();
            Set<Long> processedAccounts = new HashSet<>(); // To avoid duplicate party data processing
            
            AccountDto accountData = null;
            LegalEntityDto legalEntityData = null;
            String crdsId = null;
            InstrumentDto instrumentData = null;

            for (CorporateActionDistributionDto distributionData : distributionDataList) {
                // Create cashflow for each distribution
                CashflowDto cashflow = CashflowDto.builder()
                        .cashflowId(distributionData.getId() != null ? distributionData.getId().toString() : null)
                        .cashflowType(cashflowType)
                        .valueDate(disbursementMap.containsKey(transactionId) ? 
                                 disbursementMap.get(transactionId).getPaymentDate() : null)
                        .build();

                // Get account data for this distribution
                AccountDto currentAccountData = null;
                if (distributionData.getAccountId() != null) {
                    currentAccountData = repository.findAccountById(distributionData.getAccountId());
                    if (currentAccountData != null) {
                        cashflow.setPrdSid(currentAccountData.getRevenueProductCode());
                    }
                }

                cashflows.add(cashflow);

                // Process party and instrument data only once (using first distribution's account)
                if (!processedAccounts.contains(distributionData.getAccountId()) && currentAccountData != null) {
                    accountData = currentAccountData;
                    processedAccounts.add(distributionData.getAccountId());

                    // Get legal entity data
                    if (accountData.getLegalEntityId() != null) {
                        legalEntityData = repository.findLegalEntityById(accountData.getLegalEntityId());
                    }

                    // Get crdsId via deal party chain
                    if (accountData.getDealId() != null) {
                        DealPartyDto dealPartyData = repository.findDealPartyByDealId(accountData.getDealId());
                        if (dealPartyData != null && dealPartyData.getOrgId() != null) {
                            OrgDto orgData = repository.findOrgById(dealPartyData.getOrgId());
                            if (orgData != null) {
                                crdsId = orgData.getCrdsId();
                            }
                        }
                    }
                }
            }

            // Step 5: Get instrument data from disbursement
            if (!disbursementDataList.isEmpty()) {
                CorporateActionDisbursementDto disbursementData = disbursementDataList.get(0);
                if (disbursementData.getSecurityId() != null) {
                    SecuritiesMasterDto securitiesData = repository.findSecuritiesMasterBySecurityId(disbursementData.getSecurityId());
                    instrumentData = mapper.mapToInstrumentDto(securitiesData);
                }
            }

            // Build response with multiple cashflows
            return mapper.mapToCorporateActionResponse(
                    distributionDataList.get(0), // Use first distribution for messageId
                    cashflows,
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
        String timestamp = LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        
        return CorporateActionResponseDto.builder()
                .messageId(null)
                .messageTimestamp(timestamp)
                .requestingSystemId("CaSP")
                .data(DataDto.builder()
                        .cashflows(new ArrayList<>())
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

import java.util.List;

public interface CorporateActionMapper {
    CorporateActionResponseDto mapToCorporateActionResponse(
            CorporateActionDistributionDto firstDistributionData,
            List<CashflowDto> cashflows,
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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

@Component
public class CorporateActionMapperImpl implements CorporateActionMapper {

    @Override
    public CorporateActionResponseDto mapToCorporateActionResponse(
            CorporateActionDistributionDto firstDistributionData,
            List<CashflowDto> cashflows,
            AccountDto accountData,
            LegalEntityDto legalEntityData,
            String crdsId,
            InstrumentDto instrumentData) {

        // Create messageId from securities_position_id and transaction_id of first distribution
        String messageId = null;
        if (firstDistributionData != null) {
            String secPosId = firstDistributionData.getSecuritiesPositionId() != null ? 
                            firstDistributionData.getSecuritiesPositionId().toString() : "";
            String transId = firstDistributionData.getTransactionId() != null ? 
                           firstDistributionData.getTransactionId().toString() : "";
            messageId = secPosId + transId;
            if (messageId.isEmpty()) {
                messageId = null;
            }
        }

        // Create timestamp in IST format
        String timestamp = LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

        // Create party
        PartyDto party = PartyDto.builder()
                .systemAccountId(accountData != null ? accountData.getLegacyAccountId() : null)
                .systemId(null) // As per requirement, this is null
                .entityId(legalEntityData != null ? legalEntityData.getLegalEntityCode() : null)
                .crdSId(crdsId)
                .build();

        // Create data
        DataDto data = DataDto.builder()
                .cashflows(cashflows != null ? cashflows : new ArrayList<>())
                .party(party)
                .instrument(instrumentData != null ? instrumentData : InstrumentDto.builder().build())
                .additionalAttributes(new HashMap<>())
                .build();

        // Create main response
        return CorporateActionResponseDto.builder()
                .messageId(messageId)
                .messageTimestamp(timestamp)
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
        
        if (request.getTransactionId() == null) {
            log.error("Transaction ID is required");
            return ResponseEntity.badRequest().build();
        }
        
        CorporateActionResponseDto response = corporateActionService.getCorporateActionResponse(request.getTransactionId());
        
        log.info("Returning corporate action response for transaction ID: {}", request.getTransactionId());
        
        return ResponseEntity.ok(response);
    }
}
