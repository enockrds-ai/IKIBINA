package com.example.ui.member

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.AnnouncementEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.FineEntity
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

class MemberViewModel(
    private val repository: IkibinaRepository,
    val memberId: Long
) : ViewModel() {

    val memberUser: StateFlow<UserEntity?> = repository.getUserFlow(memberId)
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val personalSavings: StateFlow<List<SavingsEntity>> = repository.getMemberSavingsFlow(memberId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val personalApprovedSavings: StateFlow<Double> = repository.getMemberApprovedSavingsFlow(memberId)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val personalPendingSavings: StateFlow<Double> = repository.getMemberPendingSavingsFlow(memberId)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val communalMoney: StateFlow<Double> = repository.getCommunalAvailableMoneyFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val totalCommunalSavings: StateFlow<Double> = repository.getTotalApprovedSavingsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val personalLoans: StateFlow<List<LoanEntity>> = repository.getLoansByMemberFlow(memberId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeLoanBalance: StateFlow<Double> = repository.getMemberActiveLoanBalanceFlow(memberId)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val personalRepayments: StateFlow<List<RepaymentEntity>> = repository.getMemberRepaymentsFlow(memberId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val personalFines: StateFlow<List<FineEntity>> = repository.getMemberFinesFlow(memberId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val outstandingFines: StateFlow<Double> = repository.getMemberOutstandingFinesFlow(memberId)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val personalNotifications: StateFlow<List<NotificationEntity>> = repository.getMemberNotificationsFlow(memberId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.getMemberUnreadCountFlow(memberId)
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val personalTransactions: StateFlow<List<TransactionEntity>> = repository.getMemberTransactionsFlow(memberId)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allActiveMembers: StateFlow<List<UserEntity>> = repository.getActiveMembersFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val latestAnnouncement: StateFlow<AnnouncementEntity?> = repository.getLatestAnnouncementFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val publishedAnnouncements: StateFlow<List<AnnouncementEntity>> = repository.getPublishedMemberAnnouncementsFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.getAllChatMessagesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun submitSavings(amount: Double, paymentRef: String = "", proofNote: String = "") {
        viewModelScope.launch {
            val res = repository.submitSavings(memberId, amount, paymentRef, proofNote)
            res.fold(
                onSuccess = {
                    _message.value = "Ubwizigame bwa RWF ${it.amount.toInt()} bwoherejwe neza. Tegereza Admin abwemeze."
                },
                onFailure = {
                    _message.value = it.message ?: "Kwizigama byanze."
                }
            )
        }
    }

    fun requestLoan(amount: Double, reason: String, durationWeeks: Int) {
        viewModelScope.launch {
            val res = repository.requestLoan(memberId, amount, reason, durationWeeks)
            res.fold(
                onSuccess = {
                    _message.value = "Ubusabe bw'inguzanyo bwa RWF ${amount.toInt()} bwoherejwe neza."
                },
                onFailure = {
                    _message.value = it.message ?: "Gusaba inguzanyo byanze."
                }
            )
        }
    }

    fun submitRepayment(loanId: Long, amount: Double, paymentType: String, paymentRef: String = "") {
        viewModelScope.launch {
            val res = repository.submitRepayment(memberId, loanId, amount, paymentType, paymentRef)
            res.fold(
                onSuccess = {
                    val label = if (paymentType == "INTEREST") "Inyungu" else "Igishoro"
                    _message.value = "Ubwishyu bw'$label bwa RWF ${amount.toInt()} bwoherejwe. Tegereza Admin abwemeze."
                },
                onFailure = {
                    _message.value = it.message ?: "Kwishyura byanze."
                }
            )
        }
    }

    fun payFine(fineId: Long, paymentRef: String) {
        viewModelScope.launch {
            val res = repository.payFine(fineId, paymentRef, null)
            res.fold(
                onSuccess = {
                    _message.value = "Igihano cyishyuwe neza."
                },
                onFailure = {
                    _message.value = it.message ?: "Kwishyura igihano byanze."
                }
            )
        }
    }

    fun sendChatMessage(senderName: String, messageText: String) {
        viewModelScope.launch {
            repository.sendChatMessage(memberId, senderName, "MEMBER", messageText)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllMemberNotificationsRead(memberId)
        }
    }
}
