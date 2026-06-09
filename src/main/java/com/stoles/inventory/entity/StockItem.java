package com.stoles.inventory.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "stock_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockItem {

    // create variable id, label, fabric, size, weight, quantity, purchaseDate, createdBy, createdAt, updatedAt

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String label;

    @Column(nullable = false, length = 50)
    private String fabric;

    @Column(nullable = false, length = 20)
    private String size;

    @Column(nullable = false, length = 30)
    private String weight;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "purchase_date" , nullable = false)
    private LocalDate purchaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private AppUser createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
