package com.diyartaikenov.app.pricecomparator.ui.adapter

import androidx.recyclerview.selection.ItemKeyProvider

class MyItemKeyProvider(
    private val adapter: ProductListAdapter
): ItemKeyProvider<Long>(SCOPE_CACHED) {

    override fun getKey(position: Int): Long =
        adapter.currentList[position].id

    override fun getPosition(key: Long): Int =
        adapter.currentList.indexOfFirst { it.id == key }
}
