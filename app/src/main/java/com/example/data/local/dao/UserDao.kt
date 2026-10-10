package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdSync(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:identifier) OR phone = :identifier LIMIT 1")
    suspend fun getUserByIdentifier(identifier: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'MEMBER' ORDER BY fullName ASC")
    fun getAllMembers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'MEMBER' AND status = 'ACTIVE' ORDER BY fullName ASC")
    fun getActiveMembers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getPendingMembers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users WHERE role = 'MEMBER'")
    fun getTotalMembersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM users WHERE role = 'MEMBER' AND status = 'ACTIVE'")
    fun getActiveMembersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM users WHERE status = 'PENDING'")
    fun getPendingMembersCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET status = :status, approvedAt = :approvedAt, approvedBy = :approvedBy WHERE id = :userId")
    suspend fun updateMemberStatus(userId: Long, status: String, approvedAt: Long?, approvedBy: Long?)

    @Query("UPDATE users SET passwordHash = :passwordHash, salt = :salt, mustChangePassword = :mustChange WHERE id = :userId")
    suspend fun updatePassword(userId: Long, passwordHash: String, salt: String, mustChange: Boolean)
}
