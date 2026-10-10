package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index("memberId"),
        Index("type"),
        Index("timestamp")
    ]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long?,
    val memberName: String,
    val type: String, // "SAVINGS_DEPOSIT", "LOAN_DISBURSED", "LOAN_REPAYMENT", "FINE_COLLECTED"
    val amount: Double,
    val direction: String, // "INFLOW" (adds to communal money) or "OUTFLOW" (disbursed loan)
    val description: String,
    val referenceId: Long? = null,
    val performedBy: String = "SISTEMU",
    val timestamp: Long = System.currentTimeMillis()
)
