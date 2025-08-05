package com.sharemgt.fullstackshareapp.repository;

import com.sharemgt.fullstackshareapp.entity.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareRepo extends JpaRepository<Share, Integer> {
}
