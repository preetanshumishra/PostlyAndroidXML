package com.preetanshumishra.postlyandroidxml.screens.postlist

data class PostItem(
    val postId: Int,
    val userId: Int,
    val title: String,
    val body: String,
    val username: String,
    val avatarUrl: String,
    val userEmail: String
)

data class PostListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val displayItems: List<PostItem> = emptyList()
)
