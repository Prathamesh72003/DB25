public interface TaxRequestRepository extends JpaRepository<CorporateActionDistribution, Long> {

    @Query("SELECT " +
           "cad.id AS distributionId, " +
           "cad.securitiesPositionId AS securitiesPositionId, " +
           "cad.transaction.transactionId AS transactionId, " +
           "cad.transaction.transactionType.typeAbbrv AS transactionTypeAbbrv, " +
           "dis.paymentDate AS paymentDate, " +
           "acc.revenueProductCode AS revenueProductCode, " +
           "acc.legacyAccountId AS legacyAccountId, " +
           "le.legalEntityCode AS legalEntityCode, " +
           "org.crdsId AS crdsId, " +
           "sm.cusip AS cusip, " +
           "sm.isin AS isin, " +
           "cus.settlementDate AS settlementDate " +
           "FROM CorporateActionDistribution cad " +
           "JOIN cad.transaction tx " +
           "JOIN tx.transactionType tt " +
           "LEFT JOIN cad.disbursement dis " +
           "LEFT JOIN cad.account acc " +
           "LEFT JOIN acc.legalEntity le " +
           "LEFT JOIN acc.dealParty dp " +
           "LEFT JOIN dp.organization org " +
           "LEFT JOIN dis.security sm " +
           "LEFT JOIN cad.custodyDisbursement cus " +
           "WHERE cad.transaction.transactionId = :transactionId")
    List<TaxRequestProjection> fetchCaspData(@Param("transactionId") Long transactionId);
}
