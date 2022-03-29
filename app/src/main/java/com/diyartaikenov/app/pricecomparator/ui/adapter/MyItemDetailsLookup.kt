package com.diyartaikenov.app.pricecomparator.ui.adapter

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

import com.diyartaikenov.app.pricecomparator.ui.adapter.ProductListAdapter

class MyItemDetailsLookup(private val recyclerView: RecyclerView)
    : ItemDetailsLookup<Long>() {

    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)

        return if (view != null) {
            (recyclerView.getChildViewHolder(view) as ProductListAdapter.ProductViewHolder)
                .getItemDetails()
        } else null
    }
}
