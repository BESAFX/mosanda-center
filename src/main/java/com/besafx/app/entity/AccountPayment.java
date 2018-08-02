package com.besafx.app.entity;

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
import java.util.Date;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountPayment implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "accountPaymentSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "ACCOUNT_PAYMENT_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "accountPaymentSequenceGenerator")
    private Long id;

    private Long code;

    private Double amount;

    private Double vat;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String note;

    @ManyToOne
    @JoinColumn(name = "account")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "bankTransaction")
    private BankTransaction bankTransaction;

    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;

    @JsonCreator
    public static AccountPayment Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AccountPayment accountPayment = mapper.readValue(jsonString, AccountPayment.class);
        return accountPayment;
    }

    public Double getNet() {
        try {
            return this.amount + this.vat;
        } catch (Exception ex) {
            return 0.0;
        }
    }
}
