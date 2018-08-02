package com.besafx.app.rest;

import com.besafx.app.auditing.EntityHistoryListener;
import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.BankTransaction;
import com.besafx.app.entity.AccountPayment;
import com.besafx.app.entity.Person;
import com.besafx.app.init.Initializer;
import com.besafx.app.search.AccountPaymentSearch;
import com.besafx.app.service.BankTransactionService;
import com.besafx.app.service.AccountPaymentService;
import com.besafx.app.util.DateConverter;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationDegree;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping(value = "/api/accountPayment/")
public class AccountPaymentRest {

    private final static Logger LOG = LoggerFactory.getLogger(AccountPaymentRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "account[id,contact[id,shortName]]," +
            "-bankTransaction," +
            "person[id,contact[id,shortName]]";

    @Autowired
    private AccountPaymentService accountPaymentService;

    @Autowired
    private AccountPaymentSearch accountPaymentSearch;

    @Autowired
    private BankTransactionService bankTransactionService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityHistoryListener entityHistoryListener;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_PAYMENT_CREATE')")
    @Transactional
    public String create(@RequestBody AccountPayment accountPayment) {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        accountPayment.setPerson(caller);

        if (accountPayment.getAmount() > 0) {
            LOG.info("WITHDRAW REQUIRED AMOUNT FROM BANK ACCOUNT...");
            BankTransaction bankTransaction = accountPayment.getBankTransaction();
            bankTransaction.setAmount(accountPayment.getAmount());
            bankTransaction.setTransactionType(Initializer.transactionTypeDeposit);
            bankTransaction.setDate(accountPayment.getDate());
            bankTransaction.setPerson(caller);
            StringBuilder builder = new StringBuilder();
            builder.append("إيداع مبلغ نقدي بقيمة ");
            builder.append(bankTransaction.getAmount());
            builder.append("ريال سعودي، ");
            builder.append(" إلى الحساب / ");
            builder.append(bankTransaction.getBank().getName());
            builder.append(" التابع للفرع / ");
            builder.append(bankTransaction.getBank().getBranch().getName());
            builder.append("، دفعة مالية بتاريخ ");
            builder.append(DateConverter.getDateInFormat(bankTransaction.getDate()));
            builder.append("، من الطالب / ");
            builder.append(accountPayment.getAccount().getStudent().getContact().getShortName());
            builder.append("، ");
            builder.append(accountPayment.getNote());
            bankTransaction.setNote(builder.toString());

            accountPayment.setBankTransaction(bankTransactionService.save(bankTransaction));
            accountPayment = accountPaymentService.save(accountPayment);

            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على سداد رسوم التسجيل")
                                                  .message(builder.toString())
                                                  .type(NotificationDegree.success)
                                                  .icon("add")
                                                  .build());
            entityHistoryListener.perform(builder.toString());
        }else{
            throw new CustomException("لا يمكن قبول القيمة الصفرية للسند");
        }
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountPayment);
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_PAYMENT_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        AccountPayment accountPayment = accountPaymentService.findOne(id);
        if (accountPayment != null) {
            LOG.info("DELETE BANK TRANSACTION RELATED WITH THIS PAYMENT...");
            bankTransactionService.delete(accountPayment.getBankTransaction());
            LOG.info("DELETE THIS PAYMENT...");
            accountPaymentService.delete(id);

            StringBuilder builder = new StringBuilder();
            builder.append("حذف سند القبض رقم / ");
            builder.append(accountPayment.getCode());
            builder.append("، بقيمة : ");
            builder.append(accountPayment.getAmount());
            builder.append("ريال سعودي، ");
            builder.append("، بتاريخ ");
            builder.append(DateConverter.getDateInFormat(accountPayment.getDate()));
            builder.append("، للطالب / ");
            builder.append(accountPayment.getAccount().getStudent().getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على سداد رسوم التسجيل")
                                                  .message(builder.toString())
                                                  .type(NotificationDegree.error)
                                                  .icon("delete")
                                                  .build());
            entityHistoryListener.perform(builder.toString());
        }
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       accountPaymentService.findOne(id));
    }

    @GetMapping(value = "findByAccount/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByAccount(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       accountPaymentService.findByAccountId(id));
    }

    @GetMapping(value = "findByDateBetween/{startDate}/{endDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByDateBetween(@PathVariable(value = "startDate") Long startDate, @PathVariable(value = "endDate") Long endDate) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       accountPaymentService.findByDateBetween(
                                               new DateTime(startDate).withTimeAtStartOfDay().toDate(),
                                               new DateTime(endDate).plusDays(1).withTimeAtStartOfDay().toDate()
                                                                                   ));
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            //AccountPayment Filters
            @RequestParam(value = "codeFrom", required = false) final Long codeFrom,
            @RequestParam(value = "codeTo", required = false) final Long codeTo,
            @RequestParam(value = "dateFrom", required = false) final Long dateFrom,
            @RequestParam(value = "dateTo", required = false) final Long dateTo,
            //Account Filters
            @RequestParam(value = "firstName", required = false) final String firstName,
            @RequestParam(value = "secondName", required = false) final String secondName,
            @RequestParam(value = "thirdName", required = false) final String thirdName,
            @RequestParam(value = "forthName", required = false) final String forthName,
            @RequestParam(value = "studentIdentityNumber", required = false) final String studentIdentityNumber,
            @RequestParam(value = "studentMobile", required = false) final String studentMobile,
            @RequestParam(value = "filterCompareType", required = false) final String filterCompareType,
            Pageable pageable) {
        return SquigglyUtils.stringify(
                Squiggly.init(
                        new ObjectMapper(),
                        "**,".concat("content[").concat(FILTER_TABLE).concat("]")),
                accountPaymentSearch.filter(
                        codeFrom,
                        codeTo,
                        dateFrom,
                        dateTo,
                        firstName,
                        secondName,
                        thirdName,
                        forthName,
                        studentIdentityNumber,
                        studentMobile,
                        filterCompareType,
                        pageable));
    }
}
