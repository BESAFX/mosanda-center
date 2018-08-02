package com.besafx.app.rest;

import com.besafx.app.async.TransactionalService;
import com.besafx.app.auditing.EntityHistoryListener;
import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.entity.Contact;
import com.besafx.app.entity.Person;
import com.besafx.app.entity.Student;
import com.besafx.app.search.StudentSearch;
import com.besafx.app.service.ContactService;
import com.besafx.app.service.StudentService;
import com.besafx.app.ws.Notification;
import com.besafx.app.ws.NotificationDegree;
import com.besafx.app.ws.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import com.google.common.collect.Lists;
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
import java.util.List;
import java.util.ListIterator;

@RestController
@RequestMapping(value = "/api/student/")
public class StudentRest {

    private final static Logger LOG = LoggerFactory.getLogger(StudentRest.class);

    public static final String FILTER_TABLE = "" +
            "**," +
            "branch[id,code,name]," +
            "lastPerson[id,contact[id,shortName]]";

    @Autowired
    private StudentService studentService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private StudentSearch studentSearch;

    @Autowired
    private EntityHistoryListener entityHistoryListener;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TransactionalService transactionalService;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_STUDENT_CREATE')")
    @Transactional
    public String create(@RequestBody Student student) {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();

        student.setLastUpdate(new DateTime().toDate());
        student.setLastPerson(caller);
        student.setContact(contactService.save(student.getContact()));
        student = studentService.save(student);

        StringBuilder builder = new StringBuilder();
        builder.append("إنشاء كارت الطالب ");
        builder.append(" ( " + student.getContact().getFirstName().concat(student.getContact().getForthName()) + " ) ");
        builder.append("،رقم الجوال ");
        builder.append(" ( " + student.getContact().getMobile() + " ) ");
        builder.append("، بواسطة ");
        builder.append(caller.getContact().getShortName());
        notificationService.notifyAll(Notification
                                              .builder()
                                              .title("العمليات على كروت الطلاب")
                                              .message(builder.toString())
                                              .type(NotificationDegree.success)
                                              .icon("add")
                                              .build());
        entityHistoryListener.perform(builder.toString());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), student);
    }

    @PostMapping(value = "createBatch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_STUDENT_CREATE')")
    @Transactional
    public String createBatch(@RequestBody List<Student> students) {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        ListIterator<Student> studentListIterator = students.listIterator();
        while (studentListIterator.hasNext()) {
            Student student = studentListIterator.next();
            student.setLastUpdate(new DateTime().toDate());
            student.setLastPerson(caller);
            student.setContact(contactService.save(student.getContact()));
        }

        LOG.info("SAVE STUDENTS");
        studentService.save(students);

        StringBuilder builder = new StringBuilder();
        builder.append("إنشاء مجموعة من كروت الطلاب عدد ");
        builder.append("( " + students.size() + " )");
        builder.append("، بواسطة ");
        builder.append(caller.getContact().getShortName());
        notificationService.notifyAll(Notification
                                              .builder()
                                              .title("العمليات على كروت الطلاب")
                                              .message(builder.toString())
                                              .type(NotificationDegree.success)
                                              .icon("add")
                                              .build());

        entityHistoryListener.perform(builder.toString());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), students);
    }

    @PutMapping(value = "update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_STUDENT_UPDATE')")
    @Transactional
    public String update(@RequestBody Student student) {
        Student object = studentService.findOne(student.getId());
        if (object != null) {
            Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();

            student.setLastUpdate(new DateTime().toDate());
            student.setLastPerson(caller);
            student.setContact(contactService.save(student.getContact()));
            student = studentService.save(student);

            StringBuilder builder = new StringBuilder();
            builder.append("تعديل كارت الطالب ");
            builder.append(" ( " + student.getContact().getFullName() + " ) ");
            builder.append("،رقم الجوال ");
            builder.append(" ( " + student.getContact().getMobile() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على كروت الطلاب")
                                                  .message(builder.toString())
                                                  .type(NotificationDegree.warning)
                                                  .icon("edit")
                                                  .build());
            entityHistoryListener.perform(builder.toString());

            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), student);
        } else {
            return null;
        }
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_STUDENT_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        Student student = studentService.findOne(id);
        if (student != null) {
            studentService.delete(id);

            Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
            StringBuilder builder = new StringBuilder();
            builder.append("حذف كارت الطالب ");
            builder.append(" ( " + student.getContact().getFullName() + " ) ");
            builder.append("،رقم الجوال ");
            builder.append(" ( " + student.getContact().getMobile() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على كروت الطلاب")
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
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), studentService.findOne(id));
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
            @RequestParam(value = "branchId", required = false) final Long branchId,
            @RequestParam(value = "filterCompareType") final String filterCompareType,
            Pageable pageable) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(),
                                                     "**,".concat("content[").concat(FILTER_TABLE).concat("]")),
                                       studentSearch.filter(
                                               firstName,
                                               secondName,
                                               thirdName,
                                               forthName,
                                               fullName,
                                               registerDateFrom,
                                               registerDateTo,
                                               studentIdentityNumber,
                                               studentMobile,
                                               branchId,
                                               filterCompareType,
                                               pageable));
    }

}
