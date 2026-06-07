package com.preetanshu.postlyandroidxml.screens.login

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val token: String? = null,
    val isGuest: Boolean = false,
    val isLoginSuccessful: Boolean = false
)
