package com.preetanshumishra.postlyandroidxml.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshumishra.postlyandroidxml.services.NetworkException
import com.preetanshumishra.postlyandroidxml.services.NetworkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val networkService: NetworkService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameChanged -> {
                _uiState.value = _uiState.value.copy(username = event.value)
            }
            is LoginEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = event.value)
            }
            is LoginEvent.LoginClicked -> {
                login(
                    username = _uiState.value.username,
                    password = _uiState.value.password,
                    isGuest = false
                )
            }
            is LoginEvent.ContinueAsGuestClicked -> {
                login(username = "", password = "", isGuest = true)
            }
        }
    }

    private fun login(username: String, password: String, isGuest: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                isLoginSuccessful = false
            )

            try {
                val token = networkService.fetchUserToken(username, password)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    token = token,
                    isGuest = isGuest,
                    isLoginSuccessful = true
                )
            } catch (exception: NetworkException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Network error occurred.",
                    isLoginSuccessful = false
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Something went wrong.",
                    isLoginSuccessful = false
                )
            }
        }
    }
}
