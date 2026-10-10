package com.example.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale

object SecurityUtils {

    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return saltBytes.joinToString("") { "%02x".format(it) }
    }

    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val combined = "$salt:$password"
        val hashBytes = md.digest(combined.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val calculated = hashPassword(password, salt)
        return calculated.equals(expectedHash, ignoreCase = true)
    }

    fun generateRecoveryOtp(): String {
        val random = SecureRandom()
        val num = 100000 + random.nextInt(900000)
        return num.toString()
    }

    /**
     * Validate Rwandan phone number format.
     * Accepted formats:
     * - 078XXXXXXX, 079XXXXXXX, 072XXXXXXX, 073XXXXXXX (10 digits starting with 07)
     * - +25078XXXXXXX or 25078XXXXXXX
     */
    fun normalizePhoneNumber(phone: String): String {
        val clean = phone.replace("\\s+".toRegex(), "").replace("-", "")
        return when {
            clean.startsWith("+250") -> "0" + clean.substring(4)
            clean.startsWith("250") && clean.length == 12 -> "0" + clean.substring(3)
            else -> clean
        }
    }

    fun isValidRwandanPhone(phone: String): Boolean {
        val normalized = normalizePhoneNumber(phone)
        // Valid Rwanda mobile prefixes: 078, 079 (MTN), 072, 073 (Airtel)
        val regex = "^(078|079|072|073)\\d{7}$".toRegex()
        return regex.matches(normalized)
    }
}
