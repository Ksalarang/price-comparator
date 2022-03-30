package com.diyartaikenov.app.pricecomparator.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.diyartaikenov.app.pricecomparator.R

import com.diyartaikenov.app.pricecomparator.databinding.ProductItemLayoutBinding
import com.diyartaikenov.app.pricecomparator.model.Product

class ProductListAdapter(
    private val context: Context,
    private val editItemClickListener: (Product) -> Unit,
    private val deleteItemClickListener: (Product) -> Unit
): ListAdapter<Product, ProductListAdapter.ProductViewHolder>(DiffCallback) {

    var tracker: SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return ProductViewHolder(
            ProductItemLayoutBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        val isSelected = tracker?.isSelected(product.id) ?: false

        holder.bind(product, isSelected) { view ->
            PopupMenu(context, view).apply {
                inflate(R.menu.product_view_menu)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.menu_item_edit -> {
                            editItemClickListener(product)
                            true
                        }
                        R.id.menu_item_delete -> {
                            deleteItemClickListener(product)
                            true
                        }
                        else -> { false }
                    }
                }
                show()
            }
        }
    }

    override fun getItemId(position: Int) = getItem(position).id

    inner class ProductViewHolder(private val binding: ProductItemLayoutBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product,
                 isActivated: Boolean,
                 menuButtonClickListener: View.OnClickListener) {

            binding.apply {
                this.product = product
                buttonOptions.setOnClickListener(menuButtonClickListener)

                if (isActivated) {
                    buttonOptions.setImageResource(R.drawable.ic_checked_circle)
                } else {
                    buttonOptions.setImageResource(R.drawable.ic_options)
                }

                if (product.proteinQuantity > 0) {
                    setProteinViewsVisibility(View.VISIBLE)
                } else {
                    setProteinViewsVisibility(View.INVISIBLE)
                }

                executePendingBindings()
            }
        }

        fun getItemDetails() = object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int = bindingAdapterPosition
            override fun getSelectionKey(): Long = getItem(bindingAdapterPosition).id
        }

        private fun setProteinViewsVisibility(visibility: Int) {
            binding.apply {
                proteinQuantityPerProduct.visibility = visibility
                proteinQuantityPer100g.visibility = visibility
                labelProteinPricePerGram.visibility = visibility
                proteinPricePerGram.visibility = visibility
            }
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
