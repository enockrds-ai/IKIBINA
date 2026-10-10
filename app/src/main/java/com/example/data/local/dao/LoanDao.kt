package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.LoanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    @Query("SELECT * FROM loans ORDER BY requestedAt DESC")
    fun getAllLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE status = 'PENDING' ORDER BY requestedAt DESC")
    fun getPendingLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE status = 'ACTIVE' ORDER BY requestedAt DESC")
    fun getActiveLoans(): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE memberId = :memberId ORDER BY requestedAt DESC")
    fun getLoansByMember(memberId: Long): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE memberId = :memberId AND status = 'ACTIVE' LIMIT 1")
    suspend fun getActiveLoanForMember(memberId: Long): LoanEntity?

    @Query("SELECT * FROM loans WHERE memberId = :memberId AND status = 'PENDING' LIMIT 1")
    suspend fun getPendingLoanForMember(memberId: Long): LoanEntity?

    @Query("SELECT * FROM loans WHERE id = :id LIMIT 1")
    suspend fun getLoanById(id: Long): LoanEntity?

    @Query("SELECT COUNT(*) FROM loans WHERE status = 'ACTIVE'")
    fun getActiveLoansCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM loans WHERE status = 'PENDING'")
    fun getPendingLoansCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(outstandingBalance), 0.0) FROM loans WHERE status = 'ACTIVE'")
    fun getTotalOutstandingLoansBalance(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amountApproved), 0.0) FROM loans WHERE status IN ('ACTIVE', 'PAID')")
    fun getTotalPrincipalDisbursed(): Flow<Double>

    @Query("SELECT COALESCE(SUM(outstandingBalance), 0.0) FROM loans WHERE memberId = :memberId AND status = 'ACTIVE'")
    fun getMemberActiveLoanBalance(memberId: Long): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity): Long

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Query("""
        UPDATE loans 
        SET status = :status, 
            amountApproved = :approvedAmount, 
            totalDue = :totalDue, 
            outstandingBalance = :totalDue, 
            reviewedBy = :reviewedBy, 
            reviewedAt = :reviewedAt, 
            rejectionReason = :reason 
        WHERE id = :id
    """)
    suspend fun reviewLoan(
        id: Long,
        status: String,
        approvedAmount: Double,
        totalDue: Double,
        reviewedBy: Long?,
        reviewedAt: Long?,
        reason: String?
    )

    /**
     * Repay loan principal - reduces outstanding principal and total outstanding balance.
     */
    @Query("""
        UPDATE loans 
        SET principalPaid = principalPaid + :principalAmount,
            amountPaid = amountPaid + :principalAmount,
            outstandingBalance = CASE 
                WHEN (outstandingBalance - :principalAmount) <= 0.0 THEN 0.0 
                ELSE (outstandingBalance - :principalAmount) 
            END,
            status = CASE 
                WHEN (amountApproved - (principalPaid + :principalAmount)) <= 0.0 
                     AND (interestAmount - interestPaid) <= 0.0 THEN 'PAID' 
                ELSE 'ACTIVE' 
            END
        WHERE id = :loanId
    """)
    suspend fun applyPrincipalRepayment(loanId: Long, principalAmount: Double)

    /**
     * Repay loan interest - strictly pays interest, does NOT reduce outstanding principal!
     */
    @Query("""
        UPDATE loans 
        SET interestPaid = interestPaid + :interestAmountPaid,
            amountPaid = amountPaid + :interestAmountPaid,
            outstandingBalance = CASE 
                WHEN (outstandingBalance - :interestAmountPaid) <= 0.0 THEN 0.0 
                ELSE (outstandingBalance - :interestAmountPaid) 
            END,
            status = CASE 
                WHEN (amountApproved - principalPaid) <= 0.0 
                     AND (interestAmount - (interestPaid + :interestAmountPaid)) <= 0.0 THEN 'PAID' 
                ELSE 'ACTIVE' 
            END
        WHERE id = :loanId
    """)
    suspend fun applyInterestRepayment(loanId: Long, interestAmountPaid: Double)

    @Query("""
        UPDATE loans 
        SET amountPaid = amountPaid + :repayAmount, 
            outstandingBalance = CASE 
                WHEN (outstandingBalance - :repayAmount) <= 0.0 THEN 0.0 
                ELSE (outstandingBalance - :repayAmount) 
            END,
            status = CASE 
                WHEN (outstandingBalance - :repayAmount) <= 0.0 THEN 'PAID' 
                ELSE 'ACTIVE' 
            END
        WHERE id = :loanId
    """)
    suspend fun applyRepaymentToLoan(loanId: Long, repayAmount: Double)
}
