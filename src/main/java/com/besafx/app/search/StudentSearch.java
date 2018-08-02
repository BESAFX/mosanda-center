package com.besafx.app.search;

import com.besafx.app.entity.Student;
import com.besafx.app.service.StudentService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class StudentSearch {

    private final static Logger LOG = LoggerFactory.getLogger(StudentSearch.class);

    @Autowired
    private StudentService studentService;

    public Page<Student> filter(
            final String firstName,
            final String secondName,
            final String thirdName,
            final String forthName,
            final String fullName,
            final Long registerDateFrom,
            final Long registerDateTo,
            final String studentIdentityNumber,
            final String studentMobile,
            final Long branchId,
            final String filterCompareType,
            final Pageable pageRequest) {

        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(firstName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("contact").get("firstName"), "%" + value.trim() + "%")));
        Optional.ofNullable(secondName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("contact").get("secondName"), "%" + value.trim() + "%")));
        Optional.ofNullable(thirdName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("contact").get("thirdName"), "%" + value.trim() + "%")));
        Optional.ofNullable(forthName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("contact").get("forthName"), "%" + value.trim() + "%")));
        Optional.ofNullable(fullName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.<String>get("contact").get("fullName"), "%" + value.trim() + "%")));
        Optional.ofNullable(registerDateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("registerDate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(registerDateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("registerDate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(studentIdentityNumber).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("contact").get("identityNumber"), "%" + value.trim() + "%")));
        Optional.ofNullable(studentMobile).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("contact").get("mobile"), "%" + value.trim() + "%")));
        Optional.ofNullable(branchId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("branch").get("id"), value)));

        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                if (filterCompareType == null) {
                    result = Specifications.where(result).and(predicates.get(i));
                    continue;
                }
                result = filterCompareType.equalsIgnoreCase("and") ? Specifications.where(result).and(predicates.get(i)) : Specifications.where
                        (result).or(predicates.get(i));
            }
            return studentService.findAll(result, pageRequest);
        } else {
            return new PageImpl<>(new ArrayList<>());
        }
    }
}
