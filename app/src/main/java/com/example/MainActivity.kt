package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.IkibinaRepository
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.admin.AdminViewModel
import com.example.ui.auth.AuthUiState
import com.example.ui.auth.AuthViewModel
import com.example.ui.auth.ChangePasswordDialog
import com.example.ui.auth.ForgotPasswordScreen
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.member.MemberDashboardScreen
import com.example.ui.member.MemberViewModel
import com.example.ui.theme.MyApplicationTheme

enum class AuthScreen {
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = IkibinaRepository(database)

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    IkibinaApp(repository = repository)
                }
            }
        }
    }
}

@Composable
fun IkibinaApp(repository: IkibinaRepository) {
    val authViewModel = remember { AuthViewModel(repository) }
    val currentUser by authViewModel.currentUser.collectAsState()
    var currentAuthScreen by remember { mutableStateOf(AuthScreen.LOGIN) }
    var showMandatoryPasswordChange by remember { mutableStateOf(false) }

    val user = currentUser

    if (user != null) {
        // User is authenticated
        if (user.mustChangePassword) {
            showMandatoryPasswordChange = true
        }

        if (showMandatoryPasswordChange) {
            ChangePasswordDialog(
                isMandatoryFirstChange = true,
                onDismiss = { /* Non-dismissible until changed */ },
                onSubmit = { oldPass, newPass ->
                    authViewModel.changePassword(oldPass, newPass) {
                        showMandatoryPasswordChange = false
                    }
                }
            )
        }

        if (user.role == "ADMIN") {
            val adminViewModel = remember { AdminViewModel(repository) }
            BackHandler {
                authViewModel.logout()
            }
            AdminDashboardScreen(
                adminUser = user,
                viewModel = adminViewModel,
                onLogout = { authViewModel.logout() }
            )
        } else {
            val memberViewModel = remember(user.id) { MemberViewModel(repository, user.id) }
            BackHandler {
                authViewModel.logout()
            }
            MemberDashboardScreen(
                initialMember = user,
                viewModel = memberViewModel,
                onLogout = { authViewModel.logout() },
                onChangePasswordRequest = {
                    showMandatoryPasswordChange = true
                }
            )
        }
    } else {
        // User not logged in
        when (currentAuthScreen) {
            AuthScreen.LOGIN -> {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = {
                        authViewModel.clearState()
                        currentAuthScreen = AuthScreen.REGISTER
                    },
                    onNavigateToForgotPassword = {
                        authViewModel.clearState()
                        currentAuthScreen = AuthScreen.FORGOT_PASSWORD
                    }
                )
            }
            AuthScreen.REGISTER -> {
                BackHandler {
                    authViewModel.clearState()
                    currentAuthScreen = AuthScreen.LOGIN
                }
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateBackToLogin = {
                        authViewModel.clearState()
                        currentAuthScreen = AuthScreen.LOGIN
                    }
                )
            }
            AuthScreen.FORGOT_PASSWORD -> {
                BackHandler {
                    authViewModel.clearState()
                    currentAuthScreen = AuthScreen.LOGIN
                }
                ForgotPasswordScreen(
                    viewModel = authViewModel,
                    onNavigateBackToLogin = {
                        authViewModel.clearState()
                        currentAuthScreen = AuthScreen.LOGIN
                    }
                )
            }
        }
    }
}
