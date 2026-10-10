package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.AnnouncementEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.FineEntity
import com.example.data.local.entity.GroupSettingsEntity
import com.example.data.local.entity.LoanEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PasswordResetEntity
import com.example.data.local.entity.RepaymentEntity
import com.example.data.local.entity.SavingsEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.schedule.SavingsScheduleHelper
import com.example.data.security.SecurityUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class IkibinaRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val savingsDao = db.savingsDao()
    private val fineDao = db.fineDao()
    private val loanDao = db.loanDao()
    private val repaymentDao = db.repaymentDao()
    private val transactionDao = db.transactionDao()
    private val notificationDao = db.notificationDao()
    private val announcementDao = db.announcementDao()
    private val chatMessageDao = db.chatMessageDao()
    private val passwordResetDao = db.passwordResetDao()
    private val settingsDao = db.groupSettingsDao()

    // ==========================================
    // INITIALIZATION & SEEDING
    // ==========================================

    suspend fun initializeDatabaseIfNeeded() {
        // 1. Group settings
        val settings = settingsDao.getSettingsSync()
        if (settings == null) {
            settingsDao.insertOrUpdate(GroupSettingsEntity())
        }

        // 2. Initial Admin Account (enockrds@gmail.com / Enock@123)
        // Rule: Create account only if initial administrator does not already exist.
        // Never create duplicate Admin accounts on repeated initialization.
        val existingAdmin = userDao.getUserByEmail("enockrds@gmail.com")
        if (existingAdmin == null) {
            val salt = SecurityUtils.generateSalt()
            val hash = SecurityUtils.hashPassword("Enock@123", salt)
            val admin = UserEntity(
                fullName = "Enock Rds",
                email = "enockrds@gmail.com",
                phone = "0788100100",
                passwordHash = hash,
                salt = salt,
                role = "ADMIN",
                status = "ACTIVE",
                savingsGroupName = "Ikibina cy'Ubwizigame n'Inguzanyo",
                mustChangePassword = true,
                createdAt = System.currentTimeMillis()
            )
            val adminId = userDao.insertUser(admin)

            // Seed initial group announcement
            announcementDao.insertAnnouncement(
                AnnouncementEntity(
                    title = "Murakaza neza muri IKIBINA",
                    content = "Kaze muri porogaramu y'itsinda ryacu ry'ubwizigame. Igihe cyo kuzigama ni buri wa kabiri 17:00 kugeza ku wa gatatu 15:00. Ubutumwa n'ibikorwa byose biragenzurwa n'ubuyobozi.",
                    authorName = "Enock Rds (Admin)",
                    isPinned = true
                )
            )

            // Seed welcome chat message
            chatMessageDao.insertMessage(
                ChatMessageEntity(
                    senderId = adminId,
                    senderName = "Enock Rds",
                    senderRole = "ADMIN",
                    message = "Murakaza neza mwese mu ruganiriro rw'itsinda ryacu rya IKIBINA! Buri wese yisanzure abaze ikibazo."
                )
            )
        }
    }

    // ==========================================
    // AUTHENTICATION & PROFILE
    // ==========================================

    suspend fun login(identifier: String, pass: String): Result<UserEntity> {
        val trimmed = identifier.trim()
        val user = userDao.getUserByIdentifier(trimmed)
            ?: return Result.failure(Exception("Konti ntibonetse. Reba email cyangwa nimero ya terefone."))

        val valid = SecurityUtils.verifyPassword(pass, user.salt, user.passwordHash)
        if (!valid) {
            return Result.failure(Exception("Ijambobanga (Passcode) ntabwo ari ryo."))
        }

        if (user.status == "REJECTED") {
            return Result.failure(Exception("Konti yawe ntiyemejwe n'ubuyobozi."))
        }
        if (user.status == "INACTIVE") {
            return Result.failure(Exception("Konti yawe yahagaritswe by'agateganyo."))
        }

        return Result.success(user)
    }

    suspend fun registerUser(
        fullName: String,
        email: String,
        phone: String,
        passcode: String,
        confirmPasscode: String,
        groupName: String = "Ikibina cy'Ubwizigame n'Inguzanyo"
    ): Result<String> {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim()
        val cleanPhone = SecurityUtils.normalizePhoneNumber(phone.trim())

        if (cleanName.length < 3) {
            return Result.failure(Exception("Amazina agomba kugira nibura inyuguti 3."))
        }
        if (!SecurityUtils.isValidRwandanPhone(cleanPhone)) {
            return Result.failure(Exception("Nimero ya telefone ntabwo yemewe mu Rwanda (Urugero: 0788123456)."))
        }
        if (passcode.length < 4) {
            return Result.failure(Exception("Passcode igomba kugira nibura imibare cyangwa inyuguti 4."))
        }
        if (passcode != confirmPasscode) {
            return Result.failure(Exception("Passcode zombi ntizihuye."))
        }

        // Check duplicates
        if (userDao.getUserByPhone(cleanPhone) != null) {
            return Result.failure(Exception("Iyi nimero ya telefone isanzwe ikoreshwa."))
        }
        if (cleanEmail.isNotEmpty() && userDao.getUserByEmail(cleanEmail) != null) {
            return Result.failure(Exception("Iyi email isanzwe ikoreshwa."))
        }

        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword(passcode, salt)

        val newUser = UserEntity(
            fullName = cleanName,
            email = if (cleanEmail.isNotEmpty()) cleanEmail else "$cleanPhone@ikibina.rw",
            phone = cleanPhone,
            passwordHash = hash,
            salt = salt,
            role = "MEMBER",
            status = "PENDING", // PENDING approval by Admin
            savingsGroupName = groupName,
            mustChangePassword = false,
            createdAt = System.currentTimeMillis()
        )

        val newId = userDao.insertUser(newUser)

        // Notify Admin
        notificationDao.insertNotification(
            NotificationEntity(
                recipientRole = "ADMIN",
                title = "Umubyeyi/Umunyamuryango mushya yiyandikishije !",
                message = "$cleanName ($cleanPhone) ategereje kwemezwa.",
                type = "REGISTRATION_PENDING",
                referenceId = newId
            )
        )

        return Result.success("Konti yakozwe neza. Tegereza Admin ayemeze.")
    }

    suspend fun changePassword(userId: Long, oldPass: String, newPass: String): Result<Unit> {
        val user = userDao.getUserByIdSync(userId)
            ?: return Result.failure(Exception("Umukoresha ntabonetse."))

        if (!SecurityUtils.verifyPassword(oldPass, user.salt, user.passwordHash)) {
            return Result.failure(Exception("Ijambobanga risanzwe ntabwo ari ryo."))
        }
        if (newPass.length < 4) {
            return Result.failure(Exception("Ijambobanga rishya rigomba kugira nibura imibare 4."))
        }

        val newSalt = SecurityUtils.generateSalt()
        val newHash = SecurityUtils.hashPassword(newPass, newSalt)
        userDao.updatePassword(userId, newHash, newSalt, mustChange = false)

        return Result.success(Unit)
    }

    suspend fun resetPasswordWithOtp(identifier: String, otp: String, newPass: String): Result<Unit> {
        val user = userDao.getUserByIdentifier(identifier.trim())
            ?: return Result.failure(Exception("Konti ntibonetse."))

        val resetReq = passwordResetDao.getActiveReset(identifier.trim())
            ?: return Result.failure(Exception("Nta kode yo gusubiramo ijambobanga ibonetse cyangwa yararangiye."))

        if (System.currentTimeMillis() > resetReq.expiresAt) {
            return Result.failure(Exception("Igihe cyo gukoresha kode cyararenze."))
        }
        if (resetReq.token != otp.trim()) {
            return Result.failure(Exception("Kode winjije ntabwo ari yo."))
        }

        val newSalt = SecurityUtils.generateSalt()
        val newHash = SecurityUtils.hashPassword(newPass, newSalt)
        userDao.updatePassword(user.id, newHash, newSalt, mustChange = false)
        passwordResetDao.invalidateResets(identifier.trim())

        return Result.success(Unit)
    }

    suspend fun initiatePasswordReset(identifier: String): Result<String> {
        val user = userDao.getUserByIdentifier(identifier.trim())
            ?: return Result.failure(Exception("Nta konti ifitanye isano n'iyi email cyangwa telefone."))

        val otp = SecurityUtils.generateRecoveryOtp()
        val expiresAt = System.currentTimeMillis() + (15 * 60 * 1000) // 15 mins

        passwordResetDao.invalidateResets(identifier.trim())
        passwordResetDao.insertReset(
            PasswordResetEntity(
                identifier = identifier.trim(),
                token = otp,
                expiresAt = expiresAt
            )
        )

        return Result.success(otp)
    }

    // ==========================================
    // MEMBER MANAGEMENT (ADMIN)
    // ==========================================

    fun getAllUsersFlow(): Flow<List<UserEntity>> = userDao.getAllUsers()
    fun getAllMembersFlow(): Flow<List<UserEntity>> = userDao.getAllMembers()
    fun getActiveMembersFlow(): Flow<List<UserEntity>> = userDao.getActiveMembers()
    fun getPendingMembersFlow(): Flow<List<UserEntity>> = userDao.getPendingMembers()
    fun getTotalMembersCount(): Flow<Int> = userDao.getTotalMembersCount()
    fun getActiveMembersCount(): Flow<Int> = userDao.getActiveMembersCount()
    fun getPendingMembersCount(): Flow<Int> = userDao.getPendingMembersCount()
    fun getUserFlow(userId: Long): Flow<UserEntity?> = userDao.getUserById(userId)

    suspend fun approveMember(adminId: Long, memberId: Long): Result<Unit> {
        userDao.updateMemberStatus(memberId, "ACTIVE", System.currentTimeMillis(), adminId)
        notificationDao.insertNotification(
            NotificationEntity(
                recipientRole = "MEMBER",
                recipientId = memberId,
                title = "Konti yawe yemejwe!",
                message = "Ubuyobozi bwemeje konti yawe. Ushobora noneho kuzigama no gusaba inguzanyo.",
                type = "REGISTRATION_APPROVED"
            )
        )
        return Result.success(Unit)
    }

    suspend fun rejectMember(adminId: Long, memberId: Long): Result<Unit> {
        userDao.updateMemberStatus(memberId, "REJECTED", System.currentTimeMillis(), adminId)
        return Result.success(Unit)
    }

    suspend fun toggleMemberStatus(adminId: Long, memberId: Long, makeActive: Boolean): Result<Unit> {
        val status = if (makeActive) "ACTIVE" else "INACTIVE"
        userDao.updateMemberStatus(memberId, status, System.currentTimeMillis(), adminId)
        return Result.success(Unit)
    }

    // ==========================================
    // SAVINGS & FINANCIAL RULES
    // ==========================================

    fun getAllSavingsFlow(): Flow<List<SavingsEntity>> = savingsDao.getAllSavings()
    fun getPendingSavingsFlow(): Flow<List<SavingsEntity>> = savingsDao.getPendingSavings()
    fun getMemberSavingsFlow(memberId: Long): Flow<List<SavingsEntity>> = savingsDao.getSavingsByMember(memberId)
    fun getTotalApprovedSavingsFlow(): Flow<Double> = savingsDao.getTotalApprovedSavings()
    fun getTotalPendingSavingsFlow(): Flow<Double> = savingsDao.getTotalPendingSavings()
    fun getMemberApprovedSavingsFlow(memberId: Long): Flow<Double> = savingsDao.getMemberApprovedSavings(memberId)
    fun getMemberPendingSavingsFlow(memberId: Long): Flow<Double> = savingsDao.getMemberPendingSavings(memberId)

    suspend fun submitSavings(
        memberId: Long,
        amount: Double,
        paymentReference: String = "",
        proofNote: String = ""
    ): Result<SavingsEntity> {
        val member = userDao.getUserByIdSync(memberId)
            ?: return Result.failure(Exception("Umunyamuryango ntabonetse."))

        if (member.status != "ACTIVE") {
            return Result.failure(Exception("Konti yawe iracyategereje kwemezwa na Admin."))
        }

        if (amount < 500.0) {
            return Result.failure(Exception("Amafaranga yo kwizigama agomba kuba nibura RWF 500."))
        }

        val sharesCount = (amount / 500.0).toInt().coerceAtLeast(1)
        val isWithinWindow = SavingsScheduleHelper.isWithinSavingsPeriod()
        val weekCode = SavingsScheduleHelper.getWeekCode()

        val refToUse = paymentReference.trim().ifEmpty { "USSD: *182*8*1*412512#" }

        val savings = SavingsEntity(
            memberId = memberId,
            memberName = member.fullName,
            amount = amount,
            sharesCount = sharesCount,
            weekCode = weekCode,
            submittedAt = System.currentTimeMillis(),
            isWithinScheduledPeriod = isWithinWindow,
            paymentCode = "*182*8*1*412512#",
            paymentReference = refToUse,
            proofNote = proofNote.trim(),
            status = "PENDING"
        )

        val id = savingsDao.insertSavings(savings)

        // Notify Admin
        notificationDao.insertNotification(
            NotificationEntity(
                recipientRole = "ADMIN",
                title = "Ubwizigame bitegerejwe kwemezwa !",
                message = "${member.fullName} — RWF ${amount.toInt()}",
                type = "SAVINGS_PENDING",
                referenceId = id
            )
        )

        // SCHEDULE & FINES RULE:
        // The scheduled savings period is Tuesday 17:00 through Wednesday 15:00 Rwanda time (Africa/Kigali).
        // If outside window, apply a fixed fine of RWF 100 per missed week, NOT per share.
        // Never fine more than once for the same week.
        if (!isWithinWindow) {
            val existingFine = fineDao.getFineForMemberWeek(memberId, weekCode)
            if (existingFine == null) {
                fineDao.insertFine(
                    FineEntity(
                        memberId = memberId,
                        memberName = member.fullName,
                        weekCode = weekCode,
                        amount = 100.0, // Fixed 100 RWF per missed period (NOT per share)
                        reason = "Kutazigama mu gihe cyagenwe (Ku wa kabiri 17:00 - Ku wa gatatu 15:00)",
                        status = "PENDING_PAYMENT"
                    )
                )
                notificationDao.insertNotification(
                    NotificationEntity(
                        recipientRole = "MEMBER",
                        recipientId = memberId,
                        title = "Igihano cyo gutinda kuzigama (RWF 100)",
                        message = "Wazigamye hanze y'igihe cyagenwe cy'icyumweru ($weekCode). Uciwe igihano cya RWF 100.",
                        type = "FINE_ASSESSED"
                    )
                )
            }
        }

        return Result.success(savings.copy(id = id))
    }

    suspend fun approveSavings(adminId: Long, savingsId: Long): Result<Unit> {
        val savings = savingsDao.getSavingsById(savingsId)
            ?: return Result.failure(Exception("Ubwizigame ntibubonetse."))

        if (savings.status == "APPROVED") {
            return Result.failure(Exception("Ubu bwizigame bwari bwasanzwe bwemejwe."))
        }

        val member = userDao.getUserByIdSync(savings.memberId)
            ?: return Result.failure(Exception("Umunyamuryango ntabonetse."))

        // Check if this was a late payment with an applicable RWF 100 fine
        if (!savings.isWithinScheduledPeriod) {
            val pendingFine = fineDao.getFineForMemberWeek(savings.memberId, savings.weekCode)
            if (pendingFine != null && pendingFine.status == "PENDING_PAYMENT") {
                // Shortfall check: payment amount must be greater than RWF 100 to cover fine and savings
                if (savings.amount <= 100.0) {
                    return Result.failure(
                        Exception(
                            "Amafaranga ntiyahagije: Igihano cyo gutinda ni RWF 100, bityo nta mafaranga asigaye yo gushyira mu bwizigame (Shortfall: RWF ${savings.amount.toInt()} <= RWF 100)."
                        )
                    )
                }

                val fineAmount = 100.0
                val creditedSavingsAmount = savings.amount - fineAmount

                // 1. Mark savings approved
                savingsDao.updateSavingsStatus(savingsId, "APPROVED", adminId, System.currentTimeMillis(), null)

                // 2. Separate fine payment allocation
                fineDao.markFinePaid(
                    pendingFine.id,
                    System.currentTimeMillis(),
                    savings.paymentReference.ifEmpty { "Late-Savings-Deduction" },
                    adminId,
                    System.currentTimeMillis()
                )

                // 3. Record fine collected transaction
                transactionDao.insertTransaction(
                    TransactionEntity(
                        memberId = savings.memberId,
                        memberName = savings.memberName,
                        type = "FINE_COLLECTED",
                        amount = fineAmount,
                        direction = "INFLOW",
                        description = "Igihano cyo gutinda cyishyuwe: ${savings.memberName} ($fineAmount RWF ku cyumweru ${savings.weekCode})",
                        referenceId = pendingFine.id,
                        performedBy = "Admin (ID: $adminId)"
                    )
                )

                // 4. Credit net amount to member's savings
                val newSavingsBalance = member.approvedSavingsBalance + creditedSavingsAmount
                userDao.updateUser(member.copy(approvedSavingsBalance = newSavingsBalance))

                // 5. Record savings transaction for net credited amount
                transactionDao.insertTransaction(
                    TransactionEntity(
                        memberId = savings.memberId,
                        memberName = savings.memberName,
                        type = "SAVINGS_DEPOSIT",
                        amount = creditedSavingsAmount,
                        direction = "INFLOW",
                        description = "Ubwizigame bwemejwe: ${savings.memberName} (Amafaranga: RWF ${savings.amount.toInt()}, Igihano: RWF 100, Ayinjijwe: RWF ${creditedSavingsAmount.toInt()})",
                        referenceId = savingsId,
                        performedBy = "Admin (ID: $adminId)"
                    )
                )

                // 6. Notify member clearly of the allocation breakdown
                notificationDao.insertNotification(
                    NotificationEntity(
                        recipientRole = "MEMBER",
                        recipientId = savings.memberId,
                        title = "Ubwizigame n'Igihano Byemejwe !",
                        message = "Amafaranga wishyuye: RWF ${savings.amount.toInt()} | Igihano cyo gutinda: RWF 100 | Ayinjijwe mu bwizigame: RWF ${creditedSavingsAmount.toInt()}.",
                        type = "SAVINGS_APPROVED",
                        referenceId = savingsId
                    )
                )

                return Result.success(Unit)
            }
        }

        // On-time payment or no pending fine: full amount credited to savings, no fine deducted!
        savingsDao.updateSavingsStatus(savingsId, "APPROVED", adminId, System.currentTimeMillis(), null)

        val newBalance = member.approvedSavingsBalance + savings.amount
        userDao.updateUser(member.copy(approvedSavingsBalance = newBalance))

        // Record financial audit transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                memberId = savings.memberId,
                memberName = savings.memberName,
                type = "SAVINGS_DEPOSIT",
                amount = savings.amount,
                direction = "INFLOW",
                description = "Ubwizigame bwemejwe: ${savings.memberName} (RWF ${savings.amount.toInt()})",
                referenceId = savingsId,
                performedBy = "Admin (ID: $adminId)"
            )
        )

        // Notify member
        notificationDao.insertNotification(
            NotificationEntity(
                recipientRole = "MEMBER",
                recipientId = savings.memberId,
                title = "Ubwizigame bwemejwe !",
                message = "Ubwizigame bwawe bwa RWF ${savings.amount.toInt()} bwemejwe na Admin (Nta gihano cyaciwe).",
                type = "SAVINGS_APPROVED",
                referenceId = savingsId
            )
        )

        return Result.success(Unit)
    }

    suspend fun rejectSavings(adminId: Long, savingsId: Long, reason: String): Result<Unit> {
        val savings = savingsDao.getSavingsById(savingsId)
            ?: return Result.failure(Exception("Ubwizigame ntibubonetse."))

        savingsDao.updateSavingsStatus(savingsId, "REJECTED", adminId, System.currentTimeMillis(), reason)

        notificationDao.insertNotification(
            NotificationEntity(
                recipientRole = "MEMBER",
                recipientId = savings.memberId,
                title = "Ubwizigame bwanze !",
                message = "Ubwizigame bwawe bwa RWF ${savings.amount.toInt()} bwanze. Impamvu: $reason",
                type = "SAVINGS_REJECTED",
                referenceId = savingsId
            )
        )

        return Result.success(Unit)
    }

    // ==========================================
    // FINES & AUDIT
    // ==========================================

    fun getAllFinesFlow(): Flow<List<FineEntity>> = fineDao.getAllFines()
    fun getMemberFinesFlow(memberId: Long): Flow<List<FineEntity>> = fineDao.getFinesByMember(memberId)
    fun getTotalCollectedFinesFlow(): Flow<Double> = fineDao.getTotalCollectedFines()
    fun getTotalPendingFinesFlow(): Flow<Double> = fineDao.getTotalPendingFines()
    fun getMemberOutstandingFinesFlow(memberId: Long): Flow<Double> = fineDao.getMemberOutstandingFines(memberId)

    suspend fun payFine(fineId: Long, reference: String, adminId: Long?): Result<Unit> {
        fineDao.markFinePaid(fineId, System.currentTimeMillis(), reference, adminId, System.currentTimeMillis())

        // Insert transaction for collected fine (Inflow into communal treasury)
        transactionDao.insertTransaction(
            TransactionEntity(
                memberId = null,
                memberName = "Umunyamuryango",
                type = "FINE_COLLECTED",
                amount = 100.0,
                direction = "INFLOW",
                description = "Igihano cyishyuwe: RWF 100 (Ref: $reference)",
                referenceId = fineId,
                performedBy = if (adminId != null) "Admin (ID: $adminId)" else "Sistemu"
            )
        )

        return Result.success(Unit)
    }

    suspend fun waiveFine(adminId: Long, fineId: Long): Result<Unit> {
        fineDao.waiveFine(fineId, adminId, System.currentTimeMillis())
        return Result.success(Unit)
    }

    // ==========================================
    // COMMUNAL MONEY & "IMARI ISHYUSYE"
    // ==========================================

    /**
     * Calculates the actual communal available money from approved database records:
     * (Approved Savings + Collected Fines + Approved Loan Repayments) - Total Principal Disbursed
     */
    fun getCommunalAvailableMoneyFlow(): Flow<Double> {
        return combine(
            savingsDao.getTotalApprovedSavings(),
            fineDao.getTotalCollectedFines(),
            repaymentDao.getTotalApprovedRepayments(),
            loanDao.getTotalPrincipalDisbursed()
        ) { savings, fines, repayments, disbursedLoans ->
            val pool = (savings + fines + repayments) - disbursedLoans
            if (pool < 0.0) 0.0 else pool
        }
    }

    // ==========================================
    // LOANS (SABA INGUZANYA) & REPAYMENTS
    // ==========================================

    fun getAllLoansFlow(): Flow<List<LoanEntity>> = loanDao.getAllLoans()
    fun getPendingLoansFlow(): Flow<List<LoanEntity>> = loanDao.getPendingLoans()
    fun getActiveLoansFlow(): Flow<List<LoanEntity>> = loanDao.getActiveLoans()
    fun getLoansByMemberFlow(memberId: Long): Flow<List<LoanEntity>> = loanDao.getLoansByMember(memberId)
    fun getActiveLoansCountFlow(): Flow<Int> = loanDao.getActiveLoansCount()
    fun getPendingLoansCountFlow(): Flow<Int> = loanDao.getPendingLoansCount()
    fun getTotalOutstandingLoansBalanceFlow(): Flow<Double> = loanDao.getTotalOutstandingLoansBalance()
    fun getMemberActiveLoanBalanceFlow(memberId: Long): Flow<Double> = loanDao.getMemberActiveLoanBalance(memberId)

    suspend fun requestLoan(
        memberId: Long,
        amount: Double,
        reason: String,
        durationWeeks: Int = 4
    ): Result<LoanEntity> {
        val member = userDao.getUserByIdSync(memberId)
            ?: return Result.failure(Exception("Umunyamuryango ntabonetse."))

        if (member.status != "ACTIVE") {
            return Result.failure(Exception("Konti yawe igomba kuba yemewe (ACTIVE) kugira ngo usabe inguzanyo."))
        }

        // Rule: Prevent multiple active loans for a member
        val existingActive = loanDao.getActiveLoanForMember(memberId)
        if (existingActive != null) {
            return Result.failure(Exception("Ufite indi nguzanyo ikiri gukora (Asigaye: RWF ${existingActive.outstandingBalance.toInt()}). Banze uyishyure."))
        }
        val existingPending = loanDao.getPendingLoanForMember(memberId)
        if (existingPending != null) {
            return Result.failure(Exception("Ufite ubundi busabe bw'inguzanyo buri gusuzumwa."))
        }

        if (amount < 1000.0) {
            return Result.failure(Exception("Inguzanyo ntoya ishobora gusabwa ni RWF 1,000."))
        }

        // Rule: Maximum loan amount is 80% of communal available money!
        // Rule: Members must be able to request loan even if personal savings is RWF 0!
        val currentCommunalPool = getCommunalAvailableMoneyFlow().first()
        val maxAllowed = currentCommunalPool * 0.80

        if (amount > maxAllowed) {
            return Result.failure(
                Exception(
                    "Amafaranga usabye arenze 80% by'amafaranga asigaye muri rusange. Amafaranga ahari ni RWF ${currentCommunalPool.toInt()}, ntushobora kurenza RWF ${maxAllowed.toInt()}."
                )
            )
        }

        val interestPercent = 5.0
        val interest = amount * (interestPercent / 100.0)
        val totalDue = amount + interest

        val loan = LoanEntity(
            memberId = memberId,
            memberName = member.fullName,
            amountRequested = amount,
            amountApproved = 0.0,
            interestRatePercent = interestPercent,
            interestAmount = interest,
            totalDue = totalDue,
            amountPaid = 0.0,
            outstandingBalance = totalDue,
            reason = reason.trim(),
            repaymentDurationWeeks = durationWeeks,
            status = "PENDING"
        )

        val id = loanDao.insertLoan(loan)

        // Notify Admin
        notificationDao.insertNotification(
            NotificationEntity(
                recipientRole = "ADMIN",
                title = "Ubusabe bw'inguzanyo nshya !",
                message = "${member.fullName} asabye inguzanyo ya RWF ${amount.toInt()}",
                type = "LOAN_PENDING",
                referenceId = id
            )
        )

        return Result.success(loan.copy(id = id))
    }

    suspend fun approveLoan(adminId: Long, loanId: Long): Result<Unit> {
        val loan = loanDao.getLoanById(loanId)
            ?: return Result.failure(Exception("Inguzanyo ntibonetse."))

        if (loan.status != "PENDING") {
            return Result.failure(Exception("Iyi nguzanyo ntiri mu bitegereje kwemezwa."))
        }

        val currentCommunalPool = getCommunalAvailableMoneyFlow().first()
        if (loan.amountRequested > currentCommunalPool) {
            return Result.failure(Exception("Amafaranga asigaye mu kigega ntabwo ahagije yo gutanga iyi nguzanyo."))
        }

        val approvedAmount = loan.amountRequested
        val totalDue = approvedAmount + (approvedAmount * (loan.interestRatePercent / 100.0))

        loanDao.reviewLoan(
            id = loanId,
            status = "ACTIVE",
            approvedAmount = approvedAmount,
            totalDue = totalDue,
            reviewedBy = adminId,
            reviewedAt = System.currentTimeMillis(),
            reason = null
        )

        // Disburse loan: Outflow transaction from communal treasury
        transactionDao.insertTransaction(
            TransactionEntity(
                memberId = loan.memberId,
                memberName = loan.memberName,
                type = "LOAN_DISBURSED",
                amount = approvedAmount,
                direction = "OUTFLOW",
                description = "Inguzanyo yatanzwe kuri ${loan.memberName} (RWF ${approvedAmount.toInt()})",
                referenceId = loanId,
                performedBy = "Admin (ID: $adminId)"
            )
        )

        notificationDao.insertNotification(
            NotificationEntity(
                recipientRole = "MEMBER",
                recipientId = loan.memberId,
                title = "Inguzanyo yemejwe !",
                message = "Inguzanyo yawe ya RWF ${approvedAmount.toInt()} yemerewe na Admin. Yishyurwe mbere y'igihe cyemeranyijweho.",
                type = "LOAN_APPROVED",
                referenceId = loanId
            )
        )

        return Result.success(Unit)
    }

    suspend fun rejectLoan(adminId: Long, loanId: Long, reason: String): Result<Unit> {
        val loan = loanDao.getLoanById(loanId)
            ?: return Result.failure(Exception("Inguzanyo ntibonetse."))

        loanDao.reviewLoan(
            id = loanId,
            status = "REJECTED",
            approvedAmount = 0.0,
            totalDue = 0.0,
            reviewedBy = adminId,
            reviewedAt = System.currentTimeMillis(),
            reason = reason
        )

        notificationDao.insertNotification(
            NotificationEntity(
                recipientRole = "MEMBER",
                recipientId = loan.memberId,
                title = "Inguzanyo yanzwe !",
                message = "Ubusabe bwawe bw'inguzanyo ya RWF ${loan.amountRequested.toInt()} bwanze. Impamvu: $reason",
                type = "LOAN_REJECTED",
                referenceId = loanId
            )
        )

        return Result.success(Unit)
    }

    // ==========================================
    // REPAYMENTS (KWISHYURA INGUZANYO)
    // ==========================================

    fun getAllRepaymentsFlow(): Flow<List<RepaymentEntity>> = repaymentDao.getAllRepayments()
    fun getPendingRepaymentsFlow(): Flow<List<RepaymentEntity>> = repaymentDao.getPendingRepayments()
    fun getMemberRepaymentsFlow(memberId: Long): Flow<List<RepaymentEntity>> = repaymentDao.getRepaymentsByMember(memberId)
    fun getPendingRepaymentsCountFlow(): Flow<Int> = repaymentDao.getPendingRepaymentsCount()
    fun getTotalApprovedRepaymentsFlow(): Flow<Double> = repaymentDao.getTotalApprovedRepayments()

    suspend fun submitRepayment(
        memberId: Long,
        loanId: Long,
        amount: Double,
        paymentType: String = "PRINCIPAL",
        paymentRef: String = ""
    ): Result<RepaymentEntity> {
        val loan = loanDao.getLoanById(loanId)
            ?: return Result.failure(Exception("Inguzanyo ntibonetse."))

        if (loan.status != "ACTIVE") {
            return Result.failure(Exception("Iyi nguzanyo ntabwo igikora cyangwa yaramaze kwishyurwa."))
        }

        if (amount <= 0.0) {
            return Result.failure(Exception("Amafaranga agomba kuba arenze 0."))
        }

        val type = if (paymentType.equals("INTEREST", ignoreCase = true)) "INTEREST" else "PRINCIPAL"

        if (type == "PRINCIPAL") {
            if (amount > loan.outstandingPrincipal) {
                return Result.failure(
                    Exception("Amafaranga arenze igishoro cy'inguzanyo gisigaye (Asigaye: RWF ${loan.outstandingPrincipal.toInt()}).")
                )
            }
        } else {
            if (amount > loan.outstandingInterest) {
                return Result.failure(
                    Exception("Amafaranga arenze inyungu y'inguzanyo isigaye (Asigaye: RWF ${loan.outstandingInterest.toInt()}).")
                )
            }
        }

        val refToUse = paymentRef.trim().ifEmpty { "USSD: *182*8*1*412512#" }

        val repayment = RepaymentEntity(
            loanId = loanId,
            memberId = memberId,
            memberName = loan.memberName,
            amount = amount,
            principalPortion = if (type == "PRINCIPAL") amount else 0.0,
            interestPortion = if (type == "INTEREST") amount else 0.0,
            paymentType = type,
            paymentReference = refToUse,
            status = "PENDING"
        )

        val id = repaymentDao.insertRepayment(repayment)

        // Notify Admin
        notificationDao.insertNotification(
            NotificationEntity(
                recipientRole = "ADMIN",
                title = if (type == "INTEREST") "Ubwishyu bw'inyungu butegerejwe kwemezwa !" else "Ubwishyu bw'inguzanyo butegerejwe kwemezwa !",
                message = "${loan.memberName} — RWF ${amount.toInt()} ($type)",
                type = "REPAYMENT_PENDING",
                referenceId = id
            )
        )

        return Result.success(repayment.copy(id = id))
    }

    suspend fun approveRepayment(adminId: Long, repaymentId: Long): Result<Unit> {
        val repayment = repaymentDao.getRepaymentById(repaymentId)
            ?: return Result.failure(Exception("Ubwishyu ntibubonetse."))

        if (repayment.status != "PENDING") {
            return Result.failure(Exception("Ubu bwishyu bwari bwasanzwe bwarakozweho."))
        }

        repaymentDao.updateRepaymentStatus(repaymentId, "APPROVED", adminId, System.currentTimeMillis())

        if (repayment.paymentType == "INTEREST") {
            // Strictly apply interest payment - does NOT reduce outstanding principal!
            loanDao.applyInterestRepayment(repayment.loanId, repayment.amount)

            transactionDao.insertTransaction(
                TransactionEntity(
                    memberId = repayment.memberId,
                    memberName = repayment.memberName,
                    type = "LOAN_INTEREST_PAYMENT",
                    amount = repayment.amount,
                    direction = "INFLOW",
                    description = "Inyungu y'inguzanyo yishyuwe: ${repayment.memberName} (RWF ${repayment.amount.toInt()})",
                    referenceId = repaymentId,
                    performedBy = "Admin (ID: $adminId)"
                )
            )

            notificationDao.insertNotification(
                NotificationEntity(
                    recipientRole = "MEMBER",
                    recipientId = repayment.memberId,
                    title = "Inyungu y'Inguzanyo Yemejwe !",
                    message = "Kwishyura inyungu y'inguzanyo ya RWF ${repayment.amount.toInt()} kwemejwe na Admin. (Igishoro cy'inguzanyo ntabwo cyagabanyijwe).",
                    type = "REPAYMENT_APPROVED",
                    referenceId = repaymentId
                )
            )
        } else {
            // Apply principal repayment - reduces loan principal balance
            loanDao.applyPrincipalRepayment(repayment.loanId, repayment.amount)

            transactionDao.insertTransaction(
                TransactionEntity(
                    memberId = repayment.memberId,
                    memberName = repayment.memberName,
                    type = "LOAN_PRINCIPAL_REPAYMENT",
                    amount = repayment.amount,
                    direction = "INFLOW",
                    description = "Igishoro cy'inguzanyo cyishyuwe: ${repayment.memberName} (RWF ${repayment.amount.toInt()})",
                    referenceId = repaymentId,
                    performedBy = "Admin (ID: $adminId)"
                )
            )

            notificationDao.insertNotification(
                NotificationEntity(
                    recipientRole = "MEMBER",
                    recipientId = repayment.memberId,
                    title = "Ubwishyu bw'Inguzanyo Bwemejwe !",
                    message = "Kwishyura igishoro cy'inguzanyo cya RWF ${repayment.amount.toInt()} kwemejwe na Admin.",
                    type = "REPAYMENT_APPROVED",
                    referenceId = repaymentId
                )
            )
        }

        return Result.success(Unit)
    }

    suspend fun rejectRepayment(adminId: Long, repaymentId: Long): Result<Unit> {
        val repayment = repaymentDao.getRepaymentById(repaymentId)
            ?: return Result.failure(Exception("Ubwishyu ntibubonetse."))

        repaymentDao.updateRepaymentStatus(repaymentId, "REJECTED", adminId, System.currentTimeMillis())

        notificationDao.insertNotification(
            NotificationEntity(
                recipientRole = "MEMBER",
                recipientId = repayment.memberId,
                title = "Ubwishyu bwanze !",
                message = "Kwishyura inguzanyo (${repayment.paymentType}) kwa RWF ${repayment.amount.toInt()} bwanze na Admin.",
                type = "REPAYMENT_REJECTED",
                referenceId = repaymentId
            )
        )

        return Result.success(Unit)
    }

    // ==========================================
    // NOTIFICATIONS, ANNOUNCEMENTS & CHAT
    // ==========================================

    fun getAdminNotificationsFlow(): Flow<List<NotificationEntity>> = notificationDao.getAdminNotifications()
    fun getMemberNotificationsFlow(memberId: Long): Flow<List<NotificationEntity>> = notificationDao.getMemberNotifications(memberId)
    fun getAdminUnreadCountFlow(): Flow<Int> = notificationDao.getAdminUnreadCount()
    fun getMemberUnreadCountFlow(memberId: Long): Flow<Int> = notificationDao.getMemberUnreadCount(memberId)
    suspend fun markNotificationRead(id: Long) = notificationDao.markAsRead(id)
    suspend fun markAllAdminNotificationsRead() = notificationDao.markAllAdminRead()
    suspend fun markAllMemberNotificationsRead(memberId: Long) = notificationDao.markAllMemberRead(memberId)

    fun getAllAnnouncementsFlow(): Flow<List<AnnouncementEntity>> = announcementDao.getAllAnnouncements()
    fun getPublishedMemberAnnouncementsFlow(): Flow<List<AnnouncementEntity>> = announcementDao.getPublishedMemberAnnouncements()
    fun getLatestAnnouncementFlow(): Flow<AnnouncementEntity?> = announcementDao.getLatestAnnouncement()

    suspend fun createAnnouncement(
        title: String,
        content: String,
        authorName: String,
        imageUrl: String = "",
        isVisibleToMembers: Boolean = true,
        startDate: String = "",
        endDate: String = ""
    ): Result<Unit> {
        announcementDao.insertAnnouncement(
            AnnouncementEntity(
                title = title.trim(),
                content = content.trim(),
                authorName = authorName,
                isPinned = true,
                imageUrl = imageUrl.trim(),
                isPublished = true,
                isVisibleToMembers = isVisibleToMembers,
                startDate = startDate.trim(),
                endDate = endDate.trim()
            )
        )
        return Result.success(Unit)
    }

    suspend fun updateAnnouncement(announcement: AnnouncementEntity): Result<Unit> {
        announcementDao.updateAnnouncement(announcement)
        return Result.success(Unit)
    }

    suspend fun toggleAnnouncementPublish(id: Long, isPublished: Boolean): Result<Unit> {
        announcementDao.togglePublishStatus(id, isPublished)
        return Result.success(Unit)
    }

    suspend fun toggleAnnouncementVisibility(id: Long, isVisible: Boolean): Result<Unit> {
        announcementDao.toggleVisibilityStatus(id, isVisible)
        return Result.success(Unit)
    }

    suspend fun deleteAnnouncement(id: Long): Result<Unit> {
        announcementDao.deleteAnnouncement(id)
        return Result.success(Unit)
    }

    fun getAllChatMessagesFlow(): Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages()
    suspend fun sendChatMessage(senderId: Long, senderName: String, senderRole: String, message: String): Result<Unit> {
        if (message.isBlank()) return Result.failure(Exception("Ubutumwa ntibushobora kuba ubusa."))
        chatMessageDao.insertMessage(
            ChatMessageEntity(
                senderId = senderId,
                senderName = senderName,
                senderRole = senderRole,
                message = message.trim()
            )
        )
        return Result.success(Unit)
    }

    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    fun getMemberTransactionsFlow(memberId: Long): Flow<List<TransactionEntity>> = transactionDao.getTransactionsByMember(memberId)
    fun getGroupSettingsFlow(): Flow<GroupSettingsEntity?> = settingsDao.getSettings()
}
