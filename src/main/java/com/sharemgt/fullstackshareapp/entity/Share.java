package com.sharemgt.fullstackshareapp.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="share")
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_id")
    private int shareId;

    @Column(name = "share_name")
    private String shareName;

    @Column(name = "market_price")
    private int marketPrice;

    @Column(name = "issuedate")
    private LocalDate issueDate;

    public Share() {
    }

    public Share(int shareId, String shareName, int marketPrice, LocalDate issueDate) {
        this.shareId = shareId;
        this.shareName = shareName;
        this.marketPrice = marketPrice;
        this.issueDate = issueDate;
    }

    public int getShareId() {
        return shareId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    public int getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(int marketPrice) {
        this.marketPrice = marketPrice;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }
}
