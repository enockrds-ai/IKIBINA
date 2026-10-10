package com.example.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.UserEntity
import com.example.data.repository.IkibinaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: UserEntity) : AuthUiState
    data class Error(val message: String) : AuthUiState
    data class RegistrationSuccess(val message: String) : AuthUiState
    data class PasswordResetOtpSent(val identifier: String, val otp: String) : AuthUiState
    data class PasswordResetSuccess(val message: String) : AuthUiState
}

class AuthViewModel(private val repository: IkibinaRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfNeeded()
        }
    }

    fun login(identifier: String, pass: String) {
        if (identifier.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Injiza nimero ya telefone/email n'ijambobanga.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.login(identifier, pass)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _uiState.value = AuthUiState.Success(user)
                },
                onFailure = { err ->
                    _uiState.value = AuthUiState.Error(err.message ?: "Kwinjira byanze.")
                }
            )
        }
    }

    fun register(
        fullName: String,
        email: String,
        phone: String,
        passcode: String,
        confirmPasscode: String,
        groupName: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.registerUser(
                fullName = fullName,
                email = email,
                phone = phone,
                passcode = passcode,
                confirmPasscode = confirmPasscode,
                groupName = groupName
            )
            result.fold(
                onSuccess = { msg ->
                    _uiState.value = AuthUiState.RegistrationSuccess(msg)
                },
                onFailure = { err ->
                    _uiState.value = AuthUiState.Error(err.message ?: "Kwiyandikisha byanze.")
                }
            )
        }
    }

    fun initiatePasswordReset(identifier: String) {
        if (identifier.isBlank()) {
            _uiState.value = AuthUiState.Error("Injiza nimero ya telefone cyangwa email yawe.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.initiatePasswordReset(identifier)
            result.fold(
                onSuccess = { otp ->
                    _uiState.value = AuthUiState.PasswordResetOtpSent(identifier, otp)
                },
                onFailure = { err ->
                    _uiState.value = AuthUiState.Error(err.message ?: "Gusaba kode byanze.")
                }
            )
        }
    }

    fun completePasswordReset(identifier: String, otp: String, newPass: String) {
        if (otp.isBlank() || newPass.isBlank()) {
            _uiState.value = AuthUiState.Error("Uzuza kode n'ijambobanga rishya.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.resetPasswordWithOtp(identifier, otp, newPass)
            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState.PasswordResetSuccess("Passcode yahinduwe neza. Injira ubu.")
                },
                onFailure = { err ->
                    _uiState.value = AuthUiState.Error(err.message ?: "Guhindura passcode byanze.")
                }
            )
        }
    }

    fun changePassword(oldPass: String, newPass: String, onComplete: () -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repository.changePassword(user.id, oldPass, newPass)
            result.fold(
                onSuccess = {
                    _currentUser.value = user.copy(mustChangePassword = false)
                    _uiState.value = AuthUiState.Success(user.copy(mustChangePassword = false))
                    onComplete()
                },
                onFailure = { err ->
                    _uiState.value = AuthUiState.Error(err.message ?: "Guhindura ijambobanga byanze.")
                }
            )
        }
    }

    fun logout() {
        _currentUser.value = null
        _uiState.value = AuthUiState.Idle
    }

    fun clearState() {
        _uiState.value = AuthUiState.Idle
    }
}
