package com.makariev.examples.spring.bookstore.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author dmakariev
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Customer extends User {

    @Column(name = "registration_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    @Column(name = "loyalty_points")
    private Integer loyaltyPoints = 0;

    public Customer(String username, String password, String email, Date registrationDate) {
        super(username, password, email);
        this.registrationDate = registrationDate;
    }
}
