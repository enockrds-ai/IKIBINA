package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["phone"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val passwordHash: String,
    val salt: String,
    val role: String, // "ADMIN" or "MEMBER"
    val status: String, // "PENDING", "ACTIVE", "INACTIVE", "REJECTED"
    val savingsGroupName: String = "Ikibina cy'Ubwizigame n'Inguzanyo",
    val mustChangePassword: Boolean = false,
    val approvedSavingsBalance: Double = 0.0,
    val pendingSavingsBalance: Double = 0.0,
    val activeLoanBalance: Double = 0.0,
    val outstandingFinesBalance: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val approvedBy: Long? = null
)
