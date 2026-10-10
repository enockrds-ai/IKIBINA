package com.example.ui.member

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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FineEntity
import com.example.data.local.entity.LoanEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.auth.ChangePasswordDialog
import com.example.ui.components.IkibinaTopBar
import com.example.ui.components.ImariIshyusyeCard
import com.example.ui.components.LoanRequestDialog
import com.example.ui.components.PayFineDialog
import com.example.ui.components.RepayLoanDialog
import com.example.ui.components.SavingsDialog
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.IkibinaBrightBlue
import com.example.ui.theme.IkibinaBlueLight
import com.example.ui.theme.IkibinaYellow
import com.example.ui.theme.IkibinaYellowDark
import com.example.ui.theme.IkibinaYellowLight
import com.example.ui.theme.IkibinaCardBg
import com.example.ui.theme.IkibinaDarkGray
import com.example.ui.theme.IkibinaGold
import com.example.ui.theme.IkibinaGoldDark
import com.example.ui.theme.IkibinaGoldLight
import com.example.ui.theme.IkibinaGreen
import com.example.ui.theme.IkibinaGreenLight
import com.example.ui.theme.IkibinaRed
import com.example.ui.theme.IkibinaRedLight
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MemberDashboardScreen(
    initialMember: UserEntity,
    viewModel: MemberViewModel,
    onLogout: () -> Unit,
    onChangePasswordRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val liveUser by viewModel.memberUser.collectAsState()
    val member = liveUser ?: initialMember

    val personalSavings by viewModel.personalSavings.collectAsState()
    val personalApprovedSavings by viewModel.personalApprovedSavings.collectAsState()
    val personalPendingSavings by viewModel.personalPendingSavings.collectAsState()
    val communalMoney by viewModel.communalMoney.collectAsState()
    val totalCommunalSavings by viewModel.totalCommunalSavings.collectAsState()
    val personalLoans by viewModel.personalLoans.collectAsState()
    val activeLoanBalance by viewModel.activeLoanBalance.collectAsState()
    val personalRepayments by viewModel.personalRepayments.collectAsState()
    val personalFines by viewModel.personalFines.collectAsState()
    val outstandingFines by viewModel.outstandingFines.collectAsState()
    val personalNotifications by viewModel.personalNotifications.collectAsState()
    val unreadNotificationsCount by viewModel.unreadNotificationsCount.collectAsState()
    val personalTransactions by viewModel.personalTransactions.collectAsState()
    val allActiveMembers by viewModel.allActiveMembers.collectAsState()
    val latestAnnouncement by viewModel.latestAnnouncement.collectAsState()
    val publishedAnnouncements by viewModel.publishedAnnouncements.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val actionMessage by viewModel.message.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US) }

    // Dialogs state
    var showSavingsDialog by remember { mutableStateOf(false) }
    var showLoanDialog by remember { mutableStateOf(false) }
    var showRepayDialogForLoan by remember { mutableStateOf<LoanEntity?>(null) }
    var selectedRepayMode by remember { mutableStateOf("PRINCIPAL") } // "PRINCIPAL" or "INTEREST"
    var showPayFineDialog by remember { mutableStateOf<FineEntity?>(null) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    // Navigation sub-screen / view
    // 0: Dashboard (Overview & 10 Actions)
    // 1: BONA UBWIZIGAME (Savings History)
    // 2: INGUZANYA (Loans & Repayment history)
    // 3: HISTORICAL Y'IBIKORWA (Transactions)
    // 4: URUGANIRIRO RW'ITSINDA (Chat)
    // 5: REBA ABANYAMURYANGO (Directory)
    // 6: NOTIFICATIONS
    // 7: PROFILE
    var activeViewIndex by remember { mutableIntStateOf(0) }
    var chatInputText by remember { mutableStateOf("") }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            IkibinaTopBar(
                title = "IKIBINA",
                role = "MEMBER",
                unreadNotificationsCount = unreadNotificationsCount,
                onNotificationsClick = { activeViewIndex = 6 },
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
            // Member Welcome Subheader
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(IkibinaDarkGray)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Murakaza neza, ${member.fullName}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = member.savingsGroupName,
                            color = IkibinaGoldLight,
                            fontSize = 12.sp
                        )
                    }
                    StatusBadge(status = member.status)
                }
            }

            // PENDING STATUS NOTICE
            if (member.status == "PENDING") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(IkibinaGoldLight)
                        .padding(18.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Konti yakozwe neza. Tegereza Admin ayemeze.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFB45309),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Konti yawe iracyari mu busabe. Numara kwemezwa na Admin uzashobora gutangira kuzigama no gusaba inguzanyo.",
                            fontSize = 12.sp,
                            color = IkibinaDarkGray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            // Navigation bar buttons (Home, Savings, Loans, History, Chat, Members, Profile)
            val subScreens = listOf(
                Pair(0, "Ahabanza"),
                Pair(1, "Ubwizigame"),
                Pair(2, "Inguzanyo"),
                Pair(3, "Ibikorwa"),
                Pair(4, "Uruganiriro"),
                Pair(5, "Abanyamuryango"),
                Pair(7, "Umwirondoro")
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
            ) {
                items(subScreens.size) { idx ->
                    val item = subScreens[idx]
                    val isSelected = activeViewIndex == item.first
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) IkibinaGoldDark else Color(0xFFF1F5F9))
                            .clickable { activeViewIndex = item.first }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = item.second,
                            color = if (isSelected) Color.White else IkibinaDarkGray,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            // View Content
            when (activeViewIndex) {
                // 0: MAIN MEMBER DASHBOARD
                0 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        // 1. IMARI ISHYUSYE
                        item {
                            ImariIshyusyeCard(
                                communalAvailableAmount = communalMoney,
                                announcement = latestAnnouncement
                            )
                        }

                        // PROMINENT HERO ACTION BUTTON: "KANDA AHA WIZIGAME"
                        item {
                            Button(
                                onClick = {
                                    if (member.status == "ACTIVE") {
                                        showSavingsDialog = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .testTag("kanda_aha_wizigame_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue),
                                shape = RoundedCornerShape(14.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Payment,
                                    contentDescription = null,
                                    tint = IkibinaYellow,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "KANDA AHA WIZIGAME",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // PUBLISHED ANNOUNCEMENTS & ADVERTISEMENTS (Non-obstructive)
                        if (publishedAnnouncements.isNotEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = IkibinaCardBg),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Campaign,
                                                    contentDescription = null,
                                                    tint = IkibinaBrightBlue,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "AMATANGAZO N'AMAMAZA",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = IkibinaBrightBlue
                                                )
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(IkibinaYellowLight)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("${publishedAnnouncements.size} Itangazo", fontSize = 10.sp, color = IkibinaYellowDark, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        publishedAnnouncements.take(2).forEach { ann ->
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFF8FAFC))
                                                    .padding(10.dp)
                                            ) {
                                                Column {
                                                    Text(text = ann.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = IkibinaDarkGray)
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(text = ann.content, fontSize = 12.sp, color = Color(0xFF475569))
                                                    if (ann.startDate.isNotBlank() || ann.endDate.isNotBlank()) {
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(
                                                            text = "Igihe: ${ann.startDate.ifBlank { "None" }} kugeza ${ann.endDate.ifBlank { "None" }}",
                                                            fontSize = 10.sp,
                                                            color = IkibinaBrightBlue
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. 10 MAIN MEMBER ACTIONS (GRID / TILES)
                        item {
                            Text(
                                text = "IBIKORWA BY'UMUNYAMURYANGO",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = IkibinaDarkGray,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Actions Row 1: 1. KANDA AHA WIZIGAME & 2. BONA UBWIZIGAME
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MemberActionTile(
                                    title = "1. KANDA AHA WIZIGAME",
                                    subtitle = "Tanga umugabane (MoMo)",
                                    icon = Icons.Default.Payment,
                                    bgColor = IkibinaBrightBlue,
                                    textColor = Color.White,
                                    modifier = Modifier.weight(1f),
                                    testTag = "action_izigame_button"
                                ) {
                                    if (member.status == "ACTIVE") {
                                        showSavingsDialog = true
                                    }
                                }

                                MemberActionTile(
                                    title = "2. UBWIZIGAME",
                                    subtitle = "Reba ubwizigame bwawe",
                                    icon = Icons.Default.AccountBalance,
                                    bgColor = IkibinaCardBg,
                                    textColor = IkibinaDarkGray,
                                    modifier = Modifier.weight(1f),
                                    testTag = "action_view_savings_button"
                                ) {
                                    activeViewIndex = 1
                                }
                            }
                        }

                        // Actions Row 2: 3. SABA INGUZANYA & 4. ISHYURA INGUZANYA
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MemberActionTile(
                                    title = "3. SABA INGUZANYA",
                                    subtitle = "Kugeza kuri 80%",
                                    icon = Icons.Default.MonetizationOn,
                                    bgColor = IkibinaCardBg,
                                    textColor = IkibinaDarkGray,
                                    modifier = Modifier.weight(1f),
                                    testTag = "action_request_loan_button"
                                ) {
                                    if (member.status == "ACTIVE") {
                                        showLoanDialog = true
                                    }
                                }

                                MemberActionTile(
                                    title = "4. ISHYURA INGUZANYA",
                                    subtitle = "Kwishyura inguzanyo",
                                    icon = Icons.Default.PriceCheck,
                                    bgColor = IkibinaCardBg,
                                    textColor = IkibinaDarkGray,
                                    modifier = Modifier.weight(1f),
                                    testTag = "action_repay_loan_button"
                                ) {
                                    activeViewIndex = 2
                                }
                            }
                        }

                        // Actions Row 3: 5. HISTORICAL Y'IBIKORWA & 6. IMARI ISHYUSYE
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MemberActionTile(
                                    title = "5. HISTORICAL",
                                    subtitle = "Ibyakozwe byose",
                                    icon = Icons.Default.History,
                                    bgColor = IkibinaCardBg,
                                    textColor = IkibinaDarkGray,
                                    modifier = Modifier.weight(1f),
                                    testTag = "action_history_button"
                                ) {
                                    activeViewIndex = 3
                                }

                                MemberActionTile(
                                    title = "6. IMARI ISHYUSYE",
                                    subtitle = "Amafaranga asigaye",
                                    icon = Icons.Default.CreditCard,
                                    bgColor = IkibinaCardBg,
                                    textColor = IkibinaDarkGray,
                                    modifier = Modifier.weight(1f),
                                    testTag = "action_imari_button"
                                ) {
                                    // Scroll to top / ImariIshyusye is already at top
                                }
                            }
                        }

                        // Actions Row 4: 7. URUGANIRIRO & 8. REBA ABANYAMURYANGO
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MemberActionTile(
                                    title = "7. URUGANIRIRO",
                                    subtitle = "Ibiganiro by'itsinda",
                                    icon = Icons.Default.Chat,
                                    bgColor = IkibinaCardBg,
                                    textColor = IkibinaDarkGray,
                                    modifier = Modifier.weight(1f),
                                    testTag = "action_chat_button"
                                ) {
                                    activeViewIndex = 4
                                }

                                MemberActionTile(
                                    title = "8. ABANYAMURYANGO",
                                    subtitle = "Abagize itsinda",
                                    icon = Icons.Default.Group,
                                    bgColor = IkibinaCardBg,
                                    textColor = IkibinaDarkGray,
                                    modifier = Modifier.weight(1f),
                                    testTag = "action_members_directory_button"
                                ) {
                                    activeViewIndex = 5
                                }
                            }
                        }

                        // Actions Row 5: 9. NOTIFICATIONS & 10. PROFILE
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MemberActionTile(
                                    title = "9. NOTIFICATIONS",
                                    subtitle = "${unreadNotificationsCount} nshya",
                                    icon = Icons.Default.Notifications,
                                    bgColor = IkibinaCardBg,
                                    textColor = IkibinaDarkGray,
                                    modifier = Modifier.weight(1f),
                                    testTag = "action_notifications_button"
                                ) {
                                    activeViewIndex = 6
                                }

                                MemberActionTile(
                                    title = "10. PROFILE",
                                    subtitle = "Umwirondoro wawe",
                                    icon = Icons.Default.Person,
                                    bgColor = IkibinaCardBg,
                                    textColor = IkibinaDarkGray,
                                    modifier = Modifier.weight(1f),
                                    testTag = "action_profile_button"
                                ) {
                                    activeViewIndex = 7
                                }
                            }
                        }

                        // 3. PERSONAL FINANCIAL CARDS SUMMARY
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "INCAMAKE Y'IMARI YAWE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = IkibinaDarkGray
                            )
                        }

                        // Personal approved & pending savings
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard(
                                    title = "Ubwizigame bwemejwe",
                                    value = "RWF ${formatter.format(personalApprovedSavings.toLong())}",
                                    subtitle = "Bwanjye",
                                    icon = Icons.Default.Check,
                                    iconBgColor = IkibinaGreenLight,
                                    iconColor = IkibinaGreen,
                                    modifier = Modifier.weight(1f),
                                    testTag = "member_approved_savings_stat"
                                )
                                StatCard(
                                    title = "Ubwizigame butaremezwa",
                                    value = "RWF ${formatter.format(personalPendingSavings.toLong())}",
                                    subtitle = "Buri mu isuzuma",
                                    icon = Icons.Default.Payment,
                                    iconBgColor = IkibinaGoldLight,
                                    iconColor = IkibinaGoldDark,
                                    modifier = Modifier.weight(1f),
                                    testTag = "member_pending_savings_stat"
                                )
                            }
                        }

                        // Active Loan balance & Outstanding Fines
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatCard(
                                    title = "Inguzanyo mfite",
                                    value = "RWF ${formatter.format(activeLoanBalance.toLong())}",
                                    subtitle = "Itarishyurwa",
                                    icon = Icons.Default.ReceiptLong,
                                    iconBgColor = if (activeLoanBalance > 0) IkibinaRedLight else IkibinaCardBg,
                                    iconColor = if (activeLoanBalance > 0) IkibinaRed else IkibinaDarkGray,
                                    modifier = Modifier.weight(1f),
                                    testTag = "member_active_loan_stat"
                                )
                                StatCard(
                                    title = "Ibihano byo kwishyura",
                                    value = "RWF ${formatter.format(outstandingFines.toLong())}",
                                    subtitle = "RWF 100 buri cyumweru",
                                    icon = Icons.Default.Gavel,
                                    iconBgColor = if (outstandingFines > 0) IkibinaRedLight else IkibinaCardBg,
                                    iconColor = if (outstandingFines > 0) IkibinaRed else IkibinaDarkGray,
                                    modifier = Modifier.weight(1f),
                                    testTag = "member_fines_stat"
                                )
                            }
                        }
                    }
                }

                // 1: BONA UBWIZIGAME BWAWE (Personal Savings History)
                1 -> {
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
                                    text = "UBWIZIGAME BWAWE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Button(
                                    onClick = { showSavingsDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = IkibinaGoldDark),
                                    enabled = member.status == "ACTIVE"
                                ) {
                                    Text("+ IZIGAME", fontSize = 12.sp)
                                }
                            }
                        }

                        if (personalSavings.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Nta bwizigame urakora. Kanda kuri '+ IZIGAME'.", color = Color(0xFF64748B))
                                }
                            }
                        }

                        items(personalSavings) { s ->
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
                                        Column {
                                            Text(
                                                text = "Imigabane ${s.sharesCount} (${s.weekCode})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = dateFormat.format(Date(s.submittedAt)),
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "RWF ${formatter.format(s.amount.toLong())}",
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 16.sp,
                                                color = IkibinaDarkGray
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            StatusBadge(status = s.status)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "MoMo Tx: ${s.paymentReference.ifEmpty { "N/A" }}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    if (s.status == "REJECTED" && !s.rejectionReason.isNullOrBlank()) {
                                        Text(
                                            text = "Impamvu yo kwangwa: ${s.rejectionReason}",
                                            fontSize = 11.sp,
                                            color = IkibinaRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2: INGUZANYA (Loans and Repayments)
                2 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "INGUZANYA ZAWE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Button(
                                    onClick = { showLoanDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = IkibinaGoldDark),
                                    enabled = member.status == "ACTIVE"
                                ) {
                                    Text("+ SABA INGUZANYA", fontSize = 12.sp)
                                }
                            }
                        }

                        if (personalLoans.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Nta nguzanyo urasaba.", color = Color(0xFF64748B))
                                }
                            }
                        }

                        items(personalLoans) { loan ->
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
                                                text = "Inguzanyo: RWF ${formatter.format(loan.amountRequested.toLong())}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = "Impamvu: ${loan.reason}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                        StatusBadge(status = loan.status)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (loan.status == "ACTIVE") {
                                        Column(modifier = Modifier.fillMaxWidth()) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = "Igishoro gisigaye: RWF ${formatter.format(loan.outstandingPrincipal.toLong())}",
                                                        fontWeight = FontWeight.Bold,
                                                        color = IkibinaBrightBlue,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = "Inyungu isigaye: RWF ${formatter.format(loan.outstandingInterest.toLong())}",
                                                        fontSize = 12.sp,
                                                        color = IkibinaYellowDark,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                    Text(
                                                        text = "Yose hamwe asigaye: RWF ${formatter.format(loan.outstandingBalance.toLong())}",
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = IkibinaRed,
                                                        fontSize = 13.sp
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            // TWO DISTINCT BUTTONS: ISHYURA INGUZANYA & ISHYURA INYUNGU
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Button(
                                                    onClick = {
                                                        selectedRepayMode = "PRINCIPAL"
                                                        showRepayDialogForLoan = loan
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .testTag("repay_principal_action_${loan.id}"),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("ISHYURA INGUZANYA", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }

                                                Button(
                                                    onClick = {
                                                        selectedRepayMode = "INTEREST"
                                                        showRepayDialogForLoan = loan
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = IkibinaYellow),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .testTag("repay_interest_action_${loan.id}"),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("ISHYURA INYUNGU", color = IkibinaDarkGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Outstanding Fines Section
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "IBIHANO BYAWE (RWF 100 fixed buri cyumweru cyasibwe)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = IkibinaDarkGray
                            )
                        }

                        if (personalFines.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White)
                                        .padding(14.dp)
                                ) {
                                    Text("Nta bihano ufite. Uhora uzigama ku gihe!", color = IkibinaGreen, fontSize = 13.sp)
                                }
                            }
                        }

                        items(personalFines) { fine ->
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
                                            text = "${fine.weekCode}: RWF ${fine.amount.toInt()}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = IkibinaRed
                                        )
                                        Text(text = fine.reason, fontSize = 11.sp, color = Color(0xFF64748B))
                                    }

                                    if (fine.status == "PENDING_PAYMENT") {
                                        Button(
                                            onClick = { showPayFineDialog = fine },
                                            colors = ButtonDefaults.buttonColors(containerColor = IkibinaRed)
                                        ) {
                                            Text("ISHYURA (100)", fontSize = 11.sp)
                                        }
                                    } else {
                                        StatusBadge(status = fine.status)
                                    }
                                }
                            }
                        }
                    }
                }

                // 3: HISTORICAL Y'IBIKORWA (Personal transaction ledger)
                3 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "HISTORICAL Y'IBIKORWA BYAWE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }

                        if (personalTransactions.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Nta bikorwa birakorwa.", color = Color(0xFF64748B))
                                }
                            }
                        }

                        items(personalTransactions) { tx ->
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
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = dateFormat.format(Date(tx.timestamp)),
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Text(
                                        text = (if (tx.direction == "INFLOW") "+ " else "- ") +
                                                "RWF ${formatter.format(tx.amount.toLong())}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (tx.direction == "INFLOW") IkibinaGreen else IkibinaRed
                                    )
                                }
                            }
                        }
                    }
                }

                // 4: URUGANIRIRO RW'ITSINDA (Group Chat)
                4 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "URUGANIRIRO RW'ITSINDA",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Ibiganiro n'ubusabane hagati y'abanyamuryango",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Messages list
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(chatMessages) { msg ->
                                val isMe = msg.senderId == member.id
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isMe) IkibinaGoldLight else Color.White)
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = msg.senderName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = if (msg.senderRole == "ADMIN") IkibinaGoldDark else IkibinaDarkGray
                                                )
                                                if (msg.senderRole == "ADMIN") {
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "(Admin)",
                                                        fontSize = 10.sp,
                                                        color = IkibinaGoldDark,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = msg.message, fontSize = 13.sp)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = dateFormat.format(Date(msg.timestamp)),
                                                fontSize = 9.sp,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Message input box
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = chatInputText,
                                onValueChange = { chatInputText = it },
                                placeholder = { Text("Andika ubutumwa...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("chat_input_text"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (chatInputText.isNotBlank()) {
                                        viewModel.sendChatMessage(member.fullName, chatInputText)
                                        chatInputText = ""
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(IkibinaGoldDark)
                                    .testTag("send_chat_message_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Ohereza",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                // 5: REBA ABANYAMURYANGO (Member Directory)
                5 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Column {
                                Text(
                                    text = "REBA ABANYAMURYANGO",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = IkibinaDarkGray
                                )
                                Text(
                                    text = "Abanyamuryango mubana mu itsinda",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        items(allActiveMembers) { m ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = IkibinaCardBg)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(IkibinaGoldLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = m.fullName.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold,
                                            color = IkibinaGoldDark,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = m.fullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = IkibinaDarkGray
                                        )
                                        Text(
                                            text = m.phone.take(4) + "•••" + m.phone.takeLast(3),
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    StatusBadge(status = m.status)
                                }
                            }
                        }
                    }
                }

                // 6: NOTIFICATIONS
                6 -> {
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
                                    text = "UBUTUMWA BWAWE",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Soma Byose",
                                    color = IkibinaGoldDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { viewModel.markAllNotificationsRead() }
                                )
                            }
                        }

                        if (personalNotifications.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .padding(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Nta butumwa bushya urabona.", color = Color(0xFF64748B))
                                }
                            }
                        }

                        items(personalNotifications) { n ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (n.isRead) IkibinaCardBg else IkibinaGoldLight
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = n.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = dateFormat.format(Date(n.createdAt)),
                                            fontSize = 10.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = n.message, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // 7: PROFILE
                7 -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "UMWIRONDORO WAWE (PROFILE)",
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
                                    ProfileInfoRow("Amazina yose", member.fullName)
                                    ProfileInfoRow("Nimero ya telefone", member.phone)
                                    ProfileInfoRow("Email", member.email)
                                    ProfileInfoRow("Itsinda", member.savingsGroupName)
                                    ProfileInfoRow("Imimerere (Status)", member.status)
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = { showChangePasswordDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = IkibinaGoldDark),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("HINDURA PASSCODE")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialogs
    if (showSavingsDialog) {
        SavingsDialog(
            onDismiss = { showSavingsDialog = false },
            onSubmit = { shares, ref, note ->
                viewModel.submitSavings(shares, ref, note)
                showSavingsDialog = false
            }
        )
    }

    if (showLoanDialog) {
        LoanRequestDialog(
            communalAvailableAmount = communalMoney,
            onDismiss = { showLoanDialog = false },
            onSubmit = { amount, reason, duration ->
                viewModel.requestLoan(amount, reason, duration)
                showLoanDialog = false
            }
        )
    }

    showRepayDialogForLoan?.let { loan ->
        RepayLoanDialog(
            loan = loan,
            initialMode = selectedRepayMode,
            onDismiss = { showRepayDialogForLoan = null },
            onSubmit = { amount, paymentType, ref ->
                viewModel.submitRepayment(loan.id, amount, paymentType, ref)
                showRepayDialogForLoan = null
            }
        )
    }

    showPayFineDialog?.let { fine ->
        PayFineDialog(
            fine = fine,
            onDismiss = { showPayFineDialog = null },
            onSubmit = { ref ->
                viewModel.payFine(fine.id, ref)
                showPayFineDialog = null
            }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            isMandatoryFirstChange = false,
            onDismiss = { showChangePasswordDialog = false },
            onSubmit = { oldPass, newPass ->
                onChangePasswordRequest()
                showChangePasswordDialog = false
            }
        )
    }
}

@Composable
private fun MemberActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (bgColor == IkibinaGoldDark) Color.White.copy(alpha = 0.2f) else IkibinaGoldLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (bgColor == IkibinaGoldDark) Color.White else IkibinaGoldDark,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = textColor
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = if (bgColor == IkibinaGoldDark) Color.White.copy(alpha = 0.85f) else Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = IkibinaDarkGray)
    }
}
