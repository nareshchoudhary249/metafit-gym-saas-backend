package com.metafit.repository;

import com.metafit.entity.Payment;
import com.metafit.enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Payment Repository
 * Handles database operations for payment records
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payments by member ID
     */
    List<Payment> findByMemberIdOrderByPaymentDateDesc(Long memberId);

    /**
     * Find payments between dates
     */
    @Query("SELECT p FROM Payment p WHERE " +
            "p.paymentDate BETWEEN :startDate AND :endDate " +
            "ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentsBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find today's payments
     */
    @Query("SELECT p FROM Payment p WHERE " +
            "p.paymentDate BETWEEN :startDate AND :endDate " +
            "ORDER BY p.paymentDate DESC")
    List<Payment> findTodayPayments(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Calculate total revenue for today
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE " +
            "p.paymentDate BETWEEN :startDate AND :endDate")
    Double getTodayRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Calculate total revenue between dates
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE " +
            "p.paymentDate BETWEEN :startDate AND :endDate")
    Double getRevenueBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Sum total payments for a member
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE " +
            "p.member.id = :memberId")
    Double sumAmountByMemberId(@Param("memberId") Long memberId);

    /**
     * Count payments by member
     */
    Long countByMemberId(Long memberId);

    /**
     * Count today's payments
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE " +
            "p.paymentDate BETWEEN :startDate AND :endDate")
    Long countTodayPayments(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get revenue by payment method for today
     */
    @Query("SELECT p.paymentMethod, COALESCE(SUM(p.amount), 0) " +
            "FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate " +
            "GROUP BY p.paymentMethod")
    List<Object[]> getTodayRevenueByPaymentMethod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get revenue by payment method between dates
     */
    @Query("SELECT p.paymentMethod, COALESCE(SUM(p.amount), 0) " +
            "FROM Payment p WHERE " +
            "p.paymentDate BETWEEN :startDate AND :endDate " +
            "GROUP BY p.paymentMethod")
    List<Object[]> getRevenueByPaymentMethodBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find payments by payment method
     */
    List<Payment> findByPaymentMethodOrderByPaymentDateDesc(PaymentMethod paymentMethod);

    /**
     * Get daily revenue report (date, total amount)
     */
    @Query("SELECT CAST(p.paymentDate AS date) as date, COALESCE(SUM(p.amount), 0) as revenue " +
            "FROM Payment p WHERE " +
            "p.paymentDate BETWEEN :startDate AND :endDate " +
            "GROUP BY CAST(p.paymentDate AS date) " +
            "ORDER BY CAST(p.paymentDate AS date)")
    List<Object[]> getDailyRevenueReport(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Get monthly revenue report
     */
    @Query("SELECT YEAR(p.paymentDate) as year, MONTH(p.paymentDate) as month, " +
            "COALESCE(SUM(p.amount), 0) as revenue " +
            "FROM Payment p WHERE " +
            "p.paymentDate BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(p.paymentDate), MONTH(p.paymentDate) " +
            "ORDER BY year, month")
    List<Object[]> getMonthlyRevenueReport(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find last payment for a member
     */
    Payment findTopByMemberIdOrderByPaymentDateDesc(Long memberId);

    /**
     * Count payments in current month
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE " +
            "YEAR(p.paymentDate) = YEAR(CURRENT_DATE) AND " +
            "MONTH(p.paymentDate) = MONTH(CURRENT_DATE)")
    Long countPaymentsThisMonth();

    /**
     * Get this month's revenue
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE " +
            "YEAR(p.paymentDate) = YEAR(CURRENT_DATE) AND " +
            "MONTH(p.paymentDate) = MONTH(CURRENT_DATE)")
    Double getThisMonthRevenue();
}
