package com.preetanshu.postlyandroidxml.screens.postlist

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.preetanshu.postlyandroidxml.R
import com.preetanshu.postlyandroidxml.databinding.ItemPostBinding
import com.preetanshu.postlyandroidxml.services.ImageLoaderService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PostListAdapter(
    private val imageLoaderService: ImageLoaderService,
    private val coroutineScope: CoroutineScope,
    private val onUserClicked: (PostItem) -> Unit
) : ListAdapter<PostItem, PostListAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PostViewHolder(
        private val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private var imageJob: Job? = null

        fun bind(item: PostItem) {
            binding.usernameText.text = item.username
            binding.titleText.text = item.title
            binding.bodyText.text = item.body

            binding.avatarImage.setImageResource(R.drawable.ic_account_circle)

            imageJob?.cancel()
            imageJob = coroutineScope.launch {
                if (item.avatarUrl.isNotEmpty()) {
                    val bitmap: Bitmap? = imageLoaderService.loadBitmap(item.avatarUrl)
                    if (bitmap != null) {
                        binding.avatarImage.setImageBitmap(bitmap)
                    }
                }
            }

            binding.avatarImage.setOnClickListener { onUserClicked(item) }
            binding.usernameText.setOnClickListener { onUserClicked(item) }
        }
    }

    private class PostDiffCallback : DiffUtil.ItemCallback<PostItem>() {
        override fun areItemsTheSame(oldItem: PostItem, newItem: PostItem): Boolean {
            return oldItem.postId == newItem.postId
        }

        override fun areContentsTheSame(oldItem: PostItem, newItem: PostItem): Boolean {
            return oldItem == newItem
        }
    }
}
