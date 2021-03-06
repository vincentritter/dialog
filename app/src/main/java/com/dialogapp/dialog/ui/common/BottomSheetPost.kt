package com.dialogapp.dialog.ui.common

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentBottomSheetPostBinding
import com.dialogapp.dialog.ui.base.BaseFragment
import com.dialogapp.dialog.ui.util.autoCleared
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetPost : BottomSheetDialogFragment() {

    private var binding by autoCleared<FragmentBottomSheetPostBinding>()

    companion object {
        private const val ID = "id"
        private const val URL = "url"
        private const val USERNAME = "username"
        private const val DELETABLE = "deletable"

        fun newInstance(postId: String, postUrl: String, username: String?, deletable: Boolean)
                : BottomSheetPost {
            val bottomSheetPost = BottomSheetPost()
            bottomSheetPost.arguments = bundleOf(ID to postId, URL to postUrl,
                    USERNAME to username, DELETABLE to deletable)
            return bottomSheetPost
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBottomSheetPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!arguments?.getBoolean(DELETABLE)!!)
            binding.bottomNavViewPost.menu.removeItem(R.id.post_option_delete)

        binding.bottomNavViewPost.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.post_option_view_profile -> {
                    (parentFragment as BaseFragment).onProfileClicked(arguments?.getString(USERNAME)!!)
                    dismiss()
                }
                R.id.post_option_view_link -> {
                    val webpage = Uri.parse(arguments?.getString(URL))
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    val packMan = activity?.packageManager
                    if (packMan != null && intent.resolveActivity(packMan) != null) {
                        startActivity(intent)
                    }
                    dismiss()
                }
                R.id.post_option_delete -> {
                    MaterialDialog(this.requireContext())
                            .title(R.string.confirm)
                            .message(R.string.dialog_delete_post)
                            .show {
                                positiveButton(R.string.delete) {
                                    dismiss()
                                    deletePost()
                                    this@BottomSheetPost.dismiss()
                                }
                                negativeButton(android.R.string.cancel)
                            }
                }
            }
            true
        }
    }

    private fun deletePost() {
        val postId = arguments?.getString(ID)
        if (postId != null) {
            val requestViewModel = activity?.run {
                ViewModelProviders.of(this).get(RequestViewModel::class.java)
            }
            requestViewModel?.sendDeleteRequest(postId)
        }
    }
}