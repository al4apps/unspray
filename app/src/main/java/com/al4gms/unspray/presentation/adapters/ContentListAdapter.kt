package com.al4gms.unspray.presentation.adapters

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.al4gms.unspray.data.modelsui.content.Content
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

class ContentListAdapter(
    onItemClick: (contentId: String, view: View) -> Unit,
) : AsyncListDifferDelegationAdapter<Content>(ContentDiffUtilCallback()) {

    init {
        delegatesManager.addDelegate(PhotosListDelegateAdapter(onItemClick))
            .addDelegate(CollectionsListDelegateAdapter(onItemClick))
    }

    class ContentDiffUtilCallback : DiffUtil.ItemCallback<Content>() {
        override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean {
            return when {
                oldItem is Content.Collection && newItem is Content.Collection -> oldItem.id == newItem.id
                oldItem is Content.Photo && newItem is Content.Photo -> oldItem.id == newItem.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean {
            return oldItem == newItem
        }
    }
}
