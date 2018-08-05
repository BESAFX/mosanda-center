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
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "studentSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "STUDENT_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "studentSequenceGenerator")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdate;

    @ManyToOne
    @JoinColumn(name = "last_person")
    private Person lastPerson;

    @ManyToOne
    @JoinColumn(name = "contact")
    private Contact contact;

    @ManyToOne
    @JoinColumn(name = "branch")
    private Branch branch;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
    private List<Account> accounts = new ArrayList<>();

    @JsonCreator
    public static Student Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Student student = mapper.readValue(jsonString, Student.class);
        return student;
    }

    public Double getTotalNet() {
        try {
            return this.accounts.stream().mapToDouble(Account::getNet).sum();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Double getTotalPaid() {
        try {
            return this.accounts.stream().mapToDouble(Account::getPaid).sum();
        } catch (Exception ex) {
            return 0.0;
        }
    }

    public Double getTotalRemain() {
        try {
            return this.accounts.stream().mapToDouble(Account::getPaid).sum();
        } catch (Exception ex) {
            return 0.0;
        }
    }
}
