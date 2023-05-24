package com.al4gms.unspray.presentation.decor

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.al4gms.unspray.utils.fromDpToPx
import javax.inject.Inject

class ItemOffsetDecoration @Inject constructor(
    private val context: Context,
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        val preOffset = 5
        val offset = preOffset.fromDpToPx(context)
        with(outRect) {
            top = offset
            bottom = offset
            left = offset
            right = offset
        }
    }
}
