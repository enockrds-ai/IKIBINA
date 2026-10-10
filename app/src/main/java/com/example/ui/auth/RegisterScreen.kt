package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IkibinaBrightBlue
import com.example.ui.theme.IkibinaCardBg
import com.example.ui.theme.IkibinaDarkGray
import com.example.ui.theme.IkibinaGoldDark
import com.example.ui.theme.IkibinaGreen
import com.example.ui.theme.IkibinaGreenLight
import com.example.ui.theme.IkibinaRed
import com.example.ui.theme.IkibinaYellow

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var confirmPasscode by remember { mutableStateOf("") }
    var groupName by remember { mutableStateOf("Ikibina cy'Ubwizigame n'Inguzanyo") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                IconButton(
                    onClick = onNavigateBackToLogin,
                    modifier = Modifier.testTag("back_to_login_button")
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
                        text = "Iyandikishe muri IKIBINA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = IkibinaDarkGray
                    )
                    Text(
                        text = "Kora konti nshya y'ubwizigame",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Registration Success Banner
            if (uiState is AuthUiState.RegistrationSuccess) {
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
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = IkibinaGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = (uiState as AuthUiState.RegistrationSuccess).message,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = IkibinaGreen,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Admin aragenzura amakuru yawe akwemeze. Numara kwemezwa uzashobora kuzigama no gusaba inguzanyo.",
                            fontSize = 13.sp,
                            color = IkibinaDarkGray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.clearState()
                                onNavigateBackToLogin()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IkibinaGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("SUBIRA KWINJIRA (LOGIN)")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
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
                        // Full name - Text strictly #000000 (black)
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Amazina yose", fontWeight = FontWeight.SemiBold) },
                            placeholder = { Text("Urugero: Jean Bosco Habimana") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = IkibinaBrightBlue)
                            },
                            textStyle = inputTextStyle,
                            colors = inputColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_fullname_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Telephone (Validated local Rwanda format) - Text strictly #000000 (black)
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Nimero ya telefone (078/079/072/073)", fontWeight = FontWeight.SemiBold) },
                            placeholder = { Text("0788123456") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = IkibinaBrightBlue)
                            },
                            textStyle = inputTextStyle,
                            colors = inputColors,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_phone_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Email (Optional) - Text strictly #000000 (black)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email (Si ngombwa)", fontWeight = FontWeight.SemiBold) },
                            placeholder = { Text("izina@gmail.com") },
                            leadingIcon = {
                                Icon(Icons.Default.Mail, contentDescription = null, tint = IkibinaBrightBlue)
                            },
                            textStyle = inputTextStyle,
                            colors = inputColors,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_email_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Group selection - Text strictly #000000 (black)
                        OutlinedTextField(
                            value = groupName,
                            onValueChange = { groupName = it },
                            label = { Text("Itsinda ry'ubwizigame", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(Icons.Default.Group, contentDescription = null, tint = IkibinaBrightBlue)
                            },
                            textStyle = inputTextStyle,
                            colors = inputColors,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_group_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Passcode - Text strictly #000000 (black)
                        OutlinedTextField(
                            value = passcode,
                            onValueChange = { passcode = it },
                            label = { Text("Passcode (Nibura imibare 4)", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = IkibinaBrightBlue)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (passwordVisible) "Hisha passcode" else "Erekana passcode",
                                        tint = Color(0xFF334155)
                                    )
                                }
                            },
                            textStyle = inputTextStyle,
                            colors = inputColors,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_passcode_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Confirm passcode - Text strictly #000000 (black)
                        OutlinedTextField(
                            value = confirmPasscode,
                            onValueChange = { confirmPasscode = it },
                            label = { Text("Emeza Passcode", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = IkibinaBrightBlue)
                            },
                            textStyle = inputTextStyle,
                            colors = inputColors,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_confirm_passcode_input"),
                            singleLine = true
                        )

                        // Error message
                        if (uiState is AuthUiState.Error) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEE2E2))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = (uiState as AuthUiState.Error).message,
                                    color = IkibinaRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                viewModel.register(
                                    fullName = fullName,
                                    email = email,
                                    phone = phone,
                                    passcode = passcode,
                                    confirmPasscode = confirmPasscode,
                                    groupName = groupName
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_registration_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue),
                            shape = RoundedCornerShape(12.dp),
                            enabled = uiState !is AuthUiState.Loading
                        ) {
                            if (uiState is AuthUiState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                            } else {
                                Text(
                                    text = "IYANDIKISHE",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
