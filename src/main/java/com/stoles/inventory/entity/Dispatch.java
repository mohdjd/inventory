package com.stoles.inventory.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispatches")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Dispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_item_id", nullable = false)
    private StockItem stockItem;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_type_id", nullable = false)
    private WorkType workType;

    @Column(name = "price_per_piece", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerPiece;

    @Column(name = "sent_qty", nullable = false)
    private Integer sentQty;

    @Column(name = "received_qty", nullable = false)
    private Integer receivedQty = 0;

    @Column(name = "sent_date", nullable = false)
    private LocalDate sentDate;

    @Column(name = "received_date")
    private LocalDate receivedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DispatchStatus status = DispatchStatus.PENDING;

    /** user who created the dispatch record **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private AppUser createdBy;

    /** user who recorded the last receipt update **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_by")
    private AppUser receivedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
