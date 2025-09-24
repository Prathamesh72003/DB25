@Entity
@Table(name = "corporate_action_distribution")
public class CorporateActionDistribution {
    @Id
    private Long id;

    @Column(name = "securities_position_id")
    private Long securitiesPositionId;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private CaspTransaction transaction;

    @ManyToOne
    @JoinColumn(name = "transaction_id", insertable = false, updatable = false)
    private CorporateActionDisbursement disbursement;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "transaction_id", insertable = false, updatable = false)
    private CustodyDisbursement custodyDisbursement;
}

---

@Entity
@Table(name = "corporate_action_disbursement")
public class CorporateActionDisbursement {
    @Id
    private Long id;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "security_id")
    private SecuritiesMaster security;
}

----

@Entity
@Table(name = "custody_disbursement")
public class CustodyDisbursement {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private CaspTransaction transaction;

    @Column(name = "settlement_date")
    private LocalDate settlementDate;
}

----

@Entity
@Table(name = "securities_master")
public class SecuritiesMaster {
    @Id
    @Column(name = "security_id")
    private Long securityId;

    @Column(name = "cusip")
    private String cusip;

    @Column(name = "isin")
    private String isin;
}
