package com.besafx.app.rest;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ListIterator;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/person/")
public class PersonRest {

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

    private final static Logger log = LoggerFactory.getLogger(PersonRest.class);

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

    @RequestMapping(value = "create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_CREATE')")
    public String create(@RequestBody Person person) {
        if (personService.findByEmail(person.getEmail()) != null) {
            throw new CustomException("هذا البريد الإلكتروني غير متاح ، فضلاً ادخل بريد آخر غير مستخدم");
        }
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        if (person.getContact() != null) {
            person.setContact(contactService.save(person.getContact()));
        }
        person.setTokenExpired(false);
        person.setActive(false);
        person.setEnabled(true);
        person.setOptions(JSONConverter.toString(Options.builder()
                .lang("AR")
                .dateType("H")
                .iconSet("icon-set-2")
                .iconSetType("png")
                .style("mdl-style-1")));
        person = personService.save(person);
        ListIterator<BranchAccess> listIterator = person.getBranchAccesses().listIterator();
        while (listIterator.hasNext()) {
            BranchAccess branchAccess = listIterator.next();
            branchAccess.setPerson(person);
            branchAccessService.save(branchAccess);
        }
        notificationService.notifyAll(Notification.builder().message("تم إنشاء حساب شخصي بنجاح").type(NotificationDegree.success).build());
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), person);
    }

    @RequestMapping(value = "update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_UPDATE')")
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

    @RequestMapping(value = "updateProfile", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PROFILE_UPDATE')")
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

    @RequestMapping(value = "enable", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_ENABLE')")
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

    @RequestMapping(value = "disable", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_DISABLE')")
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

    @RequestMapping(value = "setDateType/{type}", method = RequestMethod.GET)
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

    @RequestMapping(value = "setStyle/{style}", method = RequestMethod.GET)
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

    @RequestMapping(value = "setIconSet/{iconSet}/{iconSetType}", method = RequestMethod.GET)
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

    @RequestMapping(value = "delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PERSON_DELETE')")
    public void delete(@PathVariable Long id) {

    }

    @RequestMapping(value = "findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAll() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), Lists.newArrayList(personService.findByEnabledIsTrue()));
    }

    @RequestMapping(value = "findAllCombo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findAllCombo() {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_PERSON_COMBO), Lists.newArrayList(personService.findByEnabledIsTrue()));
    }

    @RequestMapping(value = "findOne/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String findOne(@PathVariable Long id) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), personService.findOne(id));
    }

    @RequestMapping("findAuthorities")
    @ResponseBody
    public String findAuthorities(Authentication authentication) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), authentication.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList()));
    }

    @RequestMapping("findActivePerson")
    @ResponseBody
    public String findActivePerson(Principal principal) {
        return SquigglyUtils.stringify(Squiggly.init(new ObjectMapper(), FILTER_TABLE), personService.findByEmail(principal.getName()));
    }

}
