package com.besafx.app.entity;

import com.besafx.app.entity.enums.PaymentType;
import com.besafx.app.util.DateConverter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.decimal4j.util.DoubleRounder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "accountSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "ACCOUNT_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "accountSequenceGenerator")
    private Long id;

    private Integer code;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registerDate;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(columnDefinition = "Decimal(10,1) default '0.0'")
    private Double price;

    @Column(columnDefinition = "Decimal(10,1) default '0.0'")
    private Double discount;

    @Column(columnDefinition = "Decimal(10,1) default '0.0'")
    private Double profit;

    @Column(columnDefinition = "Decimal(10,1) default '0.0'")
    private Double premiumAmount;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String note;

    @ManyToOne
    @JoinColumn(name = "course")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountPayment> accountPayments = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountAttach> accountAttaches = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountCondition> accountConditions = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<AccountNote> accountNotes = new ArrayList<>();

    @JsonCreator
    public static Account Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(jsonString, Account.class);
        return account;
    }

    public Double getNet() {
        try {
            switch (this.paymentType) {
                case Cash:
                    return DoubleRounder.round(this.price - (this.price * this.discount / 100), 2);
                case Premium:
                    return DoubleRounder.round(this.price + (this.price * this.profit / 100), 2);
                default:
                    return 0.0;
            }
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Double getPaid() {
        try {
            return this.accountPayments.stream().mapToDouble(AccountPayment::getNet).sum();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Double getRemain() {
        try {
            return this.getNet() - this.getPaid();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public String getPaymentTypeInArabic(){
        try{
            return this.paymentType.getName();
        }catch (Exception ex){
            return "";
        }
    }

    public Date getLastPaymentDate() {
        try {
            return this.accountPayments.stream().map(AccountPayment::getDate).max(Date::compareTo).get();
        } catch (Exception ex) {
            return null;
        }
    }
}
