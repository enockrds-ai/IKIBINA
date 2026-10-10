package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FineEntity
import com.example.ui.theme.IkibinaBrightBlue
import com.example.ui.theme.IkibinaDarkGray
import com.example.ui.theme.IkibinaRed
import com.example.ui.theme.IkibinaRedLight
import com.example.ui.theme.IkibinaYellow

@Composable
fun PayFineDialog(
    fine: FineEntity,
    onDismiss: () -> Unit,
    onSubmit: (ref: String) -> Unit
) {
    val context = LocalContext.current
    var paymentRef by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var dialerStatusMessage by remember { mutableStateOf<String?>(null) }
    var dialerStatusIsError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = IkibinaRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ISHYURA IGIHANO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = IkibinaDarkGray
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(IkibinaRedLight)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Igihano cy'icyumweru: ${fine.weekCode}",
                            fontWeight = FontWeight.Bold,
                            color = IkibinaRed,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = fine.reason,
                            fontSize = 12.sp,
                            color = IkibinaDarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Amafaranga: RWF ${fine.amount.toInt()} (Fixed 100 RWF)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = IkibinaRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // USSD Code Dialing
                Text(
                    text = "Kode ya MoMo izafunguka muri dialer: ${UssdDialerHelper.USSD_CODE}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = IkibinaBrightBlue
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val launched = UssdDialerHelper.openDialer(context, UssdDialerHelper.USSD_CODE)
                        if (launched) {
                            dialerStatusIsError = false
                            dialerStatusMessage = "Dialer yafunguwe ifite kode ${UssdDialerHelper.USSD_CODE}. Emeza ubwishyu bwawe muri MoMo kuri telefone. Numara kwishyura, kanda 'EMEZA KO WISHYUYE' hasi kugira ngo ubike icyifuzo (Pending) kizemezwe na Admin."
                        } else {
                            dialerStatusIsError = true
                            dialerStatusMessage = "Telefone irasaba uruhushya (permission) cyangwa kwemeza n'intoki kugira ngo ifungure dialer na kode ${UssdDialerHelper.USSD_CODE}. Ntabwo ubwishyu burangira kugeza wemeje kuri MoMo."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IkibinaYellow)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = IkibinaDarkGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("KANDA HANO WISHYURE", color = IkibinaDarkGray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

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

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = paymentRef,
                    onValueChange = { paymentRef = it },
                    label = { Text("Nimero y'ubwishyu bwa MoMo (Si ngombwa)") },
                    placeholder = { Text("Tx ID cyangwa nimero ya telefone") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fine_payment_ref_input"),
                    singleLine = true
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(6.dp))
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
                    onSubmit(paymentRef.ifEmpty { "USSD: *182*8*1*412512#" })
                },
                modifier = Modifier.testTag("submit_fine_payment_button"),
                colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue)
            ) {
                Text("EMEZA UBWISHYU")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("REKA")
            }
        }
    )
}
