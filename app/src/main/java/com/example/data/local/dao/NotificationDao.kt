package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE recipientRole = 'ADMIN' OR recipientRole = 'ALL' ORDER BY createdAt DESC")
    fun getAdminNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE recipientId = :memberId OR recipientRole = 'ALL' ORDER BY createdAt DESC")
    fun getMemberNotifications(memberId: Long): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE (recipientRole = 'ADMIN' OR recipientRole = 'ALL') AND isRead = 0")
    fun getAdminUnreadCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notifications WHERE (recipientId = :memberId OR recipientRole = 'ALL') AND isRead = 0")
    fun getMemberUnreadCount(memberId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientRole = 'ADMIN'")
    suspend fun markAllAdminRead()

    @Query("UPDATE notifications SET isRead = 1 WHERE recipientId = :memberId")
    suspend fun markAllMemberRead(memberId: Long)
}
