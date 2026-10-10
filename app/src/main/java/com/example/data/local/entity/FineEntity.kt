package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fines",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        // CRITICAL IDEMPOTENCY: Exactly one fine per member per missed weekly savings period
        Index(value = ["memberId", "weekCode"], unique = true),
        Index("status")
    ]
)
data class FineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val weekCode: String, // e.g. "2026-W41"
    val amount: Double = 100.0, // Fixed RWF 100 per missed period (NOT per share)
    val reason: String = "Kutazigama mu gihe cyagenwe (Ku wa kabiri 17:00 kugeza ku wa gatatu 15:00)",
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING_PAYMENT", // PENDING_PAYMENT, PAID, WAIVED
    val paidAt: Long? = null,
    val paymentReference: String? = null,
    val approvedBy: Long? = null,
    val approvedAt: Long? = null
)
