package com.besafx.app.rest;

import com.besafx.app.auditing.EntityHistoryListener;
import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.MasterCategory;
import com.besafx.app.entity.Person;
import com.besafx.app.service.MasterCategoryService;
import com.besafx.app.service.MasterService;
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
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(value = "/api/masterCategory/")
public class MasterCategoryRest {

    private final static Logger LOG = LoggerFactory.getLogger(BranchRest.class);

    public static final String FILTER_TABLE = "" +
            "**," +
            "masters[id]," +
            "person[id,contact[id,shortName]]";

    public static final String FILTER_MASTER_CATEGORY_COMBO = "" +
            "id," +
            "code," +
            "name";

    @Autowired
    private MasterCategoryService masterCategoryService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityHistoryListener entityHistoryListener;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MASTER_CATEGORY_CREATE')")
    @Transactional
    public String create(@RequestBody MasterCategory masterCategory) {
        MasterCategory topMasterCategory = masterCategoryService.findTopByOrderByCodeDesc();
        if (topMasterCategory == null) {
            masterCategory.setCode(1);
        } else {
            masterCategory.setCode(topMasterCategory.getCode() + 1);
        }
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        masterCategory.setPerson(caller);
        masterCategory.setDate(new DateTime().toDate());
        masterCategory = masterCategoryService.save(masterCategory);

        StringBuilder builder = new StringBuilder();
        builder.append("إنشاء التصنيف / ");
        builder.append(" ( " + masterCategory.getName() + " ) ");
        builder.append("، بواسطة ");
        builder.append(caller.getContact().getShortName());
        notificationService.notifyAll(Notification
                                              .builder()
                                              .title("العمليات على التصنيفات")
                                              .message(builder.toString())
                                              .type(NotificationDegree.success)
                                              .icon("add")
                                              .build());
        entityHistoryListener.perform(builder.toString());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), masterCategory);
    }

    @PutMapping(value = "update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MASTER_CATEGORY_UPDATE')")
    @Transactional
    public String update(@RequestBody MasterCategory masterCategory) {
        if (masterCategoryService.findByCodeAndIdIsNot(masterCategory.getCode(), masterCategory.getId()) != null) {
            throw new CustomException("هذا الكود مستخدم سابقاً، فضلاً قم بتغير الكود.");
        }
        MasterCategory object = masterCategoryService.findOne(masterCategory.getId());
        if (object != null) {
            Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
            masterCategory.setPerson(caller);
            masterCategory.setDate(new DateTime().toDate());
            masterCategory = masterCategoryService.save(masterCategory);

            StringBuilder builder = new StringBuilder();
            builder.append("تعديل بيانات التصنيف / ");
            builder.append(" ( " + masterCategory.getName() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على التصنيفات")
                                                  .message(builder.toString())
                                                  .type(NotificationDegree.warning)
                                                  .icon("edit")
                                                  .build());
            entityHistoryListener.perform(builder.toString());

            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), masterCategory);
        } else {
            return null;
        }
    }

    @DeleteMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_MASTER_CATEGORY_DELETE')")
    @Transactional
    public void delete(@PathVariable Long id) {
        MasterCategory masterCategory = masterCategoryService.findOne(id);
        if (masterCategory != null) {
            LOG.info("NULLABLE ALL MASTERS THAT IS RELATED TO THIS CATEGORY");
            masterCategory.getMasters().stream().forEach(master -> master.setMasterCategory(null));
            masterService.save(masterCategory.getMasters());

            LOG.info("DELETE CATEGORY");
            masterCategoryService.delete(id);

            Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
            StringBuilder builder = new StringBuilder();
            builder.append("حذف التصنيف / ");
            builder.append(" ( " + masterCategory.getName() + " ) ");
            builder.append("، بواسطة ");
            builder.append(caller.getContact().getShortName());
            notificationService.notifyAll(Notification
                                                  .builder()
                                                  .title("العمليات على التصنيفات")
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
        List<MasterCategory> list = Lists.newArrayList(masterCategoryService.findAll());
        list.sort(Comparator.comparing(MasterCategory::getCode));
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), list);
    }

    @GetMapping(value = "findAllCombo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAllCombo() {
        List<MasterCategory> list = Lists.newArrayList(masterCategoryService.findAll());
        list.sort(Comparator.comparing(MasterCategory::getCode));
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_MASTER_CATEGORY_COMBO), list);
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), masterCategoryService.findOne(id));
    }
}
