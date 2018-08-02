package com.besafx.app.rest;

import com.besafx.app.async.TransactionalService;
import com.besafx.app.auditing.EntityHistoryListener;
import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.entity.Offer;
import com.besafx.app.entity.Person;
import com.besafx.app.search.OfferSearch;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.MasterService;
import com.besafx.app.service.OfferService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationDegree;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/offer/")
public class OfferRest {

    public static final String FILTER_TABLE = "" +
            "**," +
            "master[id,code,name]," +
            "person[id,contact[id,shortName]]," +
            "-calls";

    private final static Logger LOG = LoggerFactory.getLogger(OfferRest.class);

    @Autowired
    private BranchService branchService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private OfferSearch offerSearch;

    @Autowired
    private MasterService masterService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityHistoryListener entityHistoryListener;

    @Autowired
    private TransactionalService transactionalService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_OFFER_CREATE')")
    @Transactional
    public String create(@RequestBody Offer offer) {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        offer.setCode(transactionalService.getNextOfferCode(offer.getMaster().getBranch()));
        offer.setDate(new Date());
        offer.setPerson(caller);
        offer = offerService.save(offer);

        StringBuilder builder = new StringBuilder();
        builder.append("إنشاء العرض رقم ");
        builder.append(" ( " + offer.getCode() + " ) ");
        builder.append("، للعميل ");
        builder.append(" ( " + offer.getCustomerName() + " ) ");
        builder.append("، للتخصص ");
        builder.append(" ( " + offer.getMaster().getName() + " ) ");
        builder.append("، نوع الدفع ");
        builder.append(" ( " + offer.getPaymentTypeInArabic() + " ) ");
        builder.append("، بواسطة ");
        builder.append(caller.getContact().getShortName());
        notificationService.notifyAll(Notification
                                              .builder()
                                              .title("العمليات على العروض")
                                              .message(builder.toString())
                                              .type(NotificationDegree.success)
                                              .icon("add")
                                              .build());
        entityHistoryListener.perform(builder.toString());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offer);
    }

    @PutMapping(value = "update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_OFFER_UPDATE')")
    @Transactional
    public String update(@RequestBody Offer offer) {
        Offer object = offerService.findOne(offer.getId());
        if (object != null) {
            offer = offerService.save(offer);

            Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
            StringBuilder builder = new StringBuilder();
            builder.append("تعديل بيانات العرض رقم ");
            builder.append(" ( " + offer.getCode() + " ) ");
            builder.append("، للعميل ");
            builder.append(" ( " + offer.getCustomerName() + " ) ");
            builder.append("، للتخصص ");
            builder.append(" ( " + offer.getMaster().getName() + " ) ");
            builder.append("، نوع الدفع ");
            builder.append(" ( " + offer.getPaymentTypeInArabic() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على العروض")
                                                  .message(builder.toString())
                                                  .type(NotificationDegree.warning)
                                                  .icon("edit")
                                                  .build());
            entityHistoryListener.perform(builder.toString());

            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offer);
        } else {
            return null;
        }
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_OFFER_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        Offer offer = offerService.findOne(id);
        if (offer != null) {
            offerService.delete(id);

            Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
            StringBuilder builder = new StringBuilder();
            builder.append("حذف العرض رقم ");
            builder.append(" ( " + offer.getCode() + " ) ");
            builder.append("، للعميل ");
            builder.append(" ( " + offer.getCustomerName() + " ) ");
            builder.append("، للتخصص ");
            builder.append(" ( " + offer.getMaster().getName() + " ) ");
            builder.append("، نوع الدفع ");
            builder.append(" ( " + offer.getPaymentTypeInArabic() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على العروض")
                                                  .message(builder.toString())
                                                  .type(NotificationDegree.error)
                                                  .icon("delete")
                                                  .build());
            entityHistoryListener.perform(builder.toString());
        }
    }

    @GetMapping(value = "findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(offerService.findAll()));
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offerService.findOne(id));
    }

    @GetMapping(value = "findByCustomerMobile/{customerMobile}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByCustomerMobile(@PathVariable String customerMobile) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offerService.findByCustomerMobile(customerMobile));
    }

    @GetMapping(value = "findByCustomerMobileAndCodeIsNot/{customerMobile}/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByCustomerMobileAndCodeIsNot(@PathVariable String customerMobile, @PathVariable Integer code) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offerService.findByCustomerMobileAndCodeIsNot
                (customerMobile, code));
    }

    @GetMapping(value = "findByBranch/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByBranch(@PathVariable(value = "branchId") Long branchId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offerService.findByMasterBranch(branchService.findOne
                (branchId)));
    }

    @GetMapping(value = "findByMaster/{masterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByMaster(@PathVariable(value = "masterId") Long masterId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), offerService.findByMaster(masterService.findOne(masterId)));
    }

    @GetMapping(value = "findCustomersByBranch/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findCustomersByBranch(@PathVariable(value = "branchId") Long branchId) {
        List<String> list = offerService
                .findByMasterBranch(branchService.findOne(branchId))
                .stream()
                .map(value -> value.getCustomerName())
                .distinct()
                .collect(Collectors.toList());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), list);
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            @RequestParam(value = "codeFrom", required = false) final Long codeFrom,
            @RequestParam(value = "codeTo", required = false) final Long codeTo,
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            @RequestParam(value = "customerName", required = false) final String customerName,
            @RequestParam(value = "customerIdentityNumber", required = false) final String customerIdentityNumber,
            @RequestParam(value = "customerMobile", required = false) final String customerMobile,
            @RequestParam(value = "priceFrom", required = false) final Long priceFrom,
            @RequestParam(value = "priceTo", required = false) final Long priceTo,
            @RequestParam(value = "branch", required = false) final Long branch,
            @RequestParam(value = "master", required = false) final Long master,
            @RequestParam(value = "personId", required = false) final Long personId,
            Pageable pageable) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), "**,".concat("content[").concat(FILTER_TABLE).concat("]")),
                                       offerSearch.filter(
                                               codeFrom,
                                               codeTo,
                                               dateFrom,
                                               dateTo,
                                               customerName,
                                               customerIdentityNumber,
                                               customerMobile,
                                               priceFrom,
                                               priceTo,
                                               branch,
                                               master,
                                               personId,
                                               pageable));
    }
}
