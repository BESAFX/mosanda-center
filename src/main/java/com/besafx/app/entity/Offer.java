package com.besafx.app.entity;

import com.besafx.app.entity.enums.PaymentType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.decimal4j.util.DoubleRounder;
import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Component
public class Offer implements Serializable {

    private final static Logger log = LoggerFactory.getLogger(Offer.class);

    private static final long serialVersionUID = 1L;

    @GenericGenerator(
            name = "offerSequenceGenerator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "OFFER_SEQUENCE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    @Id
    @GeneratedValue(generator = "offerSequenceGenerator")
    private Long id;

    private Integer code;

    private String note;

    private String customerName;

    private String customerIdentityNumber;

    private String customerMobile;

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

    @ManyToOne
    @JoinColumn(name = "student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "master")
    private Master master;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "person")
    private Person person;

    @OneToMany(mappedBy = "offer", fetch = FetchType.LAZY)
    private List<Call> calls = new ArrayList<>();

    @JsonCreator
    public static Offer Create(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Offer offer = mapper.readValue(jsonString, Offer.class);
        return offer;
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

    public String getPaymentTypeInArabic(){
        try{
            return this.paymentType.getName();
        }catch (Exception ex){
            return "";
        }
    }
}
