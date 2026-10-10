package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val authorName: String = "Umuyobozi (Admin)",
    val isPinned: Boolean = true,
    val imageUrl: String = "", // Optional banner / advertising image URL or asset name
    val isPublished: Boolean = true,
    val isVisibleToMembers: Boolean = true,
    val startDate: String = "", // Optional start date string (e.g. "2026-10-10")
    val endDate: String = "",   // Optional end date string (e.g. "2026-10-17")
    val createdAt: Long = System.currentTimeMillis()
)
