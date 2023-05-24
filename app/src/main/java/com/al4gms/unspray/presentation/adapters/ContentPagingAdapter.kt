package com.al4gms.unspray.presentation.adapters

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.al4gms.unspray.R
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.databinding.ItemPhotoBinding
import com.al4gms.unspray.utils.BlurHashDecoder
import com.al4gms.unspray.utils.inflate
import com.bumptech.glide.Glide

class ContentPagingAdapter(
    private val onItemClick: (contentId: String, view: View) -> Unit,
) : PagingDataAdapter<Content.Photo, ContentPagingAdapter.ViewHolder>(ContentDiffUtilCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val content = getItem(position)
        if (content is Content.Photo) holder.bind(content)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_photo), onItemClick)
    }

    class ContentDiffUtilCallback : DiffUtil.ItemCallback<Content.Photo>() {
        override fun areItemsTheSame(oldItem: Content.Photo, newItem: Content.Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Content.Photo, newItem: Content.Photo): Boolean {
            return oldItem == newItem
        }
    }

    class ViewHolder(
        view: View,
        onItemClick: (contentId: String, view: View) -> Unit,
    ) : RecyclerView.ViewHolder(view) {
        private var photoId: String? = null
        private val binding = ItemPhotoBinding.bind(view)

        init {
            view.setOnClickListener { photoId?.let { onItemClick(it, itemView) } }
        }

        fun bind(photo: Content.Photo) {
            itemView.transitionName = itemView.resources.getString(R.string.photo_item_transition_name, photo.id)
            photoId = photo.id
            val blurHashAsDrawable =
                BlurHashDecoder.blurHashAsDrawable(itemView.resources, photo.blurHash ?: "", 100, 100)
            Glide.with(itemView)
                .load(photo.urls.small)
                .placeholder(blurHashAsDrawable)
                .into(binding.photoImageView)

            Glide.with(itemView)
                .load(photo.user.profileImage.small)
                .into(binding.avatarImageView)
            binding.likeImageView.setImageResource(
                if (photo.likedByUser) {
                    R.drawable.ic_liked
                } else {
                    R.drawable.ic_like_empty
                },
            )
            binding.nameTextView.text = photo.user.name
            val atUsername = itemView.resources.getString(R.string.at_mail_with_text, photo.user.username)
            binding.usernameTextView.text = atUsername
            binding.likeCountTextView.text = photo.likes.toString()
        }
    }
}
