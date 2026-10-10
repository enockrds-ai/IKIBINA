package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "loans",
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
        Index("status")
    ]
)
data class LoanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long,
    val memberName: String,
    val amountRequested: Double,
    val amountApproved: Double = 0.0,
    val interestRatePercent: Double = 5.0, // Standard group interest
    val interestAmount: Double = 0.0,
    val totalDue: Double = 0.0,
    val amountPaid: Double = 0.0,
    val principalPaid: Double = 0.0, // Tracked principal repayments
    val interestPaid: Double = 0.0,  // Tracked interest repayments
    val outstandingBalance: Double = 0.0,
    val reason: String,
    val repaymentDurationWeeks: Int = 4,
    val requestedAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, ACTIVE, PAID, REJECTED
    val reviewedBy: Long? = null,
    val reviewedAt: Long? = null,
    val rejectionReason: String? = null
) {
    val outstandingPrincipal: Double
        get() = (amountApproved - principalPaid).coerceAtLeast(0.0)

    val outstandingInterest: Double
        get() = (interestAmount - interestPaid).coerceAtLeast(0.0)
}
