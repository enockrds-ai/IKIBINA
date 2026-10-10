package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_settings")
data class GroupSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val groupName: String = "IKIBINA CY'UBWIZIGAME N'INGUZANYO",
    val sharePrice: Double = 500.0,
    val maxSharesPerWeek: Int = 8,
    val maxWeeklySavingsAmount: Double = 4000.0,
    val paymentCode: String = "18281412512#",
    val savingsStartDay: String = "Ku wa kabiri (Tuesday)",
    val savingsStartTime: String = "17:00",
    val savingsEndDay: String = "Ku wa gatatu (Wednesday)",
    val savingsEndTime: String = "15:00",
    val missedSavingsFine: Double = 100.0,
    val maxLoanPercentage: Double = 80.0
)
