package com.makariev.examples.spring.bookstore.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.makariev.examples.spring.bookstore.product.Book;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
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
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", unique = true, nullable = false)
    private Book book;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public void addStock(int amount) {
        this.quantity += amount;
    }

    public void removeStock(int amount) {
        if (amount > this.quantity) {
            throw new IllegalArgumentException("Attempt to remove more stock than is available");
        }
        this.quantity -= amount;
    }
}
