package com.besafx.app.rest;

import com.besafx.app.async.TransactionalService;
import com.besafx.app.auditing.EntityHistoryListener;
import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Account;
import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Master;
import com.besafx.app.entity.Person;
import com.besafx.app.search.MasterSearch;
import com.besafx.app.service.*;
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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/master/")
public class MasterRest {

    private final static Logger LOG = LoggerFactory.getLogger(MasterRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "-offers," +
            "branch[id,code,name]," +
            "lastPerson[id,contact[id,shortName]]," +
            "masterCategory[id,code,name]," +
            "courses[id]";

    private final String FILTER_MASTER_COMBO = "" +
            "id," +
            "code," +
            "name";

    private final String FILTER_MASTER_BRANCH_COMBO = "" +
            "id," +
            "code," +
            "name," +
            "branch[id,code,name]";

    private final String FILTER_MASTER_COURSE_COMBO = "" +
            "id," +
            "code," +
            "name," +
            "courses[id,code,instructor]";

    private final String FILTER_BRANCH_MASTER_COURSE_COMBO = "" +
            "id," +
            "code," +
            "name," +
            "branch[id,code,name]," +
            "courses[id,code,instructor]";

    @Autowired
    private BranchService branchService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private MasterSearch masterSearch;

    @Autowired
    private OfferService offerService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountAttachService accountAttachService;

    @Autowired
    private AccountConditionService accountConditionService;

    @Autowired
    private AccountNoteService accountNoteService;

    @Autowired
    private AccountPaymentService accountPaymentService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityHistoryListener entityHistoryListener;

    @Autowired
    private TransactionalService transactionalService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MASTER_CREATE')")
    public String create(@RequestBody Master master) {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        Master topMaster = masterService.findTopByBranchOrderByCodeDesc(master.getBranch());
        if (topMaster == null) {
            master.setCode(1);
        } else {
            master.setCode(topMaster.getCode() + 1);
        }
        master.setLastUpdate(new Date());
        master.setLastPerson(caller);
        master = masterService.save(master);

        StringBuilder builder = new StringBuilder();
        builder.append("إنشاء التخصص ");
        builder.append(" ( " + master.getName() + " ) ");
        builder.append("، مدة التخصص ");
        builder.append(" ( " + master.getPeriod() + " ) ");
        builder.append("، بواسطة ");
        builder.append(caller.getContact().getShortName());
        notificationService.notifyAll(Notification
                                              .builder()
                                              .title("العمليات على التخصصات")
                                              .message(builder.toString())
                                              .type(NotificationDegree.success)
                                              .icon("add")
                                              .build());
        entityHistoryListener.perform(builder.toString());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), master);
    }

    @PutMapping(value = "update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MASTER_UPDATE')")
    public String update(@RequestBody Master master) {
        if (masterService.findByCodeAndBranchAndIdIsNot(master.getCode(), master.getBranch(), master.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        Master object = masterService.findOne(master.getId());
        if (object != null) {
            master.setLastUpdate(new Date());
            master.setLastPerson(caller);
            master = masterService.save(master);

            StringBuilder builder = new StringBuilder();
            builder.append("تعديل بيانات التخصص ");
            builder.append(" ( " + master.getName() + " ) ");
            builder.append("، مدة التخصص ");
            builder.append(" ( " + master.getPeriod() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على التخصصات")
                                                  .message(builder.toString())
                                                  .type(NotificationDegree.warning)
                                                  .icon("edit")
                                                  .build());
            entityHistoryListener.perform(builder.toString());

            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), master);
        } else {
            return null;
        }
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MASTER_DELETE')")
    public void delete(@PathVariable Long id) {
        Master master = masterService.findOne(id);
        if (master != null) {

            LOG.info("DELETE ALL OFFERS");
            offerService.delete(master.getOffers());

            List<Account> accounts = master.getCourses()
                                           .stream()
                                           .flatMap(course -> course.getAccounts().stream())
                                           .collect(Collectors.toList());

            LOG.info("DELETE ALL ATTACHES RELATED WITH THIS MASTER");
            accountAttachService.delete(accounts.stream()
                                .flatMap(account -> account.getAccountAttaches().stream())
                                .collect(Collectors.toList()));

            LOG.info("DELETE ALL CONDITIONS RELATED WITH THIS MASTER");
            accountConditionService.delete(accounts.stream()
                                                 .flatMap(account -> account.getAccountConditions().stream())
                                                 .collect(Collectors.toList()));

            LOG.info("DELETE ALL NOTES RELATED WITH THIS MASTER");
            accountNoteService.delete(accounts.stream()
                                            .flatMap(account -> account.getAccountNotes().stream())
                                            .collect(Collectors.toList()));

            LOG.info("DELETE ALL PAYMENTS THAT IS RELATED WITH THIS MASTER");
            accountPaymentService.delete(accounts.stream()
                                               .flatMap(account -> account.getAccountPayments().stream())
                                               .collect(Collectors.toList()));

            LOG.info("DELETE ALL ACCOUNTS THAT IS RELATED WITH THIS MASTER");
            accountService.delete(accounts);

            LOG.info("DELETE ALL COURSES THAT IS RELATED WITH THIS MASTER");
            courseService.delete(master.getCourses());

            LOG.info("FINALLY DELETE THIS MASTER");
            masterService.delete(id);

            Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
            StringBuilder builder = new StringBuilder();
            builder.append("حذف التخصص ");
            builder.append(" ( " + master.getName() + " ) ");
            builder.append("، مدة التخصص ");
            builder.append(" ( " + master.getPeriod() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على التخصصات")
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
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(masterService.findAll()));
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), masterService.findOne(id));
    }

    @GetMapping(value = "findByBranch", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByBranch(@RequestParam(value = "branchId") Long branchId) {
        Branch branch = branchService.findOne(branchId);
        if (branch != null) {
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), masterService.findByBranch(branch));
        } else {
            return null;
        }
    }

    @GetMapping(value = "findByCodeAndBranch", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByCodeAndBranch(@RequestParam(value = "code") Integer code, @RequestParam(value = "branchId") Long branchId) {
        Branch branch = branchService.findOne(branchId);
        if (branch != null) {
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), masterService.findByCodeAndBranch(code, branch));
        } else {
            return null;
        }
    }

    @GetMapping(value = "fetchTableData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchTableData() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       transactionalService.getPersonBranches(caller)
                                                           .stream()
                                                           .flatMap(branch -> branch.getMasters().stream())
                                                           .collect(Collectors.toList()));
    }

    @GetMapping(value = "fetchMasterCombo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchMasterCombo() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_MASTER_COMBO),
                                       transactionalService.getPersonBranches(caller)
                                                           .stream()
                                                           .flatMap(branch -> branch.getMasters().stream())
                                                           .collect(Collectors.toList()));
    }

    @GetMapping(value = "fetchMasterCourseCombo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchMasterCourseCombo() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_MASTER_COURSE_COMBO),
                                       transactionalService.getPersonBranches(caller)
                                                           .stream()
                                                           .flatMap(branch -> branch.getMasters().stream())
                                                           .collect(Collectors.toList()));
    }

    @GetMapping(value = "fetchMasterBranchCombo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchMasterBranchCombo() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_MASTER_BRANCH_COMBO),
                                       transactionalService.getPersonBranches(caller)
                                                           .stream()
                                                           .flatMap(branch -> branch.getMasters().stream())
                                                           .collect(Collectors.toList()));
    }

    @GetMapping(value = "fetchBranchMasterCourseCombo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchBranchMasterCourseCombo() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_BRANCH_MASTER_COURSE_COMBO),
                                       transactionalService.getPersonBranches(caller)
                                                           .stream()
                                                           .flatMap(branch -> branch.getMasters().stream())
                                                           .collect(Collectors.toList()));
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            @RequestParam(value = "name", required = false) final String name,
            @RequestParam(value = "codeFrom", required = false) final Long codeFrom,
            @RequestParam(value = "codeTo", required = false) final Long codeTo,
            @RequestParam(value = "branchId", required = false) final Long branchId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       masterSearch.search(name, codeFrom, codeTo, branchId));
    }

}
