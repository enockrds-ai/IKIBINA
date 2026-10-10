package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AnnouncementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY isPinned DESC, createdAt DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE isPublished = 1 AND isVisibleToMembers = 1 ORDER BY isPinned DESC, createdAt DESC")
    fun getPublishedMemberAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE isPublished = 1 AND isVisibleToMembers = 1 ORDER BY isPinned DESC, createdAt DESC LIMIT 1")
    fun getLatestAnnouncement(): Flow<AnnouncementEntity?>

    @Query("SELECT * FROM announcements WHERE id = :id LIMIT 1")
    suspend fun getAnnouncementById(id: Long): AnnouncementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long

    @Update
    suspend fun updateAnnouncement(announcement: AnnouncementEntity)

    @Query("UPDATE announcements SET isPublished = :isPublished WHERE id = :id")
    suspend fun togglePublishStatus(id: Long, isPublished: Boolean)

    @Query("UPDATE announcements SET isVisibleToMembers = :isVisible WHERE id = :id")
    suspend fun toggleVisibilityStatus(id: Long, isVisible: Boolean)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: Long)
}
