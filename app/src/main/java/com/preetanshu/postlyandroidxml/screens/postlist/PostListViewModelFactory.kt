package com.preetanshu.postlyandroidxml.screens.postlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.preetanshu.postlyandroidxml.services.NetworkService

class PostListViewModelFactory(
    private val token: String,
    private val networkService: NetworkService,
    private val isGuest: Boolean
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostListViewModel::class.java)) {
            return PostListViewModel(token, networkService, isGuest) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
