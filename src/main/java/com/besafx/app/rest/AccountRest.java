package com.besafx.app.rest;

import com.besafx.app.async.TransactionalService;
import com.besafx.app.auditing.EntityHistoryListener;
import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.entity.Account;
import com.besafx.app.entity.Person;
import com.besafx.app.search.AccountSearch;
import com.besafx.app.service.*;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationDegree;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/account/")
public class AccountRest {

    public static final String FILTER_TABLE = "" +
            "**," +
            "person[id,contact[id,shortName]]," +
            "course[id,code,master[id,code,name,branch[id,code,name]]]," +
            "student[id,contact[**]]," +
            "accountPayments[**,-account,-bankTransaction,person[id,contact[id,shortName]]]," +
            "accountAttaches[**,attach[**,person[id,contact[id,shortName]]]]," +
            "accountConditions[**,person[id,contact[id,shortName]]]," +
            "accountNotes[**,person[id,contact[id,shortName]]]";

    public static final String FILTER_ACCOUNT_COMBO = "" +
            "id," +
            "code," +
            "code," +
            "name," +
            "registerDate," +
            "net," +
            "paid," +
            "remain," +
            "lastPaymentDate," +
            "course[id,code,master[id,code,name,branch[id,code,name]]]," +
            "student[id,contact[id,shortName,fullName,mobile,identityNumber]]";

    public static final String FILTER_ACCOUNT_ATTACH = "" +
            "id," +
            "code," +
            "name," +
            "course[id,master[id,name]]," +
            "student[id,contact[id,mobile,identityNumber]]," +
            "accountAttaches[**,attach[**,person[id,contact[id,shortName]]],-account]";

    public static final String FILTER_ACCOUNT_KEY = "" +
            "id," +
            "code," +
            "name," +
            "remain";

    private final static Logger LOG = LoggerFactory.getLogger(AccountRest.class);

    @Autowired
    private BranchService branchService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountPaymentService accountPaymentService;

    @Autowired
    private AccountAttachService accountAttachService;

    @Autowired
    private AccountConditionService accountConditionService;

    @Autowired
    private AccountNoteService accountNoteService;

    @Autowired
    private AccountSearch accountSearch;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityHistoryListener entityHistoryListener;

    @Autowired
    private TransactionalService transactionalService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_CREATE')")
    @Transactional
    public String create(@RequestBody Account account) {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        account.setCode(transactionalService.getNextAccountCode(account.getCourse().getMaster().getBranch()));
        account.setPerson(caller);
        account = accountService.save(account);

        StringBuilder builder = new StringBuilder();
        builder.append("انشاء تسجيل جديد رقم ");
        builder.append(" ( " + account.getCode() + " ) ");
        builder.append("،للطالب / ");
        builder.append(transactionalService.getContactFullName(account.getStudent().getContact()));
        builder.append("، رقم الجوال ");
        builder.append(" ( " + account.getStudent().getContact().getMobile() + " ) ");
        builder.append("، رقم البطاقة ");
        builder.append(" ( " + account.getStudent().getContact().getIdentityLocation() + " ) ");
        builder.append("، التخصص المطلوب ");
        builder.append(" ( " + account.getCourse().getMaster().getName() + " ) ");
        builder.append("، باجمالي سعر ");
        builder.append(" ( " + account.getNet() + " ) ");
        builder.append("، بواسطة ");
        builder.append(caller.getContact().getShortName());
        notificationService.notifyAll(Notification
                                              .builder()
                                              .title("العمليات على التسجيل")
                                              .message(builder.toString())
                                              .type(NotificationDegree.success)
                                              .icon("add")
                                              .build());
        entityHistoryListener.perform(builder.toString());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), account);
    }

    @PutMapping(value = "update",  consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_UPDATE')")
    @Transactional
    public String update(@RequestBody Account account) {
        Account object = accountService.findOne(account.getId());
        if (object != null) {
            Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
            account.setPerson(caller);
            account = accountService.save(account);

            StringBuilder builder = new StringBuilder();
            builder.append("تعديل التسجيل رقم ");
            builder.append(" ( " + account.getCode() + " ) ");
            builder.append("،للطالب / ");
            builder.append(transactionalService.getContactFullName(account.getStudent().getContact()));
            builder.append("، رقم الجوال ");
            builder.append(" ( " + account.getStudent().getContact().getMobile() + " ) ");
            builder.append("، رقم البطاقة ");
            builder.append(" ( " + account.getStudent().getContact().getIdentityLocation() + " ) ");
            builder.append("، التخصص ");
            builder.append(" ( " + account.getCourse().getMaster().getName() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على التسجيل")
                                                  .message(builder.toString())
                                                  .type(NotificationDegree.warning)
                                                  .icon("edit")
                                                  .build());
            entityHistoryListener.perform(builder.toString());

            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), account);
        } else {
            return null;
        }
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        Account account = accountService.findOne(id);
        if (account != null) {

            LOG.info("DELETE ALL ATTACHES RELATED WITH THIS ACCOUNT");
            accountAttachService.delete(account.getAccountAttaches());

            LOG.info("DELETE ALL CONDITIONS RELATED WITH THIS ACCOUNT");
            accountConditionService.delete(account.getAccountConditions());

            LOG.info("DELETE ALL NOTES RELATED WITH THIS ACCOUNT");
            accountNoteService.delete(account.getAccountNotes());

            LOG.info("DELETE ALL PAYMENTS RELATED WITH THIS ACCOUNT");
            accountPaymentService.delete(account.getAccountPayments());

            LOG.info("FINALLY DELETE THIS ACCOUNT");
            accountService.delete(id);

            Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
            StringBuilder builder = new StringBuilder();
            builder.append("حذف التسجيل رقم ");
            builder.append(" ( " + account.getCode() + " ) ");
            builder.append("،للطالب / ");
            builder.append(transactionalService.getContactFullName(account.getStudent().getContact()));
            builder.append("، رقم الجوال ");
            builder.append(" ( " + account.getStudent().getContact().getMobile() + " ) ");
            builder.append("، رقم البطاقة ");
            builder.append(" ( " + account.getStudent().getContact().getIdentityLocation() + " ) ");
            builder.append("، التخصص ");
            builder.append(" ( " + account.getCourse().getMaster().getName() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على التسجيل")
                                                  .message(builder.toString())
                                                  .type(NotificationDegree.error)
                                                  .icon("delete")
                                                  .build());
            entityHistoryListener.perform(builder.toString());
        }
    }

    @DeleteMapping(value = "deleteByCourse/{courseId}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ACCOUNT_DELETE')")
    @Transactional
    public void deleteByCourse(@PathVariable Long courseId) {
        List<Account> accounts = accountService.findByCourseId(courseId);

        LOG.info("DELETE ALL ATTACHES RELATED WITH THIS ACCOUNT");
        accountAttachService.delete(accounts.stream()
                                            .flatMap(account -> account.getAccountAttaches().stream())
                                            .collect(Collectors.toList()));

        LOG.info("DELETE ALL CONDITIONS RELATED WITH THIS ACCOUNT");
        accountConditionService.delete(accounts.stream()
                                               .flatMap(account -> account.getAccountConditions().stream())
                                               .collect(Collectors.toList()));

        LOG.info("DELETE ALL NOTES RELATED WITH THIS ACCOUNT");
        accountNoteService.delete(accounts.stream()
                                          .flatMap(account -> account.getAccountNotes().stream())
                                          .collect(Collectors.toList()));

        LOG.info("DELETE ALL PAYMENTS RELATED WITH THIS ACCOUNT");
        accountPaymentService.delete(accounts.stream()
                                             .flatMap(account -> account.getAccountPayments().stream())
                                             .collect(Collectors.toList()));

        LOG.info("FINALLY DELETE THIS ACCOUNT");
        accountService.delete(accounts);

        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        StringBuilder builder = new StringBuilder();
        builder.append("حذف تسجيلات الدورة رقم ");
        builder.append(" ( " + courseId + " ) ");
        builder.append("،عدد التسجيلات المحذوفة ");
        builder.append(" ( " + accounts.size() + " ) ");
        builder.append("، بواسطة ");
        builder.append(caller.getContact().getShortName());
        notificationService.notifyAll(Notification
                                              .builder()
                                              .title("العمليات على التسجيل")
                                              .message(builder.toString())
                                              .type(NotificationDegree.error)
                                              .icon("delete")
                                              .build());
        entityHistoryListener.perform(builder.toString());
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountService.findOne(id));
    }

    @GetMapping(value = "findByStudent/{studentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByStudent(@PathVariable Long studentId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountService.findByStudent(studentService.findOne(studentId)));
    }

    @GetMapping(value = "findByCourse/{courseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByCourse(@PathVariable Long courseId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), accountService.findByCourse(courseService.findOne(courseId)));
    }

    @GetMapping(value = "findByBranch/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByBranch(@PathVariable Long branchId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_ACCOUNT_COMBO),
                accountService.findByCourseMasterBranch(branchService.findOne(branchId)));
    }

    @GetMapping(value = "findByBranchWithKey/{branchId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByBranchWithKey(@PathVariable Long branchId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_ACCOUNT_KEY),
                accountService.findByCourseMasterBranchId(branchId, new PageRequest(0, 10)));
    }

    @GetMapping(value = "findByBranches", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByBranches(@RequestParam List<Long> branchIds) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_ACCOUNT_COMBO),
                accountService.findByCourseMasterBranchIdIn(branchIds));
    }

    @GetMapping(value = "fetchTableData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchTableData() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                caller.getBranch().getMasters().stream()
                        .flatMap(master -> master.getCourses().stream())
                        .collect(Collectors.toList())
                        .stream()
                        .flatMap(course -> course.getAccounts().stream())
                        .collect(Collectors.toList()));
    }

    @GetMapping(value = "fetchTableDataAccountComboBox", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchTableDataAccountComboBox() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_ACCOUNT_COMBO),
                transactionalService.getPersonBranches(caller).stream()
                        .flatMap(branch -> branch.getMasters().stream())
                        .collect(Collectors.toList())
                        .stream()
                        .flatMap(master -> master.getCourses().stream())
                        .collect(Collectors.toList())
                        .stream()
                        .flatMap(course -> course.getAccounts().stream())
                        .collect(Collectors.toList()));
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            @RequestParam(value = "firstName", required = false) final String firstName,
            @RequestParam(value = "secondName", required = false) final String secondName,
            @RequestParam(value = "thirdName", required = false) final String thirdName,
            @RequestParam(value = "forthName", required = false) final String forthName,
            @RequestParam(value = "fullName", required = false) final String fullName,
            @RequestParam(value = "registerDateFrom", required = false) final Long registerDateFrom,
            @RequestParam(value = "registerDateTo", required = false) final Long registerDateTo,
            @RequestParam(value = "studentIdentityNumber", required = false) final String studentIdentityNumber,
            @RequestParam(value = "studentMobile", required = false) final String studentMobile,
            @RequestParam(value = "coursePriceFrom", required = false) final Long coursePriceFrom,
            @RequestParam(value = "coursePriceTo", required = false) final Long coursePriceTo,
            @RequestParam(value = "courseIds", required = false) final List<Long> courseIds,
            @RequestParam(value = "masterIds", required = false) final List<Long> masterIds,
            @RequestParam(value = "branchIds", required = false) final List<Long> branchIds,
            @RequestParam(value = "courseCodes", required = false) final List<Integer> courseCodes,
            @RequestParam(value = "masterCodes", required = false) final List<Integer> masterCodes,
            @RequestParam(value = "branchCodes", required = false) final List<Integer> branchCodes,
            @RequestParam(value = "filterCompareType") final String filterCompareType,
            Pageable pageable) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(),
                "**,".concat("content[").concat(FILTER_TABLE).concat("]")),
                accountSearch.search(
                        firstName,
                        secondName,
                        thirdName,
                        forthName,
                        fullName,
                        registerDateFrom,
                        registerDateTo,
                        studentIdentityNumber,
                        studentMobile,
                        coursePriceFrom,
                        coursePriceTo,
                        courseIds,
                        masterIds,
                        branchIds,
                        courseCodes,
                        masterCodes,
                        branchCodes,
                        filterCompareType,
                        pageable));
    }

    @GetMapping(value = "filterWithInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filterWithInfo(
            @RequestParam(value = "firstName", required = false) final String firstName,
            @RequestParam(value = "secondName", required = false) final String secondName,
            @RequestParam(value = "thirdName", required = false) final String thirdName,
            @RequestParam(value = "forthName", required = false) final String forthName,
            @RequestParam(value = "fullName", required = false) final String fullName,
            @RequestParam(value = "registerDateFrom", required = false) final Long registerDateFrom,
            @RequestParam(value = "registerDateTo", required = false) final Long registerDateTo,
            @RequestParam(value = "studentIdentityNumber", required = false) final String studentIdentityNumber,
            @RequestParam(value = "studentMobile", required = false) final String studentMobile,
            @RequestParam(value = "coursePriceFrom", required = false) final Long coursePriceFrom,
            @RequestParam(value = "coursePriceTo", required = false) final Long coursePriceTo,
            @RequestParam(value = "courseIds", required = false) final List<Long> courseIds,
            @RequestParam(value = "masterIds", required = false) final List<Long> masterIds,
            @RequestParam(value = "branchIds", required = false) final List<Long> branchIds,
            @RequestParam(value = "courseCodes", required = false) final List<Integer> courseCodes,
            @RequestParam(value = "masterCodes", required = false) final List<Integer> masterCodes,
            @RequestParam(value = "branchCodes", required = false) final List<Integer> branchCodes,
            @RequestParam(value = "filterCompareType") final String filterCompareType,
            Pageable pageable) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), "**,".concat("content[").concat(FILTER_ACCOUNT_COMBO).concat("]")),
                accountSearch.search(
                        firstName,
                        secondName,
                        thirdName,
                        forthName,
                        fullName,
                        registerDateFrom,
                        registerDateTo,
                        studentIdentityNumber,
                        studentMobile,
                        coursePriceFrom,
                        coursePriceTo,
                        courseIds,
                        masterIds,
                        branchIds,
                        courseCodes,
                        masterCodes,
                        branchCodes,
                        filterCompareType,
                        pageable));
    }

    @GetMapping(value = "filterWithAttaches", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filterWithAttaches(
            @RequestParam(value = "firstName", required = false) final String firstName,
            @RequestParam(value = "secondName", required = false) final String secondName,
            @RequestParam(value = "thirdName", required = false) final String thirdName,
            @RequestParam(value = "forthName", required = false) final String forthName,
            @RequestParam(value = "fullName", required = false) final String fullName,
            @RequestParam(value = "registerDateFrom", required = false) final Long registerDateFrom,
            @RequestParam(value = "registerDateTo", required = false) final Long registerDateTo,
            @RequestParam(value = "studentIdentityNumber", required = false) final String studentIdentityNumber,
            @RequestParam(value = "studentMobile", required = false) final String studentMobile,
            @RequestParam(value = "coursePriceFrom", required = false) final Long coursePriceFrom,
            @RequestParam(value = "coursePriceTo", required = false) final Long coursePriceTo,
            @RequestParam(value = "courseIds", required = false) final List<Long> courseIds,
            @RequestParam(value = "masterIds", required = false) final List<Long> masterIds,
            @RequestParam(value = "branchIds", required = false) final List<Long> branchIds,
            @RequestParam(value = "courseCodes", required = false) final List<Integer> courseCodes,
            @RequestParam(value = "masterCodes", required = false) final List<Integer> masterCodes,
            @RequestParam(value = "branchCodes", required = false) final List<Integer> branchCodes,
            @RequestParam(value = "filterCompareType") final String filterCompareType,
            Pageable pageable) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), "**,".concat("content[").concat(FILTER_ACCOUNT_ATTACH).concat("]")),
                accountSearch.search(
                        firstName,
                        secondName,
                        thirdName,
                        forthName,
                        fullName,
                        registerDateFrom,
                        registerDateTo,
                        studentIdentityNumber,
                        studentMobile,
                        coursePriceFrom,
                        coursePriceTo,
                        courseIds,
                        masterIds,
                        branchIds,
                        courseCodes,
                        masterCodes,
                        branchCodes,
                        filterCompareType,
                        pageable));
    }


}
