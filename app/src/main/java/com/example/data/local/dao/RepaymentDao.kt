package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.RepaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepaymentDao {
    @Query("SELECT * FROM repayments ORDER BY submittedAt DESC")
    fun getAllRepayments(): Flow<List<RepaymentEntity>>

    @Query("SELECT * FROM repayments WHERE status = 'PENDING' ORDER BY submittedAt DESC")
    fun getPendingRepayments(): Flow<List<RepaymentEntity>>

    @Query("SELECT * FROM repayments WHERE memberId = :memberId ORDER BY submittedAt DESC")
    fun getRepaymentsByMember(memberId: Long): Flow<List<RepaymentEntity>>

    @Query("SELECT * FROM repayments WHERE id = :id LIMIT 1")
    suspend fun getRepaymentById(id: Long): RepaymentEntity?

    @Query("SELECT COUNT(*) FROM repayments WHERE status = 'PENDING'")
    fun getPendingRepaymentsCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM repayments WHERE status = 'APPROVED'")
    fun getTotalApprovedRepayments(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM repayments WHERE status = 'PENDING'")
    fun getTotalPendingRepayments(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepayment(repayment: RepaymentEntity): Long

    @Update
    suspend fun updateRepayment(repayment: RepaymentEntity)

    @Query("UPDATE repayments SET status = :status, reviewedBy = :reviewedBy, reviewedAt = :reviewedAt WHERE id = :id")
    suspend fun updateRepaymentStatus(id: Long, status: String, reviewedBy: Long?, reviewedAt: Long?)
}
