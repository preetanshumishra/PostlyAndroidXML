package com.preetanshu.postlyandroidxml.screens.postlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.preetanshu.postlyandroidxml.PostlyApplication
import com.preetanshu.postlyandroidxml.R
import com.preetanshu.postlyandroidxml.databinding.ActivityPostListBinding
import com.preetanshu.postlyandroidxml.screens.login.LoginActivity
import com.preetanshu.postlyandroidxml.screens.userinfo.UserInfoBottomSheet
import com.preetanshu.postlyandroidxml.services.ImageLoaderService
import com.preetanshu.postlyandroidxml.services.NetworkService
import kotlinx.coroutines.launch
import javax.inject.Inject

class PostListActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_TOKEN = "extra_token"
        private const val EXTRA_IS_GUEST = "extra_is_guest"

        fun start(context: Context, token: String, isGuest: Boolean) {
            val intent = Intent(context, PostListActivity::class.java).apply {
                putExtra(EXTRA_TOKEN, token)
                putExtra(EXTRA_IS_GUEST, isGuest)
            }
            context.startActivity(intent)
        }
    }

    @Inject lateinit var networkService: NetworkService
    @Inject lateinit var imageLoaderService: ImageLoaderService

    private lateinit var binding: ActivityPostListBinding
    private lateinit var adapter: PostListAdapter

    private val token by lazy { intent.getStringExtra(EXTRA_TOKEN).orEmpty() }
    private val isGuest by lazy { intent.getBooleanExtra(EXTRA_IS_GUEST, false) }

    private val viewModel: PostListViewModel by viewModels {
        PostListViewModelFactory(token, networkService, isGuest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as PostlyApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityPostListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeState()

        viewModel.onEvent(PostListEvent.LoadPosts)
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.menu_post_list)
        binding.toolbar.menu.findItem(R.id.action_logout).title =
            if (isGuest) getString(R.string.exit) else getString(R.string.logout)

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_logout) {
                if (isGuest) {
                    showExitDialog()
                } else {
                    navigateBack()
                }
                true
            } else {
                false
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PostListAdapter(
            imageLoaderService = imageLoaderService,
            coroutineScope = lifecycleScope,
            onUserClicked = { item -> showUserInfo(item) }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    if (state.errorMessage != null) {
                        binding.errorText.text = state.errorMessage
                        binding.errorText.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.errorText.visibility = View.GONE
                    }

                    if (!state.isLoading && state.errorMessage == null) {
                        binding.recyclerView.visibility = View.VISIBLE
                        adapter.submitList(state.displayItems)
                    }
                }
            }
        }
    }

    private fun showUserInfo(item: PostItem) {
        UserInfoBottomSheet.newInstance(
            username = item.username,
            avatarUrl = item.avatarUrl,
            email = item.userEmail
        ).show(supportFragmentManager, "user_info")
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.exit_message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                navigateBack()
            }
            .show()
    }

    private fun navigateBack() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
