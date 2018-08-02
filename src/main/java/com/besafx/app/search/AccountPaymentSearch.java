package com.besafx.app.search;

import com.besafx.app.entity.AccountPayment;
import com.besafx.app.service.AccountPaymentService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AccountPaymentSearch {

    private static final Logger LOG = LoggerFactory.getLogger(AccountPaymentSearch.class);

    @Autowired
    private AccountPaymentService accountPaymentService;

    public Page<AccountPayment> filter(
            //AccountPayment Filters
            final Long codeFrom,
            final Long codeTo,
            final Long dateFrom,
            final Long dateTo,
            //Account Filters
            final String firstName,
            final String secondName,
            final String thirdName,
            final String forthName,
            final String studentIdentityNumber,
            final String studentMobile,
            final String filterCompareType,
            Pageable pageRequest) {

        List<Specification<AccountPayment>> predicates = new ArrayList<>();
        //AccountPayment Specification
        Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("date"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("date"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        //Account Specification
        Optional.ofNullable(firstName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("account").get("student").get("contact").get("firstName"), "%" + value.trim() + "%")));
        Optional.ofNullable(secondName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("account").get("student").get("contact").get("secondName"), "%" + value.trim() + "%")));
        Optional.ofNullable(thirdName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("account").get("student").get("contact").get("thirdName"), "%" + value.trim() + "%")));
        Optional.ofNullable(forthName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("account").get("student").get("contact").get("forthName"), "%" + value.trim() + "%")));
        Optional.ofNullable(studentIdentityNumber).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("account").get("student").get("contact").get("identityNumber"), "%" + value.trim() + "%")));
        Optional.ofNullable(studentMobile).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("account").get("student").get("contact").get("mobile"), "%" + value.trim() + "%")));

        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                if (filterCompareType == null) {
                    result = Specifications.where(result).and(predicates.get(i));
                    continue;
                }
                result = filterCompareType.equalsIgnoreCase("and") ?
                        Specifications.where(result).and(predicates.get(i)) :
                        Specifications.where(result).or(predicates.get(i));
            }
            return accountPaymentService.findAll(result, pageRequest);
        } else {
            return accountPaymentService.findAll(pageRequest);
        }
    }
}
