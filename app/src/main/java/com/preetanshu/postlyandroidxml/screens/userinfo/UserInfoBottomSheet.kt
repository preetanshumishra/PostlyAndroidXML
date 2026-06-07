package com.preetanshu.postlyandroidxml.screens.userinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.preetanshu.postlyandroidxml.PostlyApplication
import com.preetanshu.postlyandroidxml.R
import com.preetanshu.postlyandroidxml.databinding.FragmentUserInfoBinding
import com.preetanshu.postlyandroidxml.services.ImageLoaderService
import com.preetanshu.postlyandroidxml.utilities.EmailValidator
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserInfoBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_USERNAME = "username"
        private const val ARG_AVATAR_URL = "avatar_url"
        private const val ARG_EMAIL = "email"

        fun newInstance(username: String, avatarUrl: String, email: String): UserInfoBottomSheet {
            return UserInfoBottomSheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                    putString(ARG_AVATAR_URL, avatarUrl)
                    putString(ARG_EMAIL, email)
                }
            }
        }
    }

    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageLoaderService: ImageLoaderService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageLoaderService = (requireActivity().application as PostlyApplication)
            .appComponent.imageLoaderService()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(ARG_USERNAME).orEmpty()
        val avatarUrl = arguments?.getString(ARG_AVATAR_URL).orEmpty()
        val email = arguments?.getString(ARG_EMAIL).orEmpty()

        binding.usernameText.text = username
        binding.emailText.text = email
        binding.warningIcon.visibility =
            if (EmailValidator.isValidDomain(email)) View.GONE else View.VISIBLE

        binding.avatarImage.setImageResource(R.drawable.ic_account_circle)

        if (avatarUrl.isNotEmpty()) {
            viewLifecycleOwner.lifecycleScope.launch {
                val bitmap = imageLoaderService.loadBitmap(avatarUrl)
                if (bitmap != null) {
                    binding.avatarImage.setImageBitmap(bitmap)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
