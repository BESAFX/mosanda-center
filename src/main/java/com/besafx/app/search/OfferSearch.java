package com.besafx.app.search;

import com.besafx.app.entity.Offer;
import com.besafx.app.service.AccountService;
import com.besafx.app.service.OfferService;
import com.google.common.collect.Lists;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class OfferSearch {

    private final static Logger log = LoggerFactory.getLogger(OfferSearch.class);

    @Autowired
    private OfferService offerService;

    public Page<Offer> filter(
            final Long codeFrom,
            final Long codeTo,
            final Long dateFrom,
            final Long dateTo,
            final String customerName,
            final String customerIdentityNumber,
            final String customerMobile,
            final Long priceFrom,
            final Long priceTo,
            final Long branch,
            final Long master,
            final Long personId,
            final Pageable pageRequest) {
        List<Specification> predicates = new ArrayList<>();
        Optional.ofNullable(codeFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(codeTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("code"), value)));
        Optional.ofNullable(dateFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("lastUpdate"), new DateTime(value).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(dateTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("lastUpdate"), new DateTime(value).plusDays(1).withTimeAtStartOfDay().toDate())));
        Optional.ofNullable(customerName).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("customerName"), "%" + value + "%")));
        Optional.ofNullable(customerIdentityNumber).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("customerIdentityNumber"), "%" + value + "%")));
        Optional.ofNullable(customerMobile).ifPresent(value -> predicates.add((root, cq, cb) -> cb.like(root.get("customerMobile"), "%" + value + "%")));
        Optional.ofNullable(priceFrom).ifPresent(value -> predicates.add((root, cq, cb) -> cb.greaterThanOrEqualTo(root.get("price"), value)));
        Optional.ofNullable(priceTo).ifPresent(value -> predicates.add((root, cq, cb) -> cb.lessThanOrEqualTo(root.get("price"), value)));
        Optional.ofNullable(branch).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("master").get("branch").get("id"), value)));
        Optional.ofNullable(master).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("master").get("id"), value)));
        Optional.ofNullable(personId).ifPresent(value -> predicates.add((root, cq, cb) -> cb.equal(root.get("lastPerson").get("id"), value)));
        if (!predicates.isEmpty()) {
            Specification result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = Specifications.where(result).and(predicates.get(i));
            }
            return offerService.findAll(result, pageRequest);
        } else {
            return new PageImpl<>(new ArrayList<>());
        }
    }
}
