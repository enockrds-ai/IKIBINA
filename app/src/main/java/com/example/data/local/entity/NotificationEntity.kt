package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    indices = [
        Index("recipientRole"),
        Index("recipientId"),
        Index("isRead")
    ]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipientRole: String, // "ADMIN", "MEMBER"
    val recipientId: Long? = null, // null = all admins or specific member
    val title: String, // e.g., "Ubwizigame bitegerejwe kwemezwa !"
    val message: String, // e.g., "Enock Wenda — RWF 500"
    val type: String, // "SAVINGS_PENDING", "LOAN_PENDING", "REGISTRATION_PENDING", "REPAYMENT_PENDING", "GENERAL"
    val referenceId: Long? = null, // ID of savings, loan, or user
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
