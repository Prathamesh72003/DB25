package com.sharemgt.fullstackshareapp.dto;

public class UpdateMarketPriceRequestDTO {

    private int shareId;
    private int marketPrice;

    public UpdateMarketPriceRequestDTO() {
    }

    public UpdateMarketPriceRequestDTO(int shareId, int marketPrice) {
        this.shareId = shareId;
        this.marketPrice = marketPrice;
    }

    public int getShareId() {
        return shareId;
    }

    public void setShareId(int shareId) {
        this.shareId = shareId;
    }

    public int getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(int marketPrice) {
        this.marketPrice = marketPrice;
    }
}
