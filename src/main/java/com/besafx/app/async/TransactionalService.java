package com.besafx.app.async;

import com.besafx.app.entity.*;
import com.besafx.app.entity.projection.BankTransactionAmount;
import com.besafx.app.init.Initializer;
import com.besafx.app.service.*;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

@Service
public class TransactionalService {

    private final static Logger LOG = LoggerFactory.getLogger(TransactionalService.class);

    @Autowired
    private OfferService offerService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private MasterService masterService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountPaymentService accountPaymentService;

    @Autowired
    private BankTransactionService bankTransactionService;

    @Transactional
    public Integer getNextOfferCode(Branch branch){
        Offer topOffer = offerService.findTopByMasterBranchOrderByCodeDesc(branch);
        if (topOffer == null) {
            return 1;
        } else {
            return (topOffer.getCode() + 1);
        }
    }

    @Transactional
    public Integer getNextAccountCode(Branch branch){
        Account topAccount = accountService.findTopByCourseMasterBranchOrderByCodeDesc(branch);
        if (topAccount == null) {
            return 1;
        } else {
            return (topAccount.getCode() + 1);
        }
    }

    @Transactional
    public Long getNextAccountPaymentCode(Branch branch){
        AccountPayment topAccountPayment = accountPaymentService.findTopByAccountCourseMasterBranchOrderByCodeDesc(branch);
        if (topAccountPayment == null) {
            return Long.valueOf(1);
        } else {
            return (topAccountPayment.getCode() + 1);
        }
    }

    @Transactional
    public List<Branch> getBranches(){
        return Lists.newArrayList(branchService.findAll());
    }

    @Transactional
    public List<Branch> getPersonBranches(Person person){
        try {
            if(person.getTechnicalSupport()){
                return Lists.newArrayList(branchService.findAll());
            }
            List<Branch> list = new ArrayList<>();
            list.add(person.getBranch());
            list.addAll(person.getBranchAccesses().stream().map(BranchAccess::getBranch).collect(Collectors.toList()));
            return list.stream().distinct().collect(Collectors.toList());
        } catch (Exception ex) {
            return null;
        }
    }

    @Transactional
    public List<Offer> getOffersByDateAndBranch(Branch branch, Date dateFrom, Date dateTo) {
        return offerService.findByMasterBranchAndDateBetween(branch, dateFrom, dateTo);
    }

    @Transactional
    public List<Master> getMastersByBranch(Branch branch){
        return masterService.findByBranch(branch);
    }

    @Transactional
    public List<Bank> fetchMyBanks(Person person) {
        List<Bank> banks = getPersonBranches(person)
                .stream()
                .flatMap(branch -> branch.getBanks().stream())
                .collect(Collectors.toList());
        ListIterator<Bank> bankListIterator = banks.listIterator();
        while (bankListIterator.hasNext()) {
            Bank bank = bankListIterator.next();

            Double depositAmount = bankTransactionService
                    .findByBankAndTransactionTypeIn(bank, Lists.newArrayList(
                            Initializer.transactionTypeDeposit,
                            Initializer.transactionTypeDepositTransfer),
                                                    BankTransactionAmount.class)
                    .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

            Double withdrawAmount = bankTransactionService
                    .findByBankAndTransactionTypeIn(bank, Lists.newArrayList(
                            Initializer.transactionTypeWithdraw,
                            Initializer.transactionTypeWithdrawTransfer,
                            Initializer.transactionTypeExpense),
                                                    BankTransactionAmount.class)
                    .stream().mapToDouble(BankTransactionAmount::getAmount).sum();

            LOG.info("مجموع الإيداعات = " + depositAmount);
            bank.setTotalDeposits(depositAmount);

            LOG.info("مجموع السحبيات = " + withdrawAmount);
            bank.setTotalWithdraws(withdrawAmount);

            bank.setBalance(depositAmount - withdrawAmount);
        }
        return banks;
    }

    @Transactional
    public String getContactFullName(Contact contact){
        return contact.getFullName();
    }
}
