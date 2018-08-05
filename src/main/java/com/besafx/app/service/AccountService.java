package com.besafx.app.service;

import com.besafx.app.entity.Account;
import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Course;
import com.besafx.app.entity.Student;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface AccountService extends PagingAndSortingRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    Account findTopByCourseMasterBranchOrderByCodeDesc(Branch branch);

    List<Account> findByStudent(Student student);

    List<Account> findByCourse(Course course);

    List<Account> findByCourseId(Long courseId);

    List<Account> findByCourseMasterBranch(Branch branch);

    List<Account> findByCourseMasterBranchId(Long branchId, Pageable pageable);

    List<Account> findByCourseMasterBranchIdIn(List<Long> branchIds);
}
