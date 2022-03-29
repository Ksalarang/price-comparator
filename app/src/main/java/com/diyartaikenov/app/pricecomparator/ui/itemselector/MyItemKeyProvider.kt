package com.diyartaikenov.app.pricecomparator.ui.itemselector

import androidx.recyclerview.selection.ItemKeyProvider

import com.diyartaikenov.app.pricecomparator.ui.adapter.ProductListAdapter

class MyItemKeyProvider(
    private val adapter: ProductListAdapter
): ItemKeyProvider<Long>(SCOPE_CACHED) {

    override fun getKey(position: Int): Long =
        adapter.currentList[position].id

    override fun getPosition(key: Long): Int =
        adapter.currentList.indexOfFirst { it.id == key }
}
