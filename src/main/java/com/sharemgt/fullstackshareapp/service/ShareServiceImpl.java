package com.sharemgt.fullstackshareapp.service;

import com.sharemgt.fullstackshareapp.entity.Share;
import com.sharemgt.fullstackshareapp.exception.NoSuchShareException;
import com.sharemgt.fullstackshareapp.exception.ShareAlreadyExsistException;
import com.sharemgt.fullstackshareapp.repository.ShareRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShareServiceImpl implements ShareService {

    @Autowired
    private ShareRepo shareRepo;

    @Override
    public List<Share> getAllShares() {
        return shareRepo.findAll().stream().collect(Collectors.toList());
    }

    @Override
    public Share getShareById(int shareId) {
        Optional<Share> opShare = shareRepo.findById(shareId);
        if(opShare.isPresent()){
            return opShare.get();
        }
        throw new NoSuchShareException("No Share Exist with this id!");
    }

    @Override
    public Share saveShare(Share share) {
        Optional<Share> optShare = shareRepo.findById(share.getShareId());
        if(optShare.isPresent()){
            throw new ShareAlreadyExsistException("Share with this id already exsists!");
        }else{
            Share newShare = shareRepo.save(share);
            return newShare;
        }
    }

    @Override
    public boolean deleteShare(int shareId) {
        Optional<Share> optShare = shareRepo.findById(shareId);
        if (optShare.isPresent()){
            shareRepo.deleteById(shareId);
            return true;
        }
        throw new NoSuchShareException("No Share Exist with this id!");
    }

    @Override
    public Share updateShareMarketPriceById(int shareId, int marketPrice) {
        Optional<Share> optShare = shareRepo.findById(shareId);
        if (optShare.isPresent()){
            Share shareWithNewMarketPrice = optShare.get();
            shareWithNewMarketPrice.setMarketPrice(marketPrice);
            return shareRepo.save(shareWithNewMarketPrice);
        }
        throw new NoSuchShareException("No Share Exist with this id!");
    }
}
