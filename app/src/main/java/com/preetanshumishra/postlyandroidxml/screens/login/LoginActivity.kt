package com.preetanshumishra.postlyandroidxml.screens.login

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.preetanshumishra.postlyandroidxml.PostlyApplication
import com.preetanshumishra.postlyandroidxml.databinding.ActivityLoginBinding
import com.preetanshumishra.postlyandroidxml.screens.postlist.PostListActivity
import com.preetanshumishra.postlyandroidxml.services.NetworkService
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    @Inject lateinit var networkService: NetworkService

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(networkService)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PostlyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.usernameField.doAfterTextChanged {
            viewModel.onEvent(LoginEvent.UsernameChanged(it.toString()))
        }

        binding.passwordField.doAfterTextChanged {
            viewModel.onEvent(LoginEvent.PasswordChanged(it.toString()))
        }

        binding.loginButton.setOnClickListener {
            viewModel.onEvent(LoginEvent.LoginClicked)
        }

        binding.guestButton.setOnClickListener {
            viewModel.onEvent(LoginEvent.ContinueAsGuestClicked)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.loginButton.isEnabled = !state.isLoading
                    binding.guestButton.isEnabled = !state.isLoading
                    binding.usernameField.isEnabled = !state.isLoading
                    binding.passwordField.isEnabled = !state.isLoading

                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    if (state.errorMessage != null) {
                        binding.errorText.text = state.errorMessage
                        binding.errorText.visibility = View.VISIBLE
                    } else {
                        binding.errorText.visibility = View.GONE
                    }

                    if (state.isLoginSuccessful && state.token != null) {
                        PostListActivity.start(
                            context = this@LoginActivity,
                            token = state.token,
                            isGuest = state.isGuest
                        )
                        finish()
                    }
                }
            }
        }
    }
}
