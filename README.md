public interface TaxRequestRepository extends JpaRepository<CorporateActionDistribution, Long> {

    @Query("SELECT " +
           "cad.id AS distributionId, " +
           "cad.securitiesPositionId AS securitiesPositionId, " +
           "cad.transactionId AS transactionId, " +
           "tt.typeAbbrv AS transactionTypeAbbrv, " +
           "dis.paymentDate AS paymentDate, " +
           "acc.revenueProductCode AS revenueProductCode, " +
           "acc.legacyAccountId AS legacyAccountId, " +
           "le.legalEntityCode AS legalEntityCode, " +
           "org.crdsId AS crdsId, " +
           "sm.cusip AS cusip, " +
           "sm.isin AS isin, " +
           "cus.settlementDate AS settlementDate " +
           "FROM CorporateActionDistribution cad " +
           "JOIN CaspTransaction tx ON cad.transactionId = tx.transactionId " +
           "JOIN TransactionType tt ON tx.transactionTypeId = tt.transactionTypeId " +
           "LEFT JOIN CorporateActionDisbursement dis ON cad.transactionId = dis.transactionId " +
           "LEFT JOIN Account acc ON cad.accountId = acc.accountId " +
           "LEFT JOIN LegalEntity le ON acc.legalEntityId = le.legalEntityId " +
           "LEFT JOIN DealParty dp ON acc.dealId = dp.dealId " +
           "LEFT JOIN Organization org ON dp.orgId = org.orgId " +
           "LEFT JOIN SecuritiesMaster sm ON dis.securityId = sm.securityId " +
           "LEFT JOIN CustodyDisbursement cus ON cad.transactionId = cus.transactionId " +
           "WHERE cad.transactionId = :transactionId")
    List<TaxRequestProjection> fetchCaspData(@Param("transactionId") Long transactionId);
}

