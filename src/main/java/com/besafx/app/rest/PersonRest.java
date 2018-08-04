package com.besafx.app.rest;

import com.besafx.app.auditing.EntityHistoryListener;
import com.besafx.app.auditing.PersonAwareUserDetails;
import com.besafx.app.config.CustomException;
import com.besafx.app.entity.BranchAccess;
import com.besafx.app.entity.Person;
import com.besafx.app.service.BranchAccessService;
import com.besafx.app.service.ContactService;
import com.besafx.app.service.PersonService;
import com.besafx.app.util.JSONConverter;
import com.besafx.app.util.Options;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.ListIterator;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/person/")
public class PersonRest {

    private final static Logger LOG = LoggerFactory.getLogger(PersonRest.class);

    public static final String FILTER_TABLE = "" +
            "**," +
            "team[**,-persons]," +
            "branch[id,code,name]," +
            "branches[id]," +
            "branchAccesses[id,-person,branch[id,code,name]]";
    public static final String FILTER_PERSON_COMBO = "" +
            "id," +
            "email," +
            "contact[id,shortName,mobile]";

    @Autowired
    private PersonService personService;

    @Autowired
    private BranchAccessService branchAccessService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityHistoryListener entityHistoryListener;

    @PostMapping(value = "create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_CREATE')")
    @Transactional
    public String create(@RequestBody Person person) {
        if (personService.findByEmail(person.getEmail()) != null) {
            throw new CustomException("هذا البريد الإلكتروني غير متاح ، فضلاً ادخل بريد آخر غير مستخدم");
        }
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        if (person.getContact() != null) {
            person.setContact(contactService.save(person.getContact()));
        }
        person.setTokenExpired(false);
        person.setTechnicalSupport(false);
        person.setActive(false);
        person.setEnabled(true);
        person.setHiddenPassword(person.getPassword());
        person.setOptions(JSONConverter
                                  .toString(Options.builder()
                                                   .lang("AR")
                                                   .style("mdl-style")
                                                   .dateType("G")
                                                   .iconSet("icon-set-2")
                                                   .iconSetType("png")
                                                   .build())
                         );
        person = personService.save(person);
        ListIterator<BranchAccess> listIterator = person.getBranchAccesses().listIterator();
        while (listIterator.hasNext()) {
            BranchAccess branchAccess = listIterator.next();
            branchAccess.setPerson(person);
            branchAccessService.save(branchAccess);
        }

        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        StringBuilder builder = new StringBuilder();
        builder.append("إنشاء المستخدم ");
        builder.append(" ( " + person.getContact().getFirstName().concat(" ").concat(person.getContact().getForthName()) + " ) ");
        builder.append("، بواسطة ");
        builder.append(caller.getContact().getShortName());
        notificationService.notifyAll(Notification
                                              .builder()
                                              .title("العمليات على المستخدمين")
                                              .message(builder.toString())
                                              .type(NotificationDegree.success)
                                              .icon("add")
                                              .build());
        entityHistoryListener.perform(builder.toString());

        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), person);
    }

    @PutMapping(value = "update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_UPDATE')")
    @Transactional
    public String update(@RequestBody Person person) {
        Person object = personService.findOne(person.getId());
        if (object != null) {
            if (!object.getPassword().equals(person.getPassword())) {
                person.setPassword(passwordEncoder.encode(person.getPassword()));
            }
            if (person.getContact() != null) {
                person.setContact(contactService.save(person.getContact()));
            }
            ListIterator<BranchAccess> listIterator = person.getBranchAccesses().listIterator();
            branchAccessService.delete(branchAccessService.findByPerson(person));
            person = personService.save(person);
            while (listIterator.hasNext()) {
                BranchAccess branchAccess = listIterator.next();
                branchAccess.setPerson(person);
                branchAccessService.save(branchAccess);
            }
            notificationService.notifyAll(Notification.builder().message("تم تعديل بيانات الحساب الشخصي بنجاح").type(NotificationDegree.success).build());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), person);
        } else {
            return null;
        }
    }

    @PutMapping(value = "updateProfile", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PROFILE_UPDATE')")
    @Transactional
    public String updateProfile(@RequestBody Person person) {
        Person object = personService.findOne(person.getId());
        if (object != null) {
            if (!object.getPassword().equals(person.getPassword())) {
                person.setPassword(passwordEncoder.encode(person.getPassword()));
            }
            if (person.getContact() != null) {
                person.setContact(contactService.save(person.getContact()));
            }
            notificationService.notifyAll(Notification.builder().message("تم تعديل بيانات الحساب الشخصي بنجاح").type(NotificationDegree.success).build());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), person);
        } else {
            return null;
        }
    }

    @PutMapping(value = "enable", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_ENABLE')")
    @Transactional
    public String enable(@RequestBody Person person) {
        Person object = personService.findOne(person.getId());
        if (object != null) {
            person.setEnabled(true);
            person = personService.save(person);
            notificationService.notifyAll(Notification.builder().message("تم تفعيل المستخدم بنجاح").type(NotificationDegree.success).build());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), person);
        } else {
            return null;
        }
    }

    @PutMapping(value = "disable", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_DISABLE')")
    @Transactional
    public String disable(@RequestBody Person person) {
        Person object = personService.findOne(person.getId());
        if (object != null) {
            person.setEnabled(false);
            person = personService.save(person);
            notificationService.notifyAll(Notification.builder().message("تم تعطيل المستخدم بنجاح").type(NotificationDegree.error).build());
            return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), person);
        } else {
            return null;
        }
    }

    @GetMapping(value = "setDateType/{type}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PROFILE_UPDATE')")
    public void setDateType(@PathVariable(value = "type") String type) {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        Options options = new Options();
        if (caller.getOptions() != null) {
            options = JSONConverter.toObject(caller.getOptions(), Options.class);
            options.setDateType(type);
        }
        caller.setOptions(JSONConverter.toString(options));
        personService.save(caller);
    }

    @GetMapping(value = "setStyle/{style}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PROFILE_UPDATE')")
    @Transactional
    public void setStyle(@PathVariable(value = "style") String style) {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        Options options = JSONConverter.toObject(caller.getOptions(), Options.class);
        options.setStyle(style);
        caller.setOptions(JSONConverter.toString(options));
        personService.save(caller);
    }

    @GetMapping(value = "setIconSet/{iconSet}/{iconSetType}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PROFILE_UPDATE')")
    @Transactional
    public void setIconSet(@PathVariable(value = "iconSet") String iconSet, @PathVariable(value = "iconSetType") String iconSetType) {
        Person caller = ((PersonAwareUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getPerson();
        Options options = JSONConverter.toObject(caller.getOptions(), Options.class);
        options.setIconSet(iconSet);
        options.setIconSetType(iconSetType);
        caller.setOptions(JSONConverter.toString(options));
        personService.save(caller);
    }

    @GetMapping(value = "delete/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_DELETE')")
    public void delete(@PathVariable Long id) {

    }

    @GetMapping(value = "findAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(personService.findByEnabledIsTrue()));
    }

    @GetMapping(value = "findAllCombo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAllCombo() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_PERSON_COMBO), Lists.newArrayList(personService.findByEnabledIsTrue()));
    }

    @GetMapping(value = "findOne/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), personService.findOne(id));
    }

    @GetMapping("findAuthorities")
    @ResponseBody
    public String findAuthorities(Authentication authentication) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), authentication.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList()));
    }

    @GetMapping("findActivePerson")
    @ResponseBody
    public String findActivePerson(Principal principal) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), personService.findByEmail(principal.getName()));
    }

}
