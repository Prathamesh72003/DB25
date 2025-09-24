@Entity
@Table(name = "casp_transaction")
public class CaspTransaction {
    @Id
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "transaction_type_id")
    private TransactionType transactionType;
}

@Entity
@Table(name = "transaction_type")
public class TransactionType {
    @Id
    @Column(name = "transaction_type_id")
    private Long transactionTypeId;

    @Column(name = "type_abbrv")
    private String typeAbbrv;
}
@Entity
@Table(name = "account")
public class Account {
    @Id
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "revenue_product_code")
    private String revenueProductCode;

    @Column(name = "legacy_account_id")
    private String legacyAccountId;

    @ManyToOne
    @JoinColumn(name = "legal_entity_id")
    private LegalEntity legalEntity;

    @ManyToOne
    @JoinColumn(name = "deal_id")
    private DealParty dealParty;
}


@Entity
@Table(name = "legal_entity")
public class LegalEntity {
    @Id
    @Column(name = "legal_entity_id")
    private Long legalEntityId;

    @Column(name = "legal_entity_code")
    private String legalEntityCode;
}


@Entity
@Table(name = "deal_party")
public class DealParty {
    @Id
    @Column(name = "deal_id")
    private Long dealId;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private Organization organization;
}


@Entity
@Table(name = "org")
public class Organization {
    @Id
    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "crds_id")
    private String crdsId;
}
