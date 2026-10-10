package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IkibinaDarkGray
import com.example.ui.theme.IkibinaGoldDark
import com.example.ui.theme.IkibinaGoldLight
import com.example.ui.theme.IkibinaRed
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LoanRequestDialog(
    communalAvailableAmount: Double,
    onDismiss: () -> Unit,
    onSubmit: (amount: Double, reason: String, durationWeeks: Int) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var reasonText by remember { mutableStateOf("") }
    var durationWeeks by remember { mutableIntStateOf(4) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val maxLoanLimit = communalAvailableAmount * 0.80
    val formatter = remember { NumberFormat.getNumberInstance(Locale.US) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = IkibinaGoldDark,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SABA INGUZANYA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Rule Banner: 80% communal limit & 0 RWF personal savings allowed
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(IkibinaGoldLight)
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = IkibinaGoldDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Amategeko y'inguzanyo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = IkibinaGoldDark
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Ushobora gusaba inguzanyo n'ubwo ubwizigame bwawe bwaba ari RWF 0.",
                            fontSize = 12.sp,
                            color = IkibinaDarkGray
                        )
                        Text(
                            text = "• Inzira ntabwo irenga 80% by'amafaranga asigaye mu kigega cya rusange.",
                            fontSize = 12.sp,
                            color = IkibinaDarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ntarengwa ishoboka ubu: RWF ${formatter.format(maxLoanLimit.toLong())}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = IkibinaGoldDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it.filter { ch -> ch.isDigit() }
                    },
                    label = { Text("Amafaranga usaba (RWF)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("loan_amount_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Calculated interest (5%)
                val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
                if (parsedAmount > 0) {
                    val interest = parsedAmount * 0.05
                    val totalDue = parsedAmount + interest
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(androidx.compose.ui.graphics.Color(0xFFF1F5F9))
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = "Inyungu (5%): RWF ${formatter.format(interest.toLong())}",
                                fontSize = 12.sp,
                                color = IkibinaDarkGray
                            )
                            Text(
                                text = "Ayose azishyurwa: RWF ${formatter.format(totalDue.toLong())}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = IkibinaGoldDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("Impamvu y'inguzanyo (Urugero: Ubucuruzi, Ubuvuzi)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("loan_reason_input"),
                    maxLines = 3
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
                    if (amount == null || amount < 1000) {
                        errorMsg = "Amafaranga agomba kuba nibura RWF 1,000."
                    } else if (amount > maxLoanLimit) {
                        errorMsg = "Amafaranga arenze 80% by'asigaye mu kigega (Max: RWF ${formatter.format(maxLoanLimit.toLong())})."
                    } else if (reasonText.isBlank()) {
                        errorMsg = "Sobanura impamvu usaba inguzanyo."
                    } else {
                        onSubmit(amount, reasonText, durationWeeks)
                    }
                },
                modifier = Modifier.testTag("submit_loan_request_button"),
                colors = ButtonDefaults.buttonColors(containerColor = IkibinaGoldDark)
            ) {
                Text("SABA INGUZANYA")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("REKA")
            }
        }
    )
}
