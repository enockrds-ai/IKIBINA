package com.example.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.IkibinaBrightBlue
import com.example.ui.theme.IkibinaGoldDark
import com.example.ui.theme.IkibinaRed

@Composable
fun ChangePasswordDialog(
    isMandatoryFirstChange: Boolean = false,
    onDismiss: () -> Unit,
    onSubmit: (oldPass: String, newPass: String) -> Unit
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = {
            if (!isMandatoryFirstChange) onDismiss()
        },
        title = {
            Text(
                text = if (isMandatoryFirstChange) "Hindura Ijambobanga ry'Ibanze" else "Guhindura Passcode",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (isMandatoryFirstChange) {
                    Text(
                        text = "Ku nshuro ya mbere winjiye, ugomba guhindura ijambobanga ry'ibanze ry'umutekano.",
                        fontSize = 12.sp,
                        color = IkibinaGoldDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

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

                OutlinedTextField(
                    value = oldPass,
                    onValueChange = { oldPass = it },
                    label = { Text("Ijambobanga risanzwe", fontWeight = FontWeight.SemiBold) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    textStyle = inputTextStyle,
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth().testTag("old_password_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("Ijambobanga rishya", fontWeight = FontWeight.SemiBold) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    textStyle = inputTextStyle,
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth().testTag("new_password_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = { confirmPass = it },
                    label = { Text("Emeza iryari rishya", fontWeight = FontWeight.SemiBold) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    textStyle = inputTextStyle,
                    colors = inputColors,
                    modifier = Modifier.fillMaxWidth().testTag("confirm_new_password_input"),
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
                    if (oldPass.isBlank() || newPass.isBlank()) {
                        errorMsg = "Uzuza imyanya yose."
                    } else if (newPass.length < 4) {
                        errorMsg = "Ijambobanga rishya rigomba kugira nibura inyuguti/imibare 4."
                    } else if (newPass != confirmPass) {
                        errorMsg = "Amagambo y'ibanga mashya ntiyemeranya."
                    } else {
                        onSubmit(oldPass, newPass)
                    }
                },
                modifier = Modifier.testTag("submit_change_password_button"),
                colors = ButtonDefaults.buttonColors(containerColor = IkibinaBrightBlue)
            ) {
                Text("EMEZA GUHINDURA")
            }
        },
        dismissButton = {
            if (!isMandatoryFirstChange) {
                OutlinedButton(onClick = onDismiss) {
                    Text("REKA")
                }
            }
        }
    )
}
