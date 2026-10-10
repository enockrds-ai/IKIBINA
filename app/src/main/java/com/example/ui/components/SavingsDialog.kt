package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.schedule.SavingsScheduleHelper
import com.example.ui.theme.IkibinaBlueLight
import com.example.ui.theme.IkibinaBrightBlue
import com.example.ui.theme.IkibinaCardBg
import com.example.ui.theme.IkibinaDarkGray
import com.example.ui.theme.IkibinaGreen
import com.example.ui.theme.IkibinaGreenLight
import com.example.ui.theme.IkibinaRed
import com.example.ui.theme.IkibinaRedLight
import com.example.ui.theme.IkibinaYellow
import com.example.ui.theme.IkibinaYellowDark
import com.example.ui.theme.IkibinaYellowLight
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SavingsDialog(
    onDismiss: () -> Unit,
    onSubmit: (amount: Double, ref: String, note: String) -> Unit
) {
    val context = LocalContext.current
    var amountText by remember { mutableStateOf("1000") }
    var paymentRef by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var dialerStatusMessage by remember { mutableStateOf<String?>(null) }
    var dialerStatusIsError by remember { mutableStateOf(false) }

    val isWithinSchedule = remember { SavingsScheduleHelper.isWithinSavingsPeriod() }
    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }
    val parsedAmount = amountText.toDoubleOrNull() ?: 0.0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    tint = IkibinaBrightBlue,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "KWINJIZA UBWIZIGAME",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = IkibinaDarkGray
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Schedule Banner (Tuesday 17:00 to Wednesday 15:00 Rwanda time)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isWithinSchedule) IkibinaGreenLight else IkibinaRedLight)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = if (isWithinSchedule) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isWithinSchedule) IkibinaGreen else IkibinaRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isWithinSchedule)
                                    "Igihe cyo kuzigama KIRAFUNGUYE (Nta gihano)"
                                else
                                    "Igihe cyo kuzigama CYARARENZE (Late)",
                                fontWeight = FontWeight.Bold,
                                color = if (isWithinSchedule) IkibinaGreen else IkibinaRed,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isWithinSchedule)
                                    "Ku wa kabiri 17:00 – Ku wa gatatu 15:00 (Rwanda Time). Amafaranga yose azashyirwa mu bwizigame bwawe."
                                else
                                    "Uzirikane ko hazacibwa igihano cya RWF 100 cy'icyumweru cyasibwe, gitandukanywe n'ubwizigame.",
                                fontSize = 11.sp,
                                color = IkibinaDarkGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Enter Amount field
                Text(
                    text = "Injiza amafaranga wifuza kwizigama (RWF):",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = IkibinaDarkGray
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { ch -> ch.isDigit() }
                    },
                    label = { Text("Amafaranga (RWF)") },
                    placeholder = { Text("Urugero: 1000, 2000, 4000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("savings_amount_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick presets (500, 1000, 2000, 4000)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(500, 1000, 2000, 4000).forEach { preset ->
                        val selected = amountText == preset.toString()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) IkibinaBrightBlue else Color(0xFFF1F5F9))
                                .border(
                                    width = 1.dp,
                                    color = if (selected) IkibinaBrightBlue else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { amountText = preset.toString() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${formatter.format(preset)}",
                                color = if (selected) Color.White else IkibinaDarkGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Allocation Breakdown preview if late
                if (!isWithinSchedule && parsedAmount > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(IkibinaYellowLight)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Imiterere y'ubwishyu bwa nyuma y'igihe:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = IkibinaYellowDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (parsedAmount <= 100) {
                                Text(
                                    text = "⚠ Shortfall: Amafaranga RWF ${formatter.format(parsedAmount.toLong())} ntahagije igihano cya RWF 100 n'ubwizigame. Ongera amafaranga.",
                                    color = IkibinaRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Text(
                                    text = "• Amafaranga wishyura: RWF ${formatter.format(parsedAmount.toLong())}",
                                    fontSize = 11.sp,
                                    color = IkibinaDarkGray
                                )
                                Text(
                                    text = "• Igihano cyo gutinda: RWF 100 (Cyihariye)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = IkibinaRed
                                )
                                Text(
                                    text = "• Ayinjizwa mu bwizigame: RWF ${formatter.format((parsedAmount - 100).toLong())}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IkibinaGreen
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // PROMINENT USSD PAYMENT SECTION: "KANDA HANO WISHYURE"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = IkibinaBlueLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = IkibinaBrightBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Kwishyura kuri Mobile Money (USSD)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = IkibinaBrightBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Kode ya MoMo izafunguka muri dialer: ${UssdDialerHelper.USSD_CODE}",
                            fontSize = 12.sp,
                            color = IkibinaDarkGray,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // REQUIRED BUTTON: "KANDA HANO WISHYURE" - Automatically triggers dialer
                        Button(
                            onClick = {
                                val launched = UssdDialerHelper.openDialer(context, UssdDialerHelper.USSD_CODE)
                                if (launched) {
                                    dialerStatusIsError = false
                                    dialerStatusMessage = "Dialer yafunguwe ifite kode ${UssdDialerHelper.USSD_CODE}. Emeza ubwishyu muri MoMo kuri telefone yawe. Numara kwishyura, kanda 'OHEREZA UBWIZIGAME' hasi kugira ngo icyifuzo cyawe kigume ari Pending kizemezwe na Admin."
                                } else {
                                    dialerStatusIsError = true
                                    dialerStatusMessage = "Telefone irasaba uruhushya (permission) cyangwa kwemeza n'intoki kugira ngo ifungure dialer na kode ${UssdDialerHelper.USSD_CODE}. Ntabwo ubwishyu burangira kugeza wemeje kuri MoMo."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("pay_ussd_dial_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = IkibinaYellow),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = IkibinaDarkGray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KANDA HANO WISHYURE",
                                color = IkibinaDarkGray,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                        }

                        // Dialer launch / permission message
                        dialerStatusMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (dialerStatusIsError) Color(0xFFFEE2E2) else Color(0xFFDCFCE7))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = msg,
                                    fontSize = 11.sp,
                                    color = if (dialerStatusIsError) IkibinaRed else Color(0xFF166534),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Optional MoMo Reference (NO PROOF / SCREENSHOT REQUIRED)
                OutlinedTextField(
                    value = paymentRef,
                    onValueChange = { paymentRef = it },
                    label = { Text("Nimero y'ubwishyu bwa MoMo (Si ngombwa)") },
                    placeholder = { Text("Tx Ref cyangwa nimero ya telefone") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("savings_payment_ref_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Optional Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ibisobanuro bindi (Si ngombwa)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMsg ?: "",
                        color = IkibinaRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount < 500) {
                        errorMsg = "Injiza amafaranga yemewe yo kwizigama (nibura RWF 500)."
                    } else if (!isWithinSchedule && amount <= 100) {
                        errorMsg = "Amafaranga agomba kurenga RWF 100 kugira ngo akwire igihano cyo gutinda n'ubwizigame."
                    } else {
                        // Submit without requiring proof
                        onSubmit(amount, paymentRef, note)
                    }
                },
                modifier = Modifier.testTag("submit_savings_button"),
                colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue)
            ) {
                Text("OHEREZA UBWIZIGAME")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("REKA")
            }
        }
    )
}
