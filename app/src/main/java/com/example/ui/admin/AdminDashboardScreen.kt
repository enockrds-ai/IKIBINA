package com.example.ui.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import com.example.data.local.entity.AnnouncementEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.components.IkibinaTopBar
import com.example.ui.components.ImariIshyusyeCard
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.IkibinaBrightBlue
import com.example.ui.theme.IkibinaBlueLight
import com.example.ui.theme.IkibinaCardBg
import com.example.ui.theme.IkibinaDarkGray
import com.example.ui.theme.IkibinaGold
import com.example.ui.theme.IkibinaGoldDark
import com.example.ui.theme.IkibinaGoldLight
import com.example.ui.theme.IkibinaGreen
import com.example.ui.theme.IkibinaGreenLight
import com.example.ui.theme.IkibinaRed
import com.example.ui.theme.IkibinaRedLight
import com.example.ui.theme.IkibinaYellow
import com.example.ui.theme.IkibinaYellowDark
import com.example.ui.theme.IkibinaYellowLight
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    adminUser: UserEntity,
    viewModel: AdminViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allMembers by viewModel.allMembers.collectAsState()
    val pendingMembers by viewModel.pendingMembers.collectAsState()
    val allSavings by viewModel.allSavings.collectAsState()
    val pendingSavings by viewModel.pendingSavings.collectAsState()
    val allLoans by viewModel.allLoans.collectAsState()
    val pendingLoans by viewModel.pendingLoans.collectAsState()
    val activeLoans by viewModel.activeLoans.collectAsState()
    val allRepayments by viewModel.allRepayments.collectAsState()
    val pendingRepayments by viewModel.pendingRepayments.collectAsState()
    val allFines by viewModel.allFines.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val adminNotifications by viewModel.adminNotifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val latestAnnouncement by viewModel.latestAnnouncement.collectAsState()
    val communalMoney by viewModel.communalMoney.collectAsState()
    val totalApprovedSavings by viewModel.totalApprovedSavings.collectAsState()
    val totalPendingSavings by viewModel.totalPendingSavings.collectAsState()
    val totalCollectedFines by viewModel.totalCollectedFines.collectAsState()
    val totalOutstandingLoans by viewModel.totalOutstandingLoans.collectAsState()
    val totalApprovedRepayments by viewModel.totalApprovedRepayments.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val actionMessage by viewModel.message.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showNewAnnouncementDialog by remember { mutableStateOf(false) }
    var editingAnnouncement by remember { mutableStateOf<AnnouncementEntity?>(null) }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    val tabs = listOf(
        "Incamake",
        "Abanyamuryango",
        "Ibyizigamwe",
        "Inguzanyo",
        "Kwishyura",
        "Ibihano",
        "Transactions",
        "Ubutumwa",
        "Amatangazo n'Amamaza",
        "Raporo",
        "Igenamiterere"
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            IkibinaTopBar(
                title = "IKIBINA ADMIN",
                role = "ADMIN",
                unreadNotificationsCount = unreadCount,
                onNotificationsClick = { selectedTabIndex = 7 },
                onLogoutClick = onLogout
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
        ) {
            // Admin Profile Subheader
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(IkibinaDarkGray)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Umuyobozi: ${adminUser.fullName}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = adminUser.email,
                            color = IkibinaGoldLight,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF334155))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Admin Verified",
                            color = IkibinaGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Scrollable Navigation Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                items(tabs.size) { index ->
                    val isSelected = selectedTabIndex == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) IkibinaGoldDark else Color(0xFFF1F5F9))
                            .clickable { selectedTabIndex = index }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tabs[index],
                            color = if (isSelected) Color.White else IkibinaDarkGray,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // Tab Content
            when (selectedTabIndex) {
                // 0: INCAMAKE (12 Summary Cards & IMARI ISHYUSYE)
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        // IMARI ISHYUSYE CARD
                        item {
                            ImariIshyusyeCard(
                                communalAvailableAmount = communalMoney,
                                announcement = latestAnnouncement
                            )
                        }

                        item {
                            Text(
                                text = "INCAMAKE Y'IMARI N'ITSINDA",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = IkibinaDarkGray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Card 1 & 2: Registered members & Active members
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard(
                                    title = "Abanyamuryango bose",
                                    value = "${allMembers.size}",
                                    subtitle = "Biyandikishije",
                                    icon = Icons.Default.Group,
                                    iconBgColor = IkibinaGoldLight,
                                    iconColor = IkibinaGoldDark,
                                    modifier = Modifier.weight(1f),
                                    testTag = "admin_total_members_card"
                                )
                                StatCard(
                                    title = "Abakora (Active)",
                                    value = "${allMembers.count { it.status == "ACTIVE" }}",
                                    subtitle = "Bemejwe",
                                    icon = Icons.Default.Check,
                                    iconBgColor = IkibinaGreenLight,
                                    iconColor = IkibinaGreen,
                                    modifier = Modifier.weight(1f),
                                    testTag = "admin_active_members_card"
                                )
                            }
                        }

                        // Card 3 & 4: Pending registrations & Total approved savings
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard(
                                    title = "Abategereje kwemezwa",
                                    value = "${pendingMembers.size}",
                                    subtitle = "Konti nshya",
                                    icon = Icons.Default.PendingActions,
                                    iconBgColor = IkibinaGoldLight,
                                    iconColor = Color(0xFFB45309),
                                    modifier = Modifier.weight(1f),
                                    testTag = "admin_pending_members_card"
                                )
                                StatCard(
                                    title = "Ubwizigame bwemejwe",
                                    value = "RWF ${formatter.format(totalApprovedSavings.toLong())}",
                                    subtitle = "Mu kigega",
                                    icon = Icons.Default.AccountBalance,
                                    iconBgColor = IkibinaGreenLight,
                                    iconColor = IkibinaGreen,
                                    modifier = Modifier.weight(1f),
                                    testTag = "admin_approved_savings_card"
                                )
                            }
                        }

                        // Card 5 & 6: Pending savings approvals & Communal available money
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard(
                                    title = "Ubwizigame butaremezwa",
                                    value = "RWF ${formatter.format(totalPendingSavings.toLong())}",
                                    subtitle = "${pendingSavings.size} bisabwa",
                                    icon = Icons.Default.MonetizationOn,
                                    iconBgColor = IkibinaGoldLight,
                                    iconColor = IkibinaGoldDark,
                                    modifier = Modifier.weight(1f),
                                    testTag = "admin_pending_savings_card"
                                )
                                StatCard(
                                    title = "Asigaye muri rusange",
                                    value = "RWF ${formatter.format(communalMoney.toLong())}",
                                    subtitle = "Imari y'itsinda",
                                    icon = Icons.Default.TrendingUp,
                                    iconBgColor = Color(0xFFE0E7FF),
                                    iconColor = Color(0xFF4338CA),
                                    modifier = Modifier.weight(1f),
                                    testTag = "admin_communal_money_card"
                                )
                            }
                        }

                        // Card 7 & 8: Active loans & Outstanding loan balances
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard(
                                    title = "Inguzanyo zikora",
                                    value = "${activeLoans.size}",
                                    subtitle = "Zitarishyurwa",
                                    icon = Icons.Default.ReceiptLong,
                                    iconBgColor = Color(0xFFFEF3C7),
                                    iconColor = IkibinaGoldDark,
                                    modifier = Modifier.weight(1f),
                                    testTag = "admin_active_loans_card"
                                )
                                StatCard(
                                    title = "Ibirarane by'inguzanyo",
                                    value = "RWF ${formatter.format(totalOutstandingLoans.toLong())}",
                                    subtitle = "Bitegerejwe",
                                    icon = Icons.Default.ReceiptLong,
                                    iconBgColor = IkibinaRedLight,
                                    iconColor = IkibinaRed,
                                    modifier = Modifier.weight(1f),
                                    testTag = "admin_outstanding_loans_card"
                                )
                            }
                        }

                        // Card 9 & 10: Pending loan requests & Pending repayments
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard(
                                    title = "Ibisabwa by'inguzanyo",
                                    value = "${pendingLoans.size}",
                                    subtitle = "Bitegereje kwemezwa",
                                    icon = Icons.Default.PendingActions,
                                    iconBgColor = IkibinaGoldLight,
                                    iconColor = Color(0xFFB45309),
                                    modifier = Modifier.weight(1f),
                                    testTag = "admin_pending_loans_card"
                                )
                                StatCard(
                                    title = "Ubwishyu butaremezwa",
                                    value = "${pendingRepayments.size}",
                                    subtitle = "Bitegereje Admin",
                                    icon = Icons.Default.PriceCheck,
                                    iconBgColor = IkibinaGoldLight,
                                    iconColor = IkibinaGoldDark,
                                    modifier = Modifier.weight(1f),
                                    testTag = "admin_pending_repayments_card"
                                )
                            }
                        }

                        // Card 11 & 12: Total approved repayments & Total collected fines
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard(
                                    title = "Ubwishyu bwemejwe",
                                    value = "RWF ${formatter.format(totalApprovedRepayments.toLong())}",
                                    subtitle = "Bwishyuwe",
                                    icon = Icons.Default.Check,
                                    iconBgColor = IkibinaGreenLight,
                                    iconColor = IkibinaGreen,
                                    modifier = Modifier.weight(1f),
                                    testTag = "admin_total_repayments_card"
                                )
                                StatCard(
                                    title = "Ibihano byishyuwe",
                                    value = "RWF ${formatter.format(totalCollectedFines.toLong())}",
                                    subtitle = "RWF 100 buri cyumweru",
                                    icon = Icons.Default.Gavel,
                                    iconBgColor = IkibinaRedLight,
                                    iconColor = IkibinaRed,
                                    modifier = Modifier.weight(1f),
                                    testTag = "admin_collected_fines_card"
                                )
                            }
                        }
                    }
                }

                // 1: ABANYAMURYANGO (Search, Approve/Reject, Activate/Deactivate)
                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Shakisha umunyamuryango (Izina, email, telefone)") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_search_member_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val filtered = allMembers.filter {
                            it.fullName.contains(searchQuery, ignoreCase = true) ||
                                    it.phone.contains(searchQuery) ||
                                    it.email.contains(searchQuery, ignoreCase = true)
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filtered) { member ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = IkibinaCardBg),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = member.fullName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = IkibinaDarkGray
                                                )
                                                Text(
                                                    text = "${member.phone} • ${member.email}",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                            StatusBadge(status = member.status)
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Ubwizigame: RWF ${formatter.format(member.approvedSavingsBalance.toLong())}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = IkibinaGreen
                                            )
                                            Text(
                                                text = "Yiyandikishije: ${dateFormat.format(Date(member.createdAt))}",
                                                fontSize = 11.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // Actions: Approve / Reject / Toggle
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            if (member.status == "PENDING") {
                                                Button(
                                                    onClick = { viewModel.approveMember(adminUser.id, member.id) },
                                                    colors = ButtonDefaults.buttonColors(containerColor = IkibinaGreen),
                                                    modifier = Modifier.testTag("approve_member_${member.id}")
                                                ) {
                                                    Text("EMEZA (APPROVE)", fontSize = 12.sp)
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                OutlinedButton(
                                                    onClick = { viewModel.rejectMember(adminUser.id, member.id) },
                                                    modifier = Modifier.testTag("reject_member_${member.id}")
                                                ) {
                                                    Text("HAKANA", fontSize = 12.sp, color = IkibinaRed)
                                                }
                                            } else {
                                                val isActive = member.status == "ACTIVE"
                                                OutlinedButton(
                                                    onClick = { viewModel.toggleMemberStatus(adminUser.id, member.id, !isActive) },
                                                    modifier = Modifier.testTag("toggle_member_${member.id}")
                                                ) {
                                                    Text(
                                                        text = if (isActive) "HAGARIKA" else "ONGERA UKOREZE",
                                                        fontSize = 12.sp,
                                                        color = if (isActive) IkibinaRed else IkibinaGreen
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2: IBYIZIGAMWE (Review pending savings, EMEZA / HAKANA, History)
                2 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "UBWIZIGAME BUTEREJWE KWEMEZWA (${pendingSavings.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = IkibinaDarkGray
                            )
                        }

                        if (pendingSavings.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Nta bwizigame butegereje kwemezwa burahari.", color = Color(0xFF64748B))
                                }
                            }
                        }

                        items(pendingSavings) { savings ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = IkibinaCardBg),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = savings.memberName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = "Imigabane: ${savings.sharesCount} (${savings.weekCode})",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                        Text(
                                            text = "RWF ${formatter.format(savings.amount.toLong())}",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            color = IkibinaGoldDark
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "MoMo Ref: ${savings.paymentReference.ifEmpty { "N/A" }}",
                                        fontSize = 12.sp,
                                        color = IkibinaDarkGray,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = if (savings.isWithinScheduledPeriod)
                                            "✓ Mu gihe cyagenwe (Nta gihano)"
                                        else
                                            "⚠ Hanze y'igihe (Igihano cya RWF 100 cyashyizweho)",
                                        fontSize = 11.sp,
                                        color = if (savings.isWithinScheduledPeriod) IkibinaGreen else IkibinaRed
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Button(
                                            onClick = { viewModel.approveSavings(adminUser.id, savings.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = IkibinaGreen),
                                            modifier = Modifier.testTag("admin_approve_savings_${savings.id}")
                                        ) {
                                            Text("EMEZA (APPROVE)")
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedButton(
                                            onClick = { viewModel.rejectSavings(adminUser.id, savings.id) },
                                            modifier = Modifier.testTag("admin_reject_savings_${savings.id}")
                                        ) {
                                            Text("HAKANA", color = IkibinaRed)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "UBWIZIGAME BWEMEJWE CYANGWA BWANZE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = IkibinaDarkGray
                            )
                        }

                        items(allSavings.filter { it.status != "PENDING" }) { savings ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = IkibinaCardBg)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = savings.memberName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${dateFormat.format(Date(savings.submittedAt))} • ${savings.sharesCount} imigabane",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "RWF ${formatter.format(savings.amount.toLong())}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        StatusBadge(status = savings.status)
                                    }
                                }
                            }
                        }
                    }
                }

                // 3: INGUZANYO (Pending & Active loans review)
                3 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "IBISABWA BY'INGUZANYO BITEGEREJWE (${pendingLoans.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = IkibinaDarkGray
                            )
                        }

                        if (pendingLoans.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Nta busabe bw'inguzanyo butegerejwe.", color = Color(0xFF64748B))
                                }
                            }
                        }

                        items(pendingLoans) { loan ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = IkibinaCardBg),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = loan.memberName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = "Impamvu: ${loan.reason}",
                                                fontSize = 13.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                        Text(
                                            text = "RWF ${formatter.format(loan.amountRequested.toLong())}",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            color = IkibinaGoldDark
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Inyungu (5%): RWF ${formatter.format(loan.interestAmount.toLong())} • Ayose azishyurwa: RWF ${formatter.format(loan.totalDue.toLong())}",
                                        fontSize = 12.sp,
                                        color = IkibinaDarkGray
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Button(
                                            onClick = { viewModel.approveLoan(adminUser.id, loan.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = IkibinaGreen),
                                            modifier = Modifier.testTag("admin_approve_loan_${loan.id}")
                                        ) {
                                            Text("EMEZA INGUZANYO")
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedButton(
                                            onClick = { viewModel.rejectLoan(adminUser.id, loan.id) },
                                            modifier = Modifier.testTag("admin_reject_loan_${loan.id}")
                                        ) {
                                            Text("HAKANA", color = IkibinaRed)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "INGUZANYO ZIKORA (ACTIVE LOANS - ${activeLoans.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = IkibinaDarkGray
                            )
                        }

                        items(activeLoans) { loan ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = IkibinaCardBg)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = loan.memberName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        StatusBadge(status = loan.status)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Asigaye: RWF ${formatter.format(loan.outstandingBalance.toLong())}",
                                            fontWeight = FontWeight.Bold,
                                            color = IkibinaRed,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Ayishyuwe: RWF ${formatter.format(loan.amountPaid.toLong())}",
                                            color = IkibinaGreen,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 4: KWISHYURA INGUZANYO (Review loan repayments)
                4 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "UBWISHYU BUTEREJWE KWEMEZWA (${pendingRepayments.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = IkibinaDarkGray
                            )
                        }

                        if (pendingRepayments.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Nta bwishyu butegerejwe kwemezwa.", color = Color(0xFF64748B))
                                }
                            }
                        }

                        items(pendingRepayments) { repay ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = IkibinaCardBg)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = repay.memberName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = "MoMo Ref: ${repay.paymentReference}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                        Text(
                                            text = "RWF ${formatter.format(repay.amount.toLong())}",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                            color = IkibinaGreen
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Button(
                                            onClick = { viewModel.approveRepayment(adminUser.id, repay.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = IkibinaGreen),
                                            modifier = Modifier.testTag("admin_approve_repay_${repay.id}")
                                        ) {
                                            Text("EMEZA UBWISHYU")
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        OutlinedButton(
                                            onClick = { viewModel.rejectRepayment(adminUser.id, repay.id) }
                                        ) {
                                            Text("HAKANA", color = IkibinaRed)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 5: IBIHANO (Missed week fines review & reconciliation)
                5 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "IBIHANO BY'ITSINDA (Fixed RWF 100 buri cyumweru cyasibwe)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = IkibinaDarkGray
                            )
                        }

                        items(allFines) { fine ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = IkibinaCardBg)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = fine.memberName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = "${fine.weekCode} • ${fine.reason}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                        StatusBadge(status = fine.status)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "RWF ${fine.amount.toInt()}",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp,
                                            color = IkibinaRed
                                        )

                                        if (fine.status == "PENDING_PAYMENT") {
                                            Row {
                                                Button(
                                                    onClick = {
                                                        viewModel.markFinePaid(fine.id, adminUser.id, "MoMo-Cash")
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = IkibinaGoldDark),
                                                    modifier = Modifier.testTag("admin_collect_fine_${fine.id}")
                                                ) {
                                                    Text("EMEZA KO CYISHYUWE", fontSize = 11.sp)
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                OutlinedButton(
                                                    onClick = { viewModel.waiveFine(adminUser.id, fine.id) }
                                                ) {
                                                    Text("MBABARIRA", fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 6: TRANSACTIONS (Audit log)
                6 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "URUHEREREKANE RW'IMARI RWOSI (AUDIT LEDGER)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = IkibinaDarkGray
                            )
                        }

                        items(allTransactions) { tx ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = IkibinaCardBg)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tx.description,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${dateFormat.format(Date(tx.timestamp))} • ${tx.performedBy}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Text(
                                        text = (if (tx.direction == "INFLOW") "+ " else "- ") +
                                                "RWF ${formatter.format(tx.amount.toLong())}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = if (tx.direction == "INFLOW") IkibinaGreen else IkibinaRed
                                    )
                                }
                            }
                        }
                    }
                }

                // 7: NOTIFICATIONS (Admin alerts)
                7 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "UBUTUMWA BWA ADMIN (${adminNotifications.size})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Soma Byose",
                                    color = IkibinaGoldDark,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { viewModel.markAllNotificationsRead() }
                                )
                            }
                        }

                        items(adminNotifications) { notif ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (notif.isRead) IkibinaCardBg else IkibinaGoldLight
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = notif.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = IkibinaDarkGray
                                        )
                                        Text(
                                            text = dateFormat.format(Date(notif.createdAt)),
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = notif.message,
                                        fontSize = 13.sp,
                                        color = IkibinaDarkGray
                                    )
                                }
                            }
                        }
                    }
                }

                // 8: AMATANGAZO N'AMAMAZA (Announcements & Advertising management)
                8 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "AMATANGAZO N'AMAMAZA",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = IkibinaDarkGray
                                )
                                Text(
                                    text = "Amatangazo n'amamaza y'itsinda",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Button(
                                onClick = { showNewAnnouncementDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("+ Tanga Itangazo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (announcements.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White)
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Nta matangazo cyangwa amamaza arahari. Kanda hejuru kugira ngo ushyireho rishya.", color = Color(0xFF64748B), fontSize = 13.sp)
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(announcements) { ann ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = IkibinaCardBg),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = ann.title,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 15.sp,
                                                        color = IkibinaDarkGray
                                                    )
                                                    Text(
                                                        text = "Yashyizweho na: ${ann.authorName} • ${dateFormat.format(Date(ann.createdAt))}",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF64748B)
                                                    )
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    // Status Chip: Published / Hidden
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(if (ann.isPublished) IkibinaGreenLight else Color(0xFFF1F5F9))
                                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                                    ) {
                                                        Text(
                                                            text = if (ann.isPublished) "YATANGAJWE" else "YAHISHWE",
                                                            fontSize = 10.sp,
                                                            color = if (ann.isPublished) IkibinaGreen else Color(0xFF64748B),
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    // Dashboard visibility Chip
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(if (ann.isVisibleToMembers) IkibinaBlueLight else IkibinaRedLight)
                                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                                    ) {
                                                        Text(
                                                            text = if (ann.isVisibleToMembers) "KURI DASHBOARD" else "NTIYEREKWA",
                                                            fontSize = 10.sp,
                                                            color = if (ann.isVisibleToMembers) IkibinaBrightBlue else IkibinaRed,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(text = ann.content, fontSize = 13.sp, color = IkibinaDarkGray)

                                            if (ann.imageUrl.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(IkibinaBlueLight)
                                                        .padding(8.dp)
                                                ) {
                                                    Text(
                                                        text = "Banner / Ifoto: ${ann.imageUrl}",
                                                        fontSize = 11.sp,
                                                        color = IkibinaBrightBlue,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }

                                            if (ann.startDate.isNotBlank() || ann.endDate.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "Itariki yo kugaragara: ${ann.startDate.ifBlank { "Nta ntangiriro" }} kugeza ${ann.endDate.ifBlank { "Nta mpera" }}",
                                                    fontSize = 11.sp,
                                                    color = IkibinaYellowDark,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))

                                            // Action Buttons: Edit, Toggle Publish, Toggle Visibility, Delete
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                OutlinedButton(
                                                    onClick = { editingAnnouncement = ann },
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Hindura", fontSize = 11.sp)
                                                }

                                                OutlinedButton(
                                                    onClick = { viewModel.toggleAnnouncementPublish(ann.id, !ann.isPublished) },
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                                ) {
                                                    Text(if (ann.isPublished) "Hisha" else "Tangaza", fontSize = 11.sp)
                                                }

                                                OutlinedButton(
                                                    onClick = { viewModel.toggleAnnouncementVisibility(ann.id, !ann.isVisibleToMembers) },
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(
                                                        if (ann.isVisibleToMembers) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text(if (ann.isVisibleToMembers) "Kura" else "Erekana", fontSize = 11.sp)
                                                }

                                                OutlinedButton(
                                                    onClick = { viewModel.deleteAnnouncement(ann.id) },
                                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = IkibinaRed),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = IkibinaRed)
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text("Siba", fontSize = 11.sp, color = IkibinaRed)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 9: RAPORO (Financial and Group Reports)
                9 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "RAPORO Z'IMARI N'UMUTUNGO W'IKIBINA",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = IkibinaCardBg)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Incamake y'Ikigega",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    ReportRow("Ubwizigame bwose bwemejwe", "RWF ${formatter.format(totalApprovedSavings.toLong())}")
                                    ReportRow("Ibihano byose byishyuwe", "RWF ${formatter.format(totalCollectedFines.toLong())}")
                                    ReportRow("Ubwishyu bwose bw'inguzanyo", "RWF ${formatter.format(totalApprovedRepayments.toLong())}")
                                    ReportRow("Inguzanyo zikora zose", "RWF ${formatter.format(totalOutstandingLoans.toLong())}")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(Color(0xFFE2E8F0))
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    ReportRow(
                                        "AMAFARANGA ASIGAYE MU KIGEGA",
                                        "RWF ${formatter.format(communalMoney.toLong())}",
                                        isBold = true
                                    )
                                }
                            }
                        }
                    }
                }

                // 10: IGENAMITERERE (Group Settings)
                10 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "IGENAMITERERE RY'ITSINDA (GROUP SETTINGS)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = IkibinaCardBg)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    ReportRow("Izina ry'itsinda", settings?.groupName ?: "IKIBINA")
                                    ReportRow("Igiciro cy'umugabane 1", "RWF ${settings?.sharePrice?.toInt() ?: 500}")
                                    ReportRow("Ntarengwa y'imigabane mu cyumweru", "${settings?.maxSharesPerWeek ?: 8} (RWF 4,000)")
                                    ReportRow("Kode yo kwishyuriraho", settings?.paymentCode ?: "18281412512#")
                                    ReportRow("Igihe cyo kuzigama gitangirira", "${settings?.savingsStartDay ?: "Ku wa kabiri"} ${settings?.savingsStartTime ?: "17:00"}")
                                    ReportRow("Igihe cyo kuzigama kirangirira", "${settings?.savingsEndDay ?: "Ku wa gatatu"} ${settings?.savingsEndTime ?: "15:00"}")
                                    ReportRow("Igihano cyo gusiba icyumweru", "RWF ${settings?.missedSavingsFine?.toInt() ?: 100} (Fixed)")
                                    ReportRow("Ntarengwa y'inguzanyo", "${settings?.maxLoanPercentage?.toInt() ?: 80}% by'asigaye mu kigega")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNewAnnouncementDialog) {
        var annTitle by remember { mutableStateOf("") }
        var annContent by remember { mutableStateOf("") }
        var annImageUrl by remember { mutableStateOf("") }
        var annStartDate by remember { mutableStateOf("") }
        var annEndDate by remember { mutableStateOf("") }
        var annIsVisible by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showNewAnnouncementDialog = false },
            title = { Text("Tanga Itangazo / Kwamamaza", fontWeight = FontWeight.Bold, color = IkibinaDarkGray) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = annTitle,
                        onValueChange = { annTitle = it },
                        label = { Text("Umutwe w'itangazo") },
                        placeholder = { Text("Urugero: Poromosiyo y'Ubwizigame") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = annContent,
                        onValueChange = { annContent = it },
                        label = { Text("Ubutumwa / Ibirimo") },
                        placeholder = { Text("Andika amakuru arambuye...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = annImageUrl,
                        onValueChange = { annImageUrl = it },
                        label = { Text("Banner / Ifoto y'amamaza (URL cyangwa Izina)") },
                        placeholder = { Text("https://... cyangwa banner_promo.png") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = annStartDate,
                            onValueChange = { annStartDate = it },
                            label = { Text("Itariki y'itangira") },
                            placeholder = { Text("2026-10-10") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = annEndDate,
                            onValueChange = { annEndDate = it },
                            label = { Text("Itariki y'iherezo") },
                            placeholder = { Text("2026-10-24") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kugaragara ku Dashboard y'Abanyamuryango:",
                            fontSize = 12.sp,
                            color = IkibinaDarkGray,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = annIsVisible,
                            onCheckedChange = { annIsVisible = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = IkibinaBrightBlue)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (annTitle.isNotBlank() && annContent.isNotBlank()) {
                            viewModel.publishAnnouncement(
                                title = annTitle,
                                content = annContent,
                                authorName = adminUser.fullName,
                                imageUrl = annImageUrl,
                                isVisibleToMembers = annIsVisible,
                                startDate = annStartDate,
                                endDate = annEndDate
                            )
                            showNewAnnouncementDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue)
                ) {
                    Text("TANGA ITANGAZO", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showNewAnnouncementDialog = false }) {
                    Text("REKA")
                }
            }
        )
    }

    editingAnnouncement?.let { ann ->
        var editTitle by remember { mutableStateOf(ann.title) }
        var editContent by remember { mutableStateOf(ann.content) }
        var editImageUrl by remember { mutableStateOf(ann.imageUrl) }
        var editStartDate by remember { mutableStateOf(ann.startDate) }
        var editEndDate by remember { mutableStateOf(ann.endDate) }
        var editIsVisible by remember { mutableStateOf(ann.isVisibleToMembers) }
        var editIsPublished by remember { mutableStateOf(ann.isPublished) }

        AlertDialog(
            onDismissRequest = { editingAnnouncement = null },
            title = { Text("Hindura Itangazo / Kwamamaza", fontWeight = FontWeight.Bold, color = IkibinaDarkGray) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Umutwe w'itangazo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editContent,
                        onValueChange = { editContent = it },
                        label = { Text("Ubutumwa / Ibirimo") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editImageUrl,
                        onValueChange = { editImageUrl = it },
                        label = { Text("Banner / Ifoto y'amamaza (URL)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editStartDate,
                            onValueChange = { editStartDate = it },
                            label = { Text("Itangiriro") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editEndDate,
                            onValueChange = { editEndDate = it },
                            label = { Text("Iherezo") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kugaragara ku Dashboard:",
                            fontSize = 12.sp,
                            color = IkibinaDarkGray
                        )
                        Switch(
                            checked = editIsVisible,
                            onCheckedChange = { editIsVisible = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = IkibinaBrightBlue)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gushyirwa ahagaragara (Published):",
                            fontSize = 12.sp,
                            color = IkibinaDarkGray
                        )
                        Switch(
                            checked = editIsPublished,
                            onCheckedChange = { editIsPublished = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = IkibinaGreen)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editTitle.isNotBlank() && editContent.isNotBlank()) {
                            viewModel.updateAnnouncement(
                                ann.copy(
                                    title = editTitle,
                                    content = editContent,
                                    imageUrl = editImageUrl,
                                    startDate = editStartDate,
                                    endDate = editEndDate,
                                    isVisibleToMembers = editIsVisible,
                                    isPublished = editIsPublished
                                )
                            )
                            editingAnnouncement = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue)
                ) {
                    Text("BIHINDURE", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { editingAnnouncement = null }) {
                    Text("REKA")
                }
            }
        )
    }
}

@Composable
private fun ReportRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = if (isBold) 14.sp else 13.sp,
            color = if (isBold) IkibinaDarkGray else Color(0xFF64748B),
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = if (isBold) 15.sp else 13.sp,
            color = if (isBold) IkibinaGoldDark else IkibinaDarkGray,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold
        )
    }
}
