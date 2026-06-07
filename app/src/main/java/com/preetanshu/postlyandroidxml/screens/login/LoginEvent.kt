package com.preetanshu.postlyandroidxml.screens.login

sealed interface LoginEvent {
    data class UsernameChanged(val value: String) : LoginEvent
    data class PasswordChanged(val value: String) : LoginEvent
    data object LoginClicked : LoginEvent
    data object ContinueAsGuestClicked : LoginEvent
}
