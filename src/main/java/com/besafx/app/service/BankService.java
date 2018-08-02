package com.besafx.app.service;

import com.besafx.app.entity.Bank;
import com.besafx.app.entity.Branch;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface BankService extends PagingAndSortingRepository<Bank, Long>, JpaSpecificationExecutor<Bank> {
        List<Bank> findByBranch(Branch branch);
}
