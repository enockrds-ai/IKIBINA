package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "password_resets",
    indices = [
        Index("identifier"),
        Index("token")
    ]
)
data class PasswordResetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val identifier: String, // email or phone
    val token: String, // 6-digit recovery OTP / code
    val expiresAt: Long,
    val isUsed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
