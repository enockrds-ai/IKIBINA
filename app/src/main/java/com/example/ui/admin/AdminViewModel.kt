package com.example.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.AnnouncementEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.FineEntity
import com.example.data.local.entity.GroupSettingsEntity
import com.example.data.local.entity.LoanEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.RepaymentEntity
import com.example.data.local.entity.SavingsEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.IkibinaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdminDashboardSummary(
    val totalRegisteredMembers: Int = 0,
    val activeMembersCount: Int = 0,
    val pendingMembersCount: Int = 0,
    val totalApprovedSavings: Double = 0.0,
    val totalPendingSavings: Double = 0.0,
    val communalAvailableMoney: Double = 0.0,
    val activeLoansCount: Int = 0,
    val totalOutstandingLoansBalance: Double = 0.0,
    val pendingLoansCount: Int = 0,
    val pendingRepaymentsCount: Int = 0,
    val totalApprovedRepayments: Double = 0.0,
    val totalCollectedFines: Double = 0.0
)

class AdminViewModel(private val repository: IkibinaRepository) : ViewModel() {

    val allMembers: StateFlow<List<UserEntity>> = repository.getAllMembersFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pendingMembers: StateFlow<List<UserEntity>> = repository.getPendingMembersFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allSavings: StateFlow<List<SavingsEntity>> = repository.getAllSavingsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pendingSavings: StateFlow<List<SavingsEntity>> = repository.getPendingSavingsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allLoans: StateFlow<List<LoanEntity>> = repository.getAllLoansFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pendingLoans: StateFlow<List<LoanEntity>> = repository.getPendingLoansFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeLoans: StateFlow<List<LoanEntity>> = repository.getActiveLoansFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allRepayments: StateFlow<List<RepaymentEntity>> = repository.getAllRepaymentsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val pendingRepayments: StateFlow<List<RepaymentEntity>> = repository.getPendingRepaymentsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allFines: StateFlow<List<FineEntity>> = repository.getAllFinesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.getAllTransactionsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val adminNotifications: StateFlow<List<NotificationEntity>> = repository.getAdminNotificationsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val unreadCount: StateFlow<Int> = repository.getAdminUnreadCountFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val announcements: StateFlow<List<AnnouncementEntity>> = repository.getAllAnnouncementsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val latestAnnouncement: StateFlow<AnnouncementEntity?> = repository.getLatestAnnouncementFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val communalMoney: StateFlow<Double> = repository.getCommunalAvailableMoneyFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalApprovedSavings: StateFlow<Double> = repository.getTotalApprovedSavingsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalPendingSavings: StateFlow<Double> = repository.getTotalPendingSavingsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalCollectedFines: StateFlow<Double> = repository.getTotalCollectedFinesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalOutstandingLoans: StateFlow<Double> = repository.getTotalOutstandingLoansBalanceFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalApprovedRepayments: StateFlow<Double> = repository.getTotalApprovedRepaymentsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val settings: StateFlow<GroupSettingsEntity?> = repository.getGroupSettingsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.getAllChatMessagesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun approveMember(adminId: Long, memberId: Long) {
        viewModelScope.launch {
            repository.approveMember(adminId, memberId)
            _message.value = "Umunyamuryango yemejwe neza."
        }
    }

    fun rejectMember(adminId: Long, memberId: Long) {
        viewModelScope.launch {
            repository.rejectMember(adminId, memberId)
            _message.value = "Ubusabe bw'umunyamuryango bwanze."
        }
    }

    fun toggleMemberStatus(adminId: Long, memberId: Long, makeActive: Boolean) {
        viewModelScope.launch {
            repository.toggleMemberStatus(adminId, memberId, makeActive)
            _message.value = if (makeActive) "Umunyamuryango yongeye gukora." else "Umunyamuryango yahagaritswe."
        }
    }

    fun approveSavings(adminId: Long, savingsId: Long) {
        viewModelScope.launch {
            val res = repository.approveSavings(adminId, savingsId)
            res.fold(
                onSuccess = { _message.value = "Ubwizigame bwemejwe neza." },
                onFailure = { _message.value = it.message }
            )
        }
    }

    fun rejectSavings(adminId: Long, savingsId: Long, reason: String = "Ntibyahuye na MoMo") {
        viewModelScope.launch {
            val res = repository.rejectSavings(adminId, savingsId, reason)
            res.fold(
                onSuccess = { _message.value = "Ubwizigame bwanze." },
                onFailure = { _message.value = it.message }
            )
        }
    }

    fun approveLoan(adminId: Long, loanId: Long) {
        viewModelScope.launch {
            val res = repository.approveLoan(adminId, loanId)
            res.fold(
                onSuccess = { _message.value = "Inguzanyo yemerewe neza." },
                onFailure = { _message.value = it.message }
            )
        }
    }

    fun rejectLoan(adminId: Long, loanId: Long, reason: String = "Amafaranga mu kigega ntiyahagije") {
        viewModelScope.launch {
            val res = repository.rejectLoan(adminId, loanId, reason)
            res.fold(
                onSuccess = { _message.value = "Inguzanyo yanzwe." },
                onFailure = { _message.value = it.message }
            )
        }
    }

    fun approveRepayment(adminId: Long, repaymentId: Long) {
        viewModelScope.launch {
            val res = repository.approveRepayment(adminId, repaymentId)
            res.fold(
                onSuccess = { _message.value = "Ubwishyu bwemejwe neza." },
                onFailure = { _message.value = it.message }
            )
        }
    }

    fun rejectRepayment(adminId: Long, repaymentId: Long) {
        viewModelScope.launch {
            val res = repository.rejectRepayment(adminId, repaymentId)
            res.fold(
                onSuccess = { _message.value = "Ubwishyu bwanze." },
                onFailure = { _message.value = it.message }
            )
        }
    }

    fun markFinePaid(fineId: Long, adminId: Long, ref: String) {
        viewModelScope.launch {
            repository.payFine(fineId, ref, adminId)
            _message.value = "Igihano cyemejwe ko cyishyuwe."
        }
    }

    fun waiveFine(adminId: Long, fineId: Long) {
        viewModelScope.launch {
            repository.waiveFine(adminId, fineId)
            _message.value = "Igihano cyahawe imbabazi."
        }
    }

    fun publishAnnouncement(
        title: String,
        content: String,
        authorName: String,
        imageUrl: String = "",
        isVisibleToMembers: Boolean = true,
        startDate: String = "",
        endDate: String = ""
    ) {
        viewModelScope.launch {
            repository.createAnnouncement(title, content, authorName, imageUrl, isVisibleToMembers, startDate, endDate)
            _message.value = "Itangazo / Kwamamaza byatangajwe neza."
        }
    }

    fun updateAnnouncement(announcement: AnnouncementEntity) {
        viewModelScope.launch {
            repository.updateAnnouncement(announcement)
            _message.value = "Itangazo ryahinduwe neza."
        }
    }

    fun toggleAnnouncementPublish(id: Long, isPublished: Boolean) {
        viewModelScope.launch {
            repository.toggleAnnouncementPublish(id, isPublished)
            _message.value = if (isPublished) "Itangazo ryashyizwe ahagaragara." else "Itangazo ryahishwe."
        }
    }

    fun toggleAnnouncementVisibility(id: Long, isVisible: Boolean) {
        viewModelScope.launch {
            repository.toggleAnnouncementVisibility(id, isVisible)
            _message.value = if (isVisible) "Itangazo riragaragara ku banyamuryango." else "Itangazo ntirigaragara ku banyamuryango."
        }
    }

    fun deleteAnnouncement(id: Long) {
        viewModelScope.launch {
            repository.deleteAnnouncement(id)
            _message.value = "Itangazo ryasibwe neza."
        }
    }

    fun sendChatMessage(adminId: Long, adminName: String, text: String) {
        viewModelScope.launch {
            repository.sendChatMessage(adminId, adminName, "ADMIN", text)
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllAdminNotificationsRead()
        }
    }
}
