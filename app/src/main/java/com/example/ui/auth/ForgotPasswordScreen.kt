package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IkibinaBrightBlue
import com.example.ui.theme.IkibinaCardBg
import com.example.ui.theme.IkibinaDarkGray
import com.example.ui.theme.IkibinaGoldDark
import com.example.ui.theme.IkibinaGoldLight
import com.example.ui.theme.IkibinaGreen
import com.example.ui.theme.IkibinaGreenLight
import com.example.ui.theme.IkibinaRed

@Composable
fun ForgotPasswordScreen(
    viewModel: AuthViewModel,
    onNavigateBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var identifier by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1 = identifier, 2 = verify and reset

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // If OTP was sent, update step
    if (uiState is AuthUiState.PasswordResetOtpSent && step == 1) {
        step = 2
        // Prefill or display the generated OTP for on-device completion
        val sentOtp = (uiState as AuthUiState.PasswordResetOtpSent).otp
        if (otpInput.isEmpty()) {
            otpInput = sentOtp
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                IconButton(
                    onClick = onNavigateBackToLogin,
                    modifier = Modifier.testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Gusubira inyuma",
                        tint = IkibinaDarkGray
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Gusubiramo Passcode",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = IkibinaDarkGray
                    )
                    Text(
                        text = "Wibagiwe Passcode? Hindura indi nshya",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (uiState is AuthUiState.PasswordResetSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = IkibinaGreenLight)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = (uiState as AuthUiState.PasswordResetSuccess).message,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = IkibinaGreen
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                viewModel.clearState()
                                onNavigateBackToLogin()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IkibinaGreen)
                        ) {
                            Text("INJIRA NONAHA")
                        }
                    }
                }
            } else {
                // Input styling: Black typed text, white container, dark readable labels
                val inputColors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF000000),
                    unfocusedTextColor = Color(0xFF000000),
                    errorTextColor = Color(0xFF000000),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    errorContainerColor = Color.White,
                    focusedLabelColor = IkibinaBrightBlue,
                    unfocusedLabelColor = Color(0xFF334155),
                    errorLabelColor = IkibinaRed,
                    focusedPlaceholderColor = Color(0xFF64748B),
                    unfocusedPlaceholderColor = Color(0xFF94A3B8),
                    focusedBorderColor = IkibinaBrightBlue,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    cursorColor = Color(0xFF000000)
                )
                val inputTextStyle = TextStyle(
                    color = Color(0xFF000000),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        if (step == 1) {
                            Text(
                                text = "Intambwe ya 1: Injiza imyirondoro yawe",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF000000)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = identifier,
                                onValueChange = { identifier = it },
                                label = { Text("Nimero ya telefone cyangwa Email", fontWeight = FontWeight.SemiBold) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = IkibinaBrightBlue)
                                },
                                textStyle = inputTextStyle,
                                colors = inputColors,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reset_identifier_input"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = { viewModel.initiatePasswordReset(identifier) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("request_otp_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue),
                                shape = RoundedCornerShape(10.dp),
                                enabled = uiState !is AuthUiState.Loading
                            ) {
                                if (uiState is AuthUiState.Loading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Text("OHEREZA KODE YO GUSUBIRAMO", fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            // Step 2: Enter OTP & New Password
                            Text(
                                text = "Intambwe ya 2: Kode y'umutekano n'ijambobanga rishya",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF000000)
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (uiState is AuthUiState.PasswordResetOtpSent) {
                                val otp = (uiState as AuthUiState.PasswordResetOtpSent).otp
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(IkibinaGoldLight)
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Kode y'akanya gato (OTP) yo gusubiramo ni: $otp",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = IkibinaDarkGray
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { otpInput = it },
                                label = { Text("Kode (OTP)", fontWeight = FontWeight.SemiBold) },
                                leadingIcon = {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = IkibinaBrightBlue)
                                },
                                textStyle = inputTextStyle,
                                colors = inputColors,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reset_otp_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = { Text("Passcode nshya", fontWeight = FontWeight.SemiBold) },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = IkibinaBrightBlue)
                                },
                                textStyle = inputTextStyle,
                                colors = inputColors,
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("reset_new_password_input"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    viewModel.completePasswordReset(identifier, otpInput, newPassword)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_new_password_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue),
                                shape = RoundedCornerShape(10.dp),
                                enabled = uiState !is AuthUiState.Loading
                            ) {
                                if (uiState is AuthUiState.Loading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Text("HINDUZA PASSCODE", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (uiState is AuthUiState.Error) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = (uiState as AuthUiState.Error).message,
                                color = IkibinaRed,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
