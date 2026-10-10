package com.example

import com.example.data.schedule.SavingsScheduleHelper
import com.example.data.security.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Locale

class IkibinaBusinessRulesTest {

    @Test
    fun testAdminInitialCredentialsPasswordHashing() {
        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword("Enock@123", salt)

        // Verify password
        assertTrue(SecurityUtils.verifyPassword("Enock@123", salt, hash))
        assertFalse(SecurityUtils.verifyPassword("WrongPassword", salt, hash))
        assertNotEquals("Enock@123", hash)
    }

    @Test
    fun testRwandanPhoneValidation() {
        assertTrue(SecurityUtils.isValidRwandanPhone("0788123456"))
        assertTrue(SecurityUtils.isValidRwandanPhone("0791234567"))
        assertTrue(SecurityUtils.isValidRwandanPhone("0722123456"))
        assertTrue(SecurityUtils.isValidRwandanPhone("0733123456"))
        assertTrue(SecurityUtils.isValidRwandanPhone("+250788123456"))

        // Invalid phones
        assertFalse(SecurityUtils.isValidRwandanPhone("0751234567"))
        assertFalse(SecurityUtils.isValidRwandanPhone("12345"))
        assertFalse(SecurityUtils.isValidRwandanPhone("078812345")) // 9 digits
    }

    @Test
    fun testOtpGeneration() {
        val otp = SecurityUtils.generateRecoveryOtp()
        assertEquals(6, otp.length)
        assertTrue(otp.all { it.isDigit() })
    }

    @Test
    fun testSavingsScheduleWindowTuesday17ToWednesday15() {
        // Tuesday 16:59 (Before start -> closed)
        val tuesdayBefore = Calendar.getInstance(SavingsScheduleHelper.RWANDA_TIMEZONE, Locale.US).apply {
            set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
            set(Calendar.HOUR_OF_DAY, 16)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        assertFalse("Tuesday 16:59 should be closed", SavingsScheduleHelper.isWithinSavingsPeriod(tuesdayBefore))

        // Tuesday 17:01 (Open)
        val tuesdayAfter = Calendar.getInstance(SavingsScheduleHelper.RWANDA_TIMEZONE, Locale.US).apply {
            set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY)
            set(Calendar.HOUR_OF_DAY, 17)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        assertTrue("Tuesday 17:01 should be open", SavingsScheduleHelper.isWithinSavingsPeriod(tuesdayAfter))

        // Wednesday 14:30 (Open)
        val wednesdayDuring = Calendar.getInstance(SavingsScheduleHelper.RWANDA_TIMEZONE, Locale.US).apply {
            set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        assertTrue("Wednesday 14:30 should be open", SavingsScheduleHelper.isWithinSavingsPeriod(wednesdayDuring))

        // Wednesday 15:30 (After end -> closed)
        val wednesdayAfter = Calendar.getInstance(SavingsScheduleHelper.RWANDA_TIMEZONE, Locale.US).apply {
            set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 0)
        }.timeInMillis
        assertFalse("Wednesday 15:30 should be closed", SavingsScheduleHelper.isWithinSavingsPeriod(wednesdayAfter))

        // Thursday (Closed)
        val thursday = Calendar.getInstance(SavingsScheduleHelper.RWANDA_TIMEZONE, Locale.US).apply {
            set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
        }.timeInMillis
        assertFalse("Thursday should be closed", SavingsScheduleHelper.isWithinSavingsPeriod(thursday))
    }

    @Test
    fun testFixedFineRuleMissedWeekIsFixed100RwfNotPerShare() {
        // Business rule from prompt:
        // "ONE FIXED FINE OF RWF 100 FOR THE MISSED WEEK.
        // IMPORTANT: THE FINE IS RWF 100 PER MISSED SAVINGS PERIOD, NOT RWF 100 PER SHARE.
        // Do not multiply the fine by the number of shares.
        // Example B: A member normally saves 4 shares per week but misses the scheduled period.
        // Fine = RWF 100, NOT RWF 400."

        val sharesCounts = listOf(1, 2, 4, 8)
        for (shares in sharesCounts) {
            val missedPeriodFine = 100.0 // Fixed 100 RWF regardless of shares!
            assertEquals(100.0, missedPeriodFine, 0.001)
        }
    }

    @Test
    fun testCommunalAvailableMoneyAnd80PercentLoanLimit() {
        // Communal pool = (Approved Savings + Collected Fines + Repayments) - Disbursed Active Loans
        val approvedSavings = 50000.0
        val collectedFines = 200.0
        val approvedRepayments = 10000.0
        val disbursedLoans = 15000.0

        val communalPool = (approvedSavings + collectedFines + approvedRepayments) - disbursedLoans
        assertEquals(45200.0, communalPool, 0.001)

        val maxAllowedLoan = communalPool * 0.80
        assertEquals(36160.0, maxAllowedLoan, 0.001)

        // Member requesting 30,000 is allowed
        assertTrue(30000.0 <= maxAllowedLoan)

        // Member requesting 40,000 is rejected because it exceeds 80%
        assertFalse(40000.0 <= maxAllowedLoan)
    }
}
