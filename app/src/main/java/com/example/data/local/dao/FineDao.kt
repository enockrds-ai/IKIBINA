package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.FineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FineDao {
    @Query("SELECT * FROM fines ORDER BY createdAt DESC")
    fun getAllFines(): Flow<List<FineEntity>>

    @Query("SELECT * FROM fines WHERE memberId = :memberId ORDER BY createdAt DESC")
    fun getFinesByMember(memberId: Long): Flow<List<FineEntity>>

    @Query("SELECT * FROM fines WHERE memberId = :memberId AND weekCode = :weekCode LIMIT 1")
    suspend fun getFineForMemberWeek(memberId: Long, weekCode: String): FineEntity?

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM fines WHERE status = 'PAID'")
    fun getTotalCollectedFines(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM fines WHERE status = 'PENDING_PAYMENT'")
    fun getTotalPendingFines(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM fines WHERE memberId = :memberId AND status = 'PENDING_PAYMENT'")
    fun getMemberOutstandingFines(memberId: Long): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFine(fine: FineEntity): Long

    @Update
    suspend fun updateFine(fine: FineEntity)

    @Query("UPDATE fines SET status = 'PAID', paidAt = :paidAt, paymentReference = :ref, approvedBy = :approvedBy, approvedAt = :approvedAt WHERE id = :id")
    suspend fun markFinePaid(id: Long, paidAt: Long, ref: String?, approvedBy: Long?, approvedAt: Long?)

    @Query("UPDATE fines SET status = 'WAIVED', approvedBy = :approvedBy, approvedAt = :approvedAt WHERE id = :id")
    suspend fun waiveFine(id: Long, approvedBy: Long?, approvedAt: Long?)
}
