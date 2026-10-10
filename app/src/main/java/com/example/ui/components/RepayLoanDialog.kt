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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PriceCheck
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
import com.example.data.local.entity.LoanEntity
import com.example.ui.theme.IkibinaBlueLight
import com.example.ui.theme.IkibinaBrightBlue
import com.example.ui.theme.IkibinaDarkGray
import com.example.ui.theme.IkibinaGreen
import com.example.ui.theme.IkibinaRed
import com.example.ui.theme.IkibinaYellow
import com.example.ui.theme.IkibinaYellowLight
import java.text.NumberFormat
import java.util.Locale

@Composable
fun RepayLoanDialog(
    loan: LoanEntity,
    initialMode: String = "PRINCIPAL", // "PRINCIPAL" or "INTEREST"
    onDismiss: () -> Unit,
    onSubmit: (amount: Double, paymentType: String, ref: String) -> Unit
) {
    val context = LocalContext.current
    var selectedMode by remember { mutableStateOf(initialMode) } // "PRINCIPAL" or "INTEREST"
    val isPrincipal = selectedMode == "PRINCIPAL"

    val maxAmount = if (isPrincipal) loan.outstandingPrincipal else loan.outstandingInterest
    var amountText by remember(selectedMode) {
        mutableStateOf(if (maxAmount > 0) maxAmount.toLong().toString() else "0")
    }
    var paymentRef by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var dialerStatusMessage by remember { mutableStateOf<String?>(null) }
    var dialerStatusIsError by remember { mutableStateOf(false) }

    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PriceCheck,
                    contentDescription = null,
                    tint = IkibinaBrightBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPrincipal) "ISHYURA INGUZANYA (Igishoro)" else "ISHYURA INYUNGU (Interest)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
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
                // Section Selector Tabs: ISHYURA INGUZANYA vs ISHYURA INYUNGU
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isPrincipal) IkibinaBrightBlue else Color.Transparent)
                            .clickable {
                                selectedMode = "PRINCIPAL"
                                errorMsg = null
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ISHYURA INGUZANYA",
                            color = if (isPrincipal) Color.White else IkibinaDarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isPrincipal) IkibinaBrightBlue else Color.Transparent)
                            .clickable {
                                selectedMode = "INTEREST"
                                errorMsg = null
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ISHYURA INYUNGU",
                            color = if (!isPrincipal) Color.White else IkibinaDarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Outstanding balance summary card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isPrincipal) IkibinaBlueLight else IkibinaYellowLight)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = if (isPrincipal)
                                "Igishoro cy'inguzanyo gisigaye:"
                            else
                                "Inyungu y'inguzanyo isigaye (5%):",
                            fontSize = 12.sp,
                            color = IkibinaDarkGray
                        )
                        Text(
                            text = "RWF ${formatter.format(maxAmount.toLong())}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = if (isPrincipal) IkibinaBrightBlue else Color(0xFFB45309)
                        )
                        if (!isPrincipal) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Icyitonderwa: Kwishyura inyungu ntibigabanya igishoro cy'inguzanyo.",
                                fontSize = 11.sp,
                                color = IkibinaDarkGray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount input field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { ch -> ch.isDigit() }
                    },
                    label = {
                        Text(if (isPrincipal) "Amafaranga y'igishoro wishyura (RWF)" else "Amafaranga y'inyungu wishyura (RWF)")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("repay_amount_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Prominent USSD Payment Card: "KANDA HANO WISHYURE"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = IkibinaBlueLight)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Kwishyura kuri Mobile Money (USSD)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = IkibinaBrightBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
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
                                    dialerStatusMessage = "Dialer yafunguwe ifite kode ${UssdDialerHelper.USSD_CODE}. Emeza ubwishyu muri MoMo kuri telefone yawe. Numara kwishyura, kanda 'EMEZA UBWISHYU' hasi kugira ngo icyifuzo cyawe kigume ari Pending kizemezwe na Admin."
                                } else {
                                    dialerStatusIsError = true
                                    dialerStatusMessage = "Telefone irasaba uruhushya (permission) cyangwa kwemeza n'intoki kugira ngo ifungure dialer na kode ${UssdDialerHelper.USSD_CODE}. Ntabwo ubwishyu burangira kugeza wemeje kuri MoMo."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("repay_ussd_dial_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = IkibinaYellow),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = IkibinaDarkGray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "KANDA HANO WISHYURE",
                                color = IkibinaDarkGray,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }

                        // Dialer launch / permission message
                        dialerStatusMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (dialerStatusIsError) Color(0xFFFEE2E2) else Color(0xFFDCFCE7))
                                    .padding(8.dp)
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

                Spacer(modifier = Modifier.height(12.dp))

                // Optional MoMo Reference
                OutlinedTextField(
                    value = paymentRef,
                    onValueChange = { paymentRef = it },
                    label = { Text("Nimero y'ubwishyu bwa MoMo (Si ngombwa)") },
                    placeholder = { Text("Tx ID cyangwa nimero ya telefone") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("repay_payment_ref_input"),
                    singleLine = true
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMsg ?: "",
                        color = IkibinaRed,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || amount <= 0) {
                        errorMsg = "Injiza amafaranga yemewe yo kwishyura."
                    } else if (amount > maxAmount) {
                        errorMsg = "Amafaranga ashyuwe arenze asigaye (RWF ${formatter.format(maxAmount.toLong())})."
                    } else {
                        onSubmit(amount, selectedMode, paymentRef)
                    }
                },
                modifier = Modifier.testTag("submit_repay_button"),
                colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue)
            ) {
                Text("OHEREZA UBWISHYU")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("REKA")
            }
        }
    )
}
