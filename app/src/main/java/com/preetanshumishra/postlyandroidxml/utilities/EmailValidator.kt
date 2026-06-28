package com.preetanshumishra.postlyandroidxml.utilities

object EmailValidator {

    private val validExtensions = listOf(".com", ".net", ".biz")

    fun isValidDomain(email: String): Boolean {
        val parts = email.split("@")
        if (parts.size != 2) return false

        val domain = parts[1].lowercase()
        return validExtensions.any { domain.endsWith(it) }
    }
}
