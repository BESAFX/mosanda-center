package com.besafx.app.init;

import com.besafx.app.entity.*;
import com.besafx.app.service.*;
import com.besafx.app.util.CompanyOptions;
import com.besafx.app.util.JSONConverter;
import com.besafx.app.util.Options;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements CommandLineRunner {

    private final static Logger LOG = LoggerFactory.getLogger(Initializer.class);

    public static Company company;

    public static TransactionType transactionTypeDeposit;

    public static TransactionType transactionTypeDepositTransfer;

    public static TransactionType transactionTypeWithdraw;

    public static TransactionType transactionTypeWithdrawTransfer;

    public static TransactionType transactionTypeExpense;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private TransactionTypeService transactionTypeService;

    @Autowired
    private PersonService personService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TeamService teamService;

    @Override
    public void run(String... args)  {
        start();
        init();
    }

    private void start() {

        LOG.info("بداية العمل على إنشاء الحسابات الافتراضية والاساسية لتشغيل البرنامج لأول مرة");

        Company company;

        if (Lists.newArrayList(companyService.findAll()).isEmpty()) {

            LOG.info("إنشاء حساب الشركة");
            company = new Company();
            company.setOptions(
                    JSONConverter.toString(
                            CompanyOptions.builder()
                                          .smsUserName("username")
                                          .smsPassword("password")
                                          .vatFactor(0.05)
                                          .logo("---")
                                          .background("---")
                                          .reportTitle("---")
                                          .reportSubTitle("---")
                                          .reportFooter("---")));
            companyService.save(company);

            LOG.info("إنشاء أنواع العمليات المالية");

            LOG.info("عملية الإيداع");
            TransactionType transactionTypeDeposit = new TransactionType();
            transactionTypeDeposit.setCode("Deposit");
            transactionTypeDeposit.setName("إيداع");
            transactionTypeService.save(transactionTypeDeposit);

            LOG.info("عملية الإيداع[تحويل]");
            TransactionType transactionTypeDepositTransfer = new TransactionType();
            transactionTypeDepositTransfer.setCode("Deposit_Transfer");
            transactionTypeDepositTransfer.setName("إيداع[تحويل]");
            transactionTypeDepositTransfer.setTransactionType(transactionTypeDeposit);
            transactionTypeService.save(transactionTypeDepositTransfer);

            LOG.info("عملية السحب");
            TransactionType transactionTypeWithdraw = new TransactionType();
            transactionTypeWithdraw.setCode("Withdraw");
            transactionTypeWithdraw.setName("سحب");
            transactionTypeService.save(transactionTypeWithdraw);

            LOG.info("عملية السحب[تحويل]");
            TransactionType transactionTypeWithdrawTransfer = new TransactionType();
            transactionTypeWithdrawTransfer.setCode("Withdraw_Transfer");
            transactionTypeWithdrawTransfer.setName("سحب[تحويل]");
            transactionTypeWithdrawTransfer.setTransactionType(transactionTypeWithdraw);
            transactionTypeService.save(transactionTypeWithdrawTransfer);

            LOG.info("[مصروفات]");
            TransactionType transactionTypeExpense = new TransactionType();
            transactionTypeExpense.setCode("Expense");
            transactionTypeExpense.setName("[مصروفات]");
            transactionTypeExpense.setTransactionType(transactionTypeWithdraw);
            transactionTypeService.save(transactionTypeExpense);
        }

        if (Lists.newArrayList(personService.findAll()).isEmpty()) {
            Person person = new Person();
            Contact contact = new Contact();
            contact.setFirstName("بسام");
            contact.setSecondName("أحمد");
            contact.setThirdName("أحمد");
            contact.setForthName("المهدي");
            contact.setPhoto("");
            contact.setMobile("0590780551");
            contact.setAddress("عرعر - الحدود الشمالية");
            contact.setBirthLocation("مصر");
            contact.setBirthDate(new DateTime().withDayOfMonth(10).withMonthOfYear(2).withYear(1990).toDate());
            contact.setQualification("Web Developer");
            person.setContact(contactService.save(contact));
            person.setEmail("islamhaker@gmail.com");
            person.setPassword(passwordEncoder.encode("besa2009"));
            person.setHiddenPassword("besa2009");
            person.setEnabled(true);
            person.setTokenExpired(false);
            person.setActive(false);
            person.setTechnicalSupport(true);
            Team team = new Team();
            team.setCode(1);
            team.setName("الدعم الفني");
            team.setAuthorities(String.join(",", "ROLE_COMPANY_UPDATE",
                                            "ROLE_DEPOSIT_CREATE",
                                            "ROLE_WITHDRAW_CREATE",
                                            "ROLE_TRANSFER_CREATE",
                                            "ROLE_EXPENSE_CREATE",
                                            "ROLE_SMS_SEND",
                                            "ROLE_BRANCH_CREATE",
                                            "ROLE_BRANCH_UPDATE",
                                            "ROLE_BRANCH_DELETE",
                                            "ROLE_MASTER_CREATE",
                                            "ROLE_MASTER_UPDATE",
                                            "ROLE_MASTER_DELETE",
                                            "ROLE_OFFER_CREATE",
                                            "ROLE_OFFER_UPDATE",
                                            "ROLE_OFFER_DELETE",
                                            "ROLE_COURSE_CREATE",
                                            "ROLE_COURSE_UPDATE",
                                            "ROLE_COURSE_DELETE",
                                            "ROLE_ACCOUNT_CREATE",
                                            "ROLE_ACCOUNT_UPDATE",
                                            "ROLE_ACCOUNT_DELETE",
                                            "ROLE_PERSON_CREATE",
                                            "ROLE_PERSON_UPDATE",
                                            "ROLE_PERSON_DELETE",
                                            "ROLE_TEAM_CREATE",
                                            "ROLE_TEAM_UPDATE",
                                            "ROLE_TEAM_DELETE"
                                           ));
            person.setTeam(teamService.save(team));
            person.setOptions(JSONConverter
                                      .toString(Options.builder()
                                                       .lang("AR")
                                                       .style("mdl-style")
                                                       .dateType("G")
                                                       .iconSet("icon-set-2")
                                                       .iconSetType("png")
                                                       .build())
                             );
            personService.save(person);
        }
    }

    private void init() {

        LOG.info("تعريف بعض المتغيرات الهامة");

        company = companyService.findFirstBy();

        transactionTypeDeposit = transactionTypeService.findByCode("Deposit");

        transactionTypeDepositTransfer = transactionTypeService.findByCode("Deposit_Transfer");

        transactionTypeWithdraw = transactionTypeService.findByCode("Withdraw");

        transactionTypeWithdrawTransfer = transactionTypeService.findByCode("Withdraw_Transfer");

        transactionTypeExpense = transactionTypeService.findByCode("Expense");

    }
}
