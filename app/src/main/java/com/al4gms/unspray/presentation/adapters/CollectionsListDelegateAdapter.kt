package com.al4gms.unspray.presentation.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.al4gms.unspray.R
import com.al4gms.unspray.data.modelsui.content.Content
import com.al4gms.unspray.databinding.ItemCollectionBinding
import com.al4gms.unspray.utils.BlurHashDecoder
import com.al4gms.unspray.utils.ENDING_OTHERS
import com.al4gms.unspray.utils.ENDING_WITH_1
import com.al4gms.unspray.utils.ENDING_WITH_2_3_4
import com.al4gms.unspray.utils.getEndingType
import com.al4gms.unspray.utils.inflate
import com.bumptech.glide.Glide
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class CollectionsListDelegateAdapter(
    private val onItemClick: (contentId: String, view: View) -> Unit,
) :
    AbsListItemAdapterDelegate<Content.Collection, Content, CollectionsListDelegateAdapter.ViewHolder>() {

    override fun isForViewType(item: Content, items: MutableList<Content>, position: Int): Boolean {
        return item is Content.Collection
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_collection), onItemClick)
    }

    override fun onBindViewHolder(
        item: Content.Collection,
        holder: ViewHolder,
        payloads: MutableList<Any>,
    ) {
        holder.bind(item)
    }

    class ViewHolder(
        view: View,
        private val onItemClick: (contentId: String, view: View) -> Unit,
    ) : RecyclerView.ViewHolder(view) {
        private val binding = ItemCollectionBinding.bind(view)
        private var collectionId: String? = null

        init {
            view.setOnClickListener { collectionId?.let { onItemClick(it, itemView) } }
        }

        fun bind(collection: Content.Collection) {
            itemView.transitionName = itemView.resources.getString(
                R.string.collection_item_transition_name,
                collection.id,
            )
            collectionId = collection.id
            binding.nameTextView.text = collection.user.name
            binding.usernameTextView.text =
                itemView.resources.getString(R.string.at_mail_with_text, collection.user.username)
            binding.titleTextView.text = collection.title
            binding.photoCountTextView.text = getCorrectEndingNPhotosRu(collection.totalPhotos)
            val blurHashAsDrawable = BlurHashDecoder.blurHashAsDrawable(
                itemView.resources,
                collection.coverPhoto.blurHash ?: "",
                300,
                195,
            )
            Glide.with(itemView)
                .load(collection.coverPhoto.urls.small)
                .placeholder(blurHashAsDrawable)
                .centerCrop()
                .into(binding.photoImageView)
            Glide.with(itemView)
                .load(collection.user.profileImage.small)
                .into(binding.avatarImageView)
        }

        private fun getCorrectEndingNPhotosRu(number: Int): String {
            return when (number.getEndingType()) {
                ENDING_WITH_1 -> itemView.resources.getString(
                    R.string.n_photos_ending_with_1,
                    number,
                )
                ENDING_WITH_2_3_4 -> itemView.resources.getString(
                    R.string.n_photos_ending_with_2_3_4,
                    number,
                )
                ENDING_OTHERS -> itemView.resources.getString(
                    R.string.n_photos_ending_with_others,
                    number,
                )
                else -> itemView.resources.getString(R.string.n_photos_ending_with_others, number)
            }
        }
    }
}
