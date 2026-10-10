package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.PasswordResetEntity

@Dao
interface PasswordResetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReset(reset: PasswordResetEntity): Long

    @Query("SELECT * FROM password_resets WHERE identifier = :identifier AND isUsed = 0 ORDER BY createdAt DESC LIMIT 1")
    suspend fun getActiveReset(identifier: String): PasswordResetEntity?

    @Query("UPDATE password_resets SET isUsed = 1 WHERE identifier = :identifier")
    suspend fun invalidateResets(identifier: String)
}
