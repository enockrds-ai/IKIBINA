package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "repayments",
    foreignKeys = [
        ForeignKey(
            entity = LoanEntity::class,
            parentColumns = ["id"],
            childColumns = ["loanId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("loanId"),
        Index("memberId"),
        Index("status")
    ]
)
data class RepaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val loanId: Long,
    val memberId: Long,
    val memberName: String,
    val amount: Double,
    val principalPortion: Double = 0.0,
    val interestPortion: Double = 0.0,
    val paymentType: String = "PRINCIPAL", // "PRINCIPAL" or "INTEREST"
    val paymentReference: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val reviewedBy: Long? = null,
    val reviewedAt: Long? = null
)
