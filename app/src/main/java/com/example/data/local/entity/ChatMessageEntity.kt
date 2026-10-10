package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat_messages",
    indices = [
        Index("timestamp")
    ]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderId: Long,
    val senderName: String,
    val senderRole: String, // "ADMIN" or "MEMBER"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
