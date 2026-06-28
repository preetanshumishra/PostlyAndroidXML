package com.preetanshumishra.postlyandroidxml.screens.postlist

sealed interface PostListEvent {
    data object LoadPosts : PostListEvent
    data object RefreshPosts : PostListEvent
}
