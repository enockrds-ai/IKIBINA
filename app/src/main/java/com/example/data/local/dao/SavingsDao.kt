package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.SavingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingsDao {
    @Query("SELECT * FROM savings ORDER BY submittedAt DESC")
    fun getAllSavings(): Flow<List<SavingsEntity>>

    @Query("SELECT * FROM savings WHERE status = 'PENDING' ORDER BY submittedAt DESC")
    fun getPendingSavings(): Flow<List<SavingsEntity>>

    @Query("SELECT * FROM savings WHERE memberId = :memberId ORDER BY submittedAt DESC")
    fun getSavingsByMember(memberId: Long): Flow<List<SavingsEntity>>

    @Query("SELECT * FROM savings WHERE id = :id LIMIT 1")
    suspend fun getSavingsById(id: Long): SavingsEntity?

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM savings WHERE status = 'APPROVED'")
    fun getTotalApprovedSavings(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM savings WHERE status = 'PENDING'")
    fun getTotalPendingSavings(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM savings WHERE memberId = :memberId AND status = 'APPROVED'")
    fun getMemberApprovedSavings(memberId: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM savings WHERE memberId = :memberId AND status = 'PENDING'")
    fun getMemberPendingSavings(memberId: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM savings WHERE memberId = :memberId AND weekCode = :weekCode AND status IN ('APPROVED', 'PENDING')")
    suspend fun hasMemberSavedInWeek(memberId: Long, weekCode: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavings(savings: SavingsEntity): Long

    @Update
    suspend fun updateSavings(savings: SavingsEntity)

    @Query("UPDATE savings SET status = :status, reviewedBy = :reviewedBy, reviewedAt = :reviewedAt, rejectionReason = :reason WHERE id = :id")
    suspend fun updateSavingsStatus(id: Long, status: String, reviewedBy: Long?, reviewedAt: Long?, reason: String?)
}
