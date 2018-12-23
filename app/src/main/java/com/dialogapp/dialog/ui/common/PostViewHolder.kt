package com.dialogapp.dialog.ui.common

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dialogapp.dialog.GlideRequests
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.PostItemBinding
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.ui.util.GlideImageGetter
import com.dialogapp.dialog.ui.util.HtmlTextHelper

class PostViewHolder(private val view: View, val binding: PostItemBinding, private val glide: GlideRequests,
                     private val postClickedListener: PostClickedListener)
    : RecyclerView.ViewHolder(view) {

    fun bind(post: Post?) {
        binding.textFullname.text = post?.author?.name
        binding.textUsername.text = String.format(view.resources.getString(R.string.post_username),
                post?.author?.microblog?.username)
        binding.textTime.text = String.format(view.resources.getString(R.string.post_time),
                post?.microblog?.dateRelative)
        HtmlTextHelper(glide, postClickedListener, post?.contentHtml).setHtmlContent(binding.textContent)
        glide.load(post?.author?.avatar).into(binding.imageThumbnail)

        binding.buttonConv.visibility = when (post?.microblog?.isConversation) {
            true -> {
                binding.buttonConv.setOnClickListener {
                    postClickedListener.onConversationButtonClicked(post.id)
                }
                View.VISIBLE
            }
            else -> {
                View.INVISIBLE
            }
        }

        when (post?.microblog?.isFavorite) {
            true -> binding.buttonFav.setImageResource(R.drawable.ic_round_favorite_24px)
            else -> binding.buttonFav.setImageResource(R.drawable.ic_round_favorite_border_24px)
        }
        binding.buttonFav.setOnClickListener {
            postClickedListener.onFavoriteButtonClicked(post?.id, post?.belongsToEndpoint)
        }

        binding.imageThumbnail.setOnClickListener {
            postClickedListener.onProfileClicked(post?.author?.microblog?.username!!)
        }

        binding.buttonOverflow.setOnClickListener {
            if (post != null) {
                postClickedListener.onOverflowMenuClicked(post.id, post.url, post.microblog.isDeletable)
            }
        }
    }

    fun updatePost(payloads: MutableList<Any>) {
        val diffBundle = payloads[0] as Bundle

        for (key in diffBundle.keySet()) {
            when (key) {
                PK_CONVERSATION -> {
                    binding.buttonConv.visibility = if (diffBundle.getBoolean(PK_CONVERSATION))
                        View.VISIBLE
                    else
                        View.GONE
                }
                PK_FAVORITE -> {
                    when (diffBundle.getBoolean(PK_FAVORITE)) {
                        true -> binding.buttonFav.setImageResource(R.drawable.ic_round_favorite_24px)
                        else -> binding.buttonFav.setImageResource(R.drawable.ic_round_favorite_border_24px)
                    }
                }
                PK_DATE -> {
                    binding.textTime.text = diffBundle.getString(PK_DATE)
                }
                PK_AVATAR -> {
                    glide.load(diffBundle.getString(PK_AVATAR))
                            .into(binding.imageThumbnail)
                }
                PK_USERNAME -> {
                    binding.textUsername.text = diffBundle.getString(PK_USERNAME)
                }
                PK_CONTENT -> {
                    HtmlTextHelper(glide, postClickedListener, diffBundle.getString(PK_CONTENT))
                            .setHtmlContent(binding.textContent)
                }
            }
        }
    }

    fun recycle() {
        binding.textFullname.text = null
        binding.textUsername.text = null
        binding.textTime.text = null
        binding.textContent.text = null

        glide.clear(binding.imageThumbnail)
        GlideImageGetter.clear(binding.textContent)
    }
}