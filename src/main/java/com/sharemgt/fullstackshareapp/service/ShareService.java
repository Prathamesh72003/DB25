package com.sharemgt.fullstackshareapp.service;

import com.sharemgt.fullstackshareapp.entity.Share;

import java.util.List;

public interface ShareService {
    List<Share> getAllShares();
    Share getShareById(int shareId);
    Share saveShare(Share share);
    boolean deleteShare(int shareId);
    Share updateShareMarketPriceById(int shareId, int marketPrice);
}
