package com.besafx.app.rest;

import com.besafx.app.async.TransactionalService;
import com.besafx.app.auditing.EntityHistoryListener;
import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.Course;
import com.besafx.app.entity.Master;
import com.besafx.app.entity.Person;
import com.besafx.app.search.CourseSearch;
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

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/course/")
public class CourseRest {

    private final static Logger LOG = LoggerFactory.getLogger(CourseRest.class);

    private final String FILTER_TABLE = "" +
            "**," +
            "master[id,code,name,branch[id,code,name]]," +
            "lastPerson[id,contact[id,shortName]]," +
            "accounts[id]";

    private final String FILTER_COURSE_MASTER_COMBO = "" +
            "id," +
            "code," +
            "instructor," +
            "companyName," +
            "master[id,code,name]";

    private final String FILTER_COURSE_MASTER_BRANCH_COMBO = "" +
            "id," +
            "code," +
            "instructor," +
            "companyName," +
            "master[id,code,name,branch[id,code,name]]";

    @Autowired
    private MasterService masterService;

    @Autowired
    private CourseSearch courseSearch;

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
    private TransactionalService transactionalService;

    @Autowired
    private EntityHistoryListener entityHistoryListener;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_COURSE_CREATE')")
    @Transactional
    public String create(@RequestBody Course course) {
        if (courseService.findByCodeAndMaster(course.getCode(), course.getMaster()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        course.setLastUpdate(new Date());
        course.setLastPerson(caller);
        course = courseService.save(course);

        StringBuilder builder = new StringBuilder();
        builder.append("إنشاء الدورة رقم ");
        builder.append(" ( " + course.getCode() + " ) ");
        builder.append("،التخصص ");
        builder.append(" ( " + course.getMaster().getName() + " ) ");
        builder.append("، بواسطة ");
        builder.append(caller.getContact().getShortName());
        notificationService.notifyAll(Notification
                                              .builder()
                                              .title("العمليات على الدورات")
                                              .message(builder.toString())
                                              .type(NotificationDegree.success)
                                              .icon("add")
                                              .build());
        entityHistoryListener.perform(builder.toString());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), course);
    }

    @PutMapping(value = "update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_COURSE_UPDATE')")
    @Transactional
    public String update(@RequestBody Course course) {
        if (courseService.findByCodeAndMasterAndIdIsNot(course.getCode(), course.getMaster(), course.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        Course object = courseService.findOne(course.getId());
        if (object != null) {
            course.setLastUpdate(new Date());
            course.setLastPerson(caller);
            course = courseService.save(course);

            StringBuilder builder = new StringBuilder();
            builder.append("تعديل الدورة رقم ");
            builder.append(" ( " + course.getCode() + " ) ");
            builder.append("،التخصص ");
            builder.append(" ( " + course.getMaster().getName() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على الدورات")
                                                  .message(builder.toString())
                                                  .type(NotificationDegree.warning)
                                                  .icon("edit")
                                                  .build());
            entityHistoryListener.perform(builder.toString());

            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), course);
        } else {
            return null;
        }
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_COURSE_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        Course course = courseService.findOne(id);
        if (course != null) {

            LOG.info("DELETE ALL ATTACHES RELATED WITH THIS COURSE");
            accountAttachService.delete(course.getAccounts().stream()
                                              .flatMap(account -> account.getAccountAttaches().stream())
                                              .collect(Collectors.toList()));

            LOG.info("DELETE ALL CONDITIONS RELATED WITH THIS COURSE");
            accountConditionService.delete(course.getAccounts().stream()
                                                 .flatMap(account -> account.getAccountConditions().stream())
                                                 .collect(Collectors.toList()));

            LOG.info("DELETE ALL NOTES RELATED WITH THIS COURSE");
            accountNoteService.delete(course.getAccounts().stream()
                                            .flatMap(account -> account.getAccountNotes().stream())
                                            .collect(Collectors.toList()));

            LOG.info("DELETE ALL PAYMENTS THAT IS RELATED WITH THIS COURSE");
            accountPaymentService.delete(course.getAccounts().stream()
                                               .flatMap(account -> account.getAccountPayments().stream())
                                               .collect(Collectors.toList()));

            LOG.info("DELETE ALL ACCOUNTS THAT IS RELATED WITH THIS COURSE");
            accountService.delete(course.getAccounts());

            LOG.info("FINALLY DELETE THIS COURSE");
            courseService.delete(course);

            Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
            StringBuilder builder = new StringBuilder();
            builder.append("حذف الدورة رقم ");
            builder.append(" ( " + course.getCode() + " ) ");
            builder.append("،التخصص ");
            builder.append(" ( " + course.getMaster().getName() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على الدورات")
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
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(courseService.findAll()));
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), courseService.findOne(id));
    }

    @GetMapping(value = "findByMaster/{masterId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findByMaster(@PathVariable(value = "masterId") Long masterId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), courseService.findByMaster(masterService.findOne(masterId)));
    }

    @GetMapping(value = "fetchTableData", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchTableData() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       transactionalService.getPersonBranches(caller)
                                                           .stream()
                                                           .flatMap(branch -> branch.getMasters().stream())
                                                           .collect(Collectors.toList())
                                                           .stream()
                                                           .flatMap(master -> master.getCourses().stream()).collect(Collectors.toList()));
    }

    @GetMapping(value = "fetchCourseMasterCombo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchCourseMasterCombo() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_COURSE_MASTER_COMBO),
                                       transactionalService.getPersonBranches(caller)
                                                           .stream()
                                                           .flatMap(branch -> branch.getMasters().stream())
                                                           .collect(Collectors.toList())
                                                           .stream()
                                                           .flatMap(master -> master.getCourses().stream()).collect(Collectors.toList()));
    }

    @GetMapping(value = "fetchCourseMasterBranchCombo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String fetchCourseMasterBranchCombo() {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_COURSE_MASTER_BRANCH_COMBO),
                                       transactionalService.getPersonBranches(caller)
                                                           .stream()
                                                           .flatMap(branch -> branch.getMasters().stream())
                                                           .collect(Collectors.toList())
                                                           .stream()
                                                           .flatMap(master -> master.getCourses().stream())
                                                           .sorted(Comparator.comparing(course -> course.getMaster().getBranch().getCode()))
                                                           .collect(Collectors.toList()));
    }

    @GetMapping(value = "filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String filter(
            @RequestParam(value = "instructor", required = false) final String instructor,
            @RequestParam(value = "codeFrom", required = false) final Long codeFrom,
            @RequestParam(value = "codeTo", required = false) final Long codeTo,
            @RequestParam(value = "branchId", required = false) final Long branchId,
            @RequestParam(value = "masterId", required = false) final Long masterId) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE),
                                       courseSearch.search(instructor, codeFrom, codeTo, branchId, masterId));
    }

}
