package com.al4gms.unspray.presentation.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.al4gms.unspray.R
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.databinding.ItemPhotoBinding
import com.al4gms.unspray.utils.BlurHashDecoder
import com.al4gms.unspray.utils.inflate
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class PhotosListDelegateAdapter(
    private val onItemClick: (contentId: String, view: View) -> Unit,
) : AbsListItemAdapterDelegate<Content.Photo, Content, PhotosListDelegateAdapter.ViewHolder>() {

    override fun isForViewType(
        item: Content,
        items: MutableList<Content>,
        position: Int,
    ): Boolean {
        return item is Content.Photo
    }

    override fun onBindViewHolder(
        item: Content.Photo,
        holder: ViewHolder,
        payloads: MutableList<Any>,
    ) {
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_photo), onItemClick)
    }

    class ViewHolder(
        view: View,
        onItemClick: (contentId: String, view: View) -> Unit,
    ) : RecyclerView.ViewHolder(view) {
        private val binding = ItemPhotoBinding.bind(view)
        private var photoId: String? = null

        init {
            view.setOnClickListener { photoId?.let { onItemClick(it, itemView) } }
        }

        fun bind(photo: Content.Photo) {
            itemView.transitionName = itemView.resources.getString(R.string.photo_item_transition_name, photo.id)
            photoId = photo.id
            val blurHashAsDrawable = BlurHashDecoder.blurHashAsDrawable(
                itemView.resources,
                photo.blurHash ?: "",
                100,
                100,
            )
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
