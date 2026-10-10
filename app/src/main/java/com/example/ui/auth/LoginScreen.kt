package com.example.ui.auth

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.IkibinaBrightBlue
import com.example.ui.theme.IkibinaCardBg
import com.example.ui.theme.IkibinaDarkGray
import com.example.ui.theme.IkibinaGoldDark
import com.example.ui.theme.IkibinaGoldLight
import com.example.ui.theme.IkibinaRed
import com.example.ui.theme.IkibinaYellow

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding & Logo
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(IkibinaDarkGray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ikibina_logo),
                    contentDescription = "IKIBINA Logo",
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "IKIBINA",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = IkibinaDarkGray,
                letterSpacing = 1.sp
            )

            Text(
                text = "Porogaramu yo Gucunga Ibimina n'Ubwizigame",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(28.dp))

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

            // Login Card with crisp white background
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Injira muri konti",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Identifier input (Email or Phone) - Text strictly #000000 (black)
                    OutlinedTextField(
                        value = identifier,
                        onValueChange = {
                            identifier = it
                            if (uiState is AuthUiState.Error) viewModel.clearState()
                        },
                        label = { Text("Email cyangwa Telefone", fontWeight = FontWeight.SemiBold) },
                        placeholder = { Text("078XXXXXXX cyangwa email") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = IkibinaBrightBlue)
                        },
                        textStyle = inputTextStyle,
                        colors = inputColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_identifier_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password/Passcode input with toggle - Text strictly #000000 (black)
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (uiState is AuthUiState.Error) viewModel.clearState()
                        },
                        label = { Text("Passcode cyangwa Ijambobanga", fontWeight = FontWeight.SemiBold) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = IkibinaBrightBlue)
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible },
                                modifier = Modifier.testTag("toggle_password_visibility")
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hisha ijambobanga" else "Erekana ijambobanga",
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
                            .testTag("login_password_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Forgot Passcode Link
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Wibagiwe Passcode?",
                            color = IkibinaBrightBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .testTag("forgot_passcode_link")
                                .clickable { onNavigateToForgotPassword() }
                                .padding(vertical = 4.dp)
                        )
                    }

                    // Error display
                    if (uiState is AuthUiState.Error) {
                        Spacer(modifier = Modifier.height(8.dp))
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

                    Spacer(modifier = Modifier.height(18.dp))

                    // Login Button (Bright Blue with Bold White text)
                    Button(
                        onClick = { viewModel.login(identifier, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue),
                        shape = RoundedCornerShape(12.dp),
                        enabled = uiState !is AuthUiState.Loading
                    ) {
                        if (uiState is AuthUiState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "INJIRA",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Registration Link
            Row(
                modifier = Modifier
                    .clickable { onNavigateToRegister() }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ntabwo ufite konti? ",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "Kora konti nshya",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = IkibinaGoldDark,
                    modifier = Modifier.testTag("register_link")
                )
            }
        }
    }
}
