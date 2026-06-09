package com.stoles.inventory.repository;

import com.stoles.inventory.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByWorkerIdOrderByPaymentDateDesc(Long workerId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.worker.id = :workerId")
    BigDecimal totalPaidToWorker(@Param("workerId") Long workerId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p")
    BigDecimal totalPaidAll();

    @Query("""
            SELECT p FROM Payment p
            JOIN FETCH p.worker w
            LEFT JOIN FETCH p.createdBy u
            WHERE (:workerId IS NULL OR w.id = :workerId)
            AND (:from IS NULL OR p.paymentDate >= :from)
            AND (:to IS NULL OR p.paymentDate <= :to)
            ORDER BY p.paymentDate DESC
            """)
    List<Payment> filter(
            @Param("workerId") Long workerId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}