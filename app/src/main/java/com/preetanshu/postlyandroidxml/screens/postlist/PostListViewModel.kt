package com.preetanshu.postlyandroidxml.screens.postlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.preetanshu.postlyandroidxml.services.NetworkException
import com.preetanshu.postlyandroidxml.services.NetworkService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class PostListViewModel(
    private val token: String,
    private val networkService: NetworkService,
    val isGuest: Boolean
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState: StateFlow<PostListUiState> = _uiState.asStateFlow()

    fun onEvent(event: PostListEvent) {
        when (event) {
            PostListEvent.LoadPosts -> loadPosts()
            PostListEvent.RefreshPosts -> loadPosts()
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                supervisorScope {
                    val postsDeferred = async { networkService.fetchPosts(token) }
                    val usersDeferred = async { networkService.fetchUsers(token) }

                    val posts = postsDeferred.await()
                    val users = usersDeferred.await()

                    val userMap = users.associateBy { it.id }

                    val displayItems = posts.mapNotNull { post ->
                        val user = userMap[post.userId] ?: return@mapNotNull null
                        PostItem(
                            postId = post.id ?: return@mapNotNull null,
                            userId = post.userId ?: return@mapNotNull null,
                            title = post.title.orEmpty(),
                            body = post.body.orEmpty(),
                            username = user.username.orEmpty(),
                            avatarUrl = user.avatar.orEmpty(),
                            userEmail = user.email.orEmpty()
                        )
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        displayItems = displayItems
                    )
                }
            } catch (exception: NetworkException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Network error occurred."
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Something went wrong"
                )
            }
        }
    }
}
