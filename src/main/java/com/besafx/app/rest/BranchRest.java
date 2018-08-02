package com.besafx.app.rest;

import com.besafx.app.async.TransactionalService;
import com.besafx.app.auditing.EntityHistoryListener;
import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Branch;
import com.besafx.app.entity.Person;
import com.besafx.app.service.BranchService;
import com.besafx.app.service.CompanyService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationDegree;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping(value = "/api/branch/")
public class BranchRest {

    private final static Logger LOG = LoggerFactory.getLogger(BranchRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "-masters," +
            "-banks," +
            "company[id,code,name]";
    private final String FILTER_BRANCH_COMBO = "" +
            "id," +
            "code," +
            "name";
    private final String FILTER_BRANCH_MASTER_COMBO = "" +
            "id," +
            "code," +
            "name," +
            "masters[id,code,name]";
    private final String FILTER_BRANCH_MASTER_COURSE_COMBO = "" +
            "id," +
            "code," +
            "name," +
            "masters[id,code,name,courses[id,code,instructor]]";

    @Autowired
    private CompanyService companyService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityHistoryListener entityHistoryListener;

    @Autowired
    private TransactionalService transactionalService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BRANCH_CREATE')")
    @Transactional
    public String create(@RequestBody Branch branch) {
        Branch topBranch = branchService.findTopByOrderByCodeDesc();
        if (topBranch == null) {
            branch.setCode(1);
        } else {
            branch.setCode(topBranch.getCode() + 1);
        }
        branch.setCompany(companyService.findFirstBy());
        branch = branchService.save(branch);

        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        StringBuilder builder = new StringBuilder();
        builder.append("إنشاء الفرع رقم ");
        builder.append(" ( " + branch.getCode() + " ) ");
        builder.append("، بواسطة ");
        builder.append(caller.getContact().getShortName());
        notificationService.notifyAll(Notification
                                              .builder()
                                              .title("العمليات على الفروع")
                                              .message(builder.toString())
                                              .type(NotificationDegree.success)
                                              .icon("add")
                                              .build());
        entityHistoryListener.perform(builder.toString());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), branch);
    }

    @PostMapping(value = "duplicate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BRANCH_CREATE')")
    @Transactional
    public String duplicate(@RequestBody Branch branch) {
        Branch topBranch = branchService.findTopByOrderByCodeDesc();
        if (topBranch == null) {
            branch.setCode(1);
        } else {
            branch.setCode(topBranch.getCode() + 1);
        }
        branch.setId(null);
        branch.setCompany(companyService.findFirstBy());
        branch = branchService.save(branch);

        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        StringBuilder builder = new StringBuilder();
        builder.append("نسخ بيانات الفرع رقم ");
        builder.append(" ( " + branch.getCode() + " ) ");
        builder.append("، بواسطة ");
        builder.append(caller.getContact().getShortName());
        notificationService.notifyAll(Notification
                                              .builder()
                                              .title("العمليات على الفروع")
                                              .message(builder.toString())
                                              .type(NotificationDegree.warning)
                                              .icon("edit")
                                              .build());
        entityHistoryListener.perform(builder.toString());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), branch);
    }

    @PutMapping(value = "update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BRANCH_UPDATE')")
    @Transactional
    public String update(@RequestBody Branch branch) {
        if (branchService.findByCodeAndIdIsNot(branch.getCode(), branch.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Branch object = branchService.findOne(branch.getId());
        if (object != null) {
            branch.setCompany(companyService.findFirstBy());
            branch = branchService.save(branch);

            Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
            StringBuilder builder = new StringBuilder();
            builder.append("تعديل بيانات الفرع رقم ");
            builder.append(" ( " + branch.getCode() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على الفروع")
                                                  .message(builder.toString())
                                                  .type(NotificationDegree.warning)
                                                  .icon("edit")
                                                  .build());
            entityHistoryListener.perform(builder.toString());

            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), branch);
        } else {
            return null;
        }
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_BRANCH_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        Branch branch = branchService.findOne(id);
        if (branch != null) {

            LOG.info("DELETE ALL OFFERS");

            LOG.info("DELETE ALL MASTERS");

            LOG.info("DELETE ALL PERSONS");
            branchService.delete(id);

            Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
            StringBuilder builder = new StringBuilder();
            builder.append("حذف الفرع رقم ");
            builder.append(" ( " + branch.getCode() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على الفروع")
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
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), branchService.findOne(id));
    }

    @GetMapping(value = "fetchTableData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchTableData() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), transactionalService.getPersonBranches(caller));
    }

    @GetMapping(value = "fetchBranchMaster", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchBranchMaster() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_BRANCH_MASTER_COMBO), transactionalService.getPersonBranches(caller));
    }

    @GetMapping(value = "fetchBranchMasterCourse", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchBranchMasterCourse() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_BRANCH_MASTER_COURSE_COMBO), transactionalService.getPersonBranches(caller));
    }

    @GetMapping(value = "fetchBranchCombo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchBranchCombo() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_BRANCH_COMBO), transactionalService.getPersonBranches(caller));
    }
}
