package com.besafx.app.service;

import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Master;
import com.besafx.app.entity.Offer;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public interface OfferService extends PagingAndSortingRepository<Offer, Long>, JpaSpecificationExecutor<Offer> {

    Offer findTopByMasterBranchOrderByCodeDesc(Branch branch);

    List<Offer> findByCustomerMobile(String customerMobile);

    List<Offer> findByCustomerMobileAndCodeIsNot(String customerMobile, Integer code);

    List<Offer> findByMasterBranch(Branch branch);

    List<Offer> findByMasterBranchAndWrittenDateBetween(Branch branch, @Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);

    List<Offer> findByMaster(Master master);
}
