package com.besafx.app.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bank implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "bankSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "BANK_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "bankSequenceGenerator")
    private Long id;

    private String code;

    private String name;

    @Transient
    private Double openCash;

    @Transient
    private Double balance;

    @Transient
    private Double totalDeposits;

    @Transient
    private Double totalWithdraws;

    @Temporal(TemporalType.TIMESTAMP)
    private Date registerDate;

    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "branch")
    private Branch branch;

    @OneToMany(mappedBy = "bank")
    private List<BankTransaction> bankTransactions = new ArrayList<>();

    @JsonCreator
    public static Bank Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Bank bank = mapper.readValue(jsonString, Bank.class);
        return bank;
    }

    public Date getLastTransactionDate() {
        try {
            return this.bankTransactions.stream().map(BankTransaction::getDate).max(Date::compareTo).get();
        } catch (Exception ex) {
            return null;
        }
    }
}
