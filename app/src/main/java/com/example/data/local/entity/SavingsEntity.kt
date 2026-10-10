package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "savings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("memberId"),
        Index("status"),
        Index("weekCode")
    ]
)
data class SavingsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val amount: Double,
    val sharesCount: Int, // 1 share = 500 RWF (1 to 8 shares)
    val weekCode: String, // e.g., "2026-W41"
    val submittedAt: Long = System.currentTimeMillis(),
    val isWithinScheduledPeriod: Boolean, // Tuesday 17:00 to Wednesday 15:00
    val paymentCode: String = "18281412512#",
    val paymentReference: String = "",
    val proofNote: String = "",
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val reviewedBy: Long? = null,
    val reviewedAt: Long? = null,
    val rejectionReason: String? = null
)
