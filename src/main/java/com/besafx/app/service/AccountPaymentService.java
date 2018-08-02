package com.besafx.app.service;

import com.besafx.app.entity.AccountPayment;
import com.besafx.app.entity.Branch;
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
public interface AccountPaymentService extends PagingAndSortingRepository<AccountPayment, Long>, JpaSpecificationExecutor<AccountPayment> {

    AccountPayment findTopByAccountCourseMasterBranchOrderByCodeDesc(Branch branch);

    AccountPayment findByCodeAndIdIsNot(Integer code, Long id);

    List<AccountPayment> findByAccountId(Long id);

    List<AccountPayment> findByDateBetween(@Temporal(TemporalType.TIMESTAMP) Date startDate, @Temporal(TemporalType.TIMESTAMP) Date endDate);
}
