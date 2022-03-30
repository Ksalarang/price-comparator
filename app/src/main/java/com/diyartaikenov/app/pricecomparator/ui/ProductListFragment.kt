package com.diyartaikenov.app.pricecomparator.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.diyartaikenov.app.pricecomparator.BaseApplication
import com.diyartaikenov.app.pricecomparator.MainActivity
import com.diyartaikenov.app.pricecomparator.R
import com.diyartaikenov.app.pricecomparator.databinding.FragmentProductListBinding
import com.diyartaikenov.app.pricecomparator.ui.adapter.MyItemDetailsLookup
import com.diyartaikenov.app.pricecomparator.ui.adapter.MyItemKeyProvider
import com.diyartaikenov.app.pricecomparator.ui.adapter.ProductListAdapter
import com.diyartaikenov.app.pricecomparator.ui.viewmodel.ProductViewModel
import com.diyartaikenov.app.pricecomparator.ui.viewmodel.ProductViewModelFactory
import com.diyartaikenov.app.pricecomparator.utils.*
import kotlinx.coroutines.coroutineScope

class ProductListFragment: Fragment(), ActionMode.Callback {

    private val viewModel: ProductViewModel by activityViewModels {
        ProductViewModelFactory(
            (activity?.application as BaseApplication).database.getProductDao()
        )
    }

    private var _bind: FragmentProductListBinding? = null
    private val bind get() = _bind!!

    private var actionMode: ActionMode? = null

    private lateinit var adapter: ProductListAdapter
    private lateinit var sortActionMenuItems: List<MenuItem>
    private lateinit var tracker: SelectionTracker<Long>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentProductListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = createAdapter()
        adapter.setHasStableIds(true)
        bind.recyclerView.adapter = adapter

        tracker = buildSelectionTracker()
        adapter.tracker = tracker

        tracker.addObserver(selectionObserver())

        bind.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_nav_products_to_nav_add_product)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        tracker.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        tracker.onRestoreInstanceState(savedInstanceState)
        if (tracker.hasSelection()) {
            actionMode = (activity as MainActivity).startSupportActionMode(this)
            actionMode?.title = getString(R.string.action_mode_title, tracker.selection.size())
        }
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onDestroyView() {
        saveIntPreference(
            requireActivity(),
            PREF_SORT_ORDER_ORDINAL,
            viewModel.sortOrder.ordinal
        )
        _bind = null
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_buttons_menu, menu)

        // The ordinal of the SortOrders matches the index of the sortActionMenuItems items.
        // Storing preferences this way works as long as menuItems stored in sortActionMenuItems
        // arranged in the same order as the instances of the SortOrder enum.
        sortActionMenuItems = listOf(
            menu.findItem(R.id.sort_by_default),
            menu.findItem(R.id.sort_by_protein_price),
            menu.findItem(R.id.sort_by_protein_quantity),
            menu.findItem(R.id.sort_by_price)
        )
        val menuItemIndex = getIntPreference(requireActivity(), PREF_SORT_ORDER_ORDINAL)
        // Apply the stored sort order on app launch
        onOptionsItemSelected(sortActionMenuItems[menuItemIndex])
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_by -> {
                return true
            }
            R.id.sort_by_default -> {
                sortActionMenuItems[0].isChecked = true
                viewModel.sortInOrder(SortOrder.DEFAULT)
            }
            R.id.sort_by_protein_price -> {
                sortActionMenuItems[1].isChecked = true
                viewModel.sortInOrder(SortOrder.BY_PROTEIN_PRICE)
            }
            R.id.sort_by_protein_quantity -> {
                sortActionMenuItems[2].isChecked = true
                viewModel.sortInOrder(SortOrder.BY_PROTEIN_QUANTITY)
            }
            R.id.sort_by_price -> {
                sortActionMenuItems[3].isChecked = true
                viewModel.sortInOrder(SortOrder.BY_PRICE)
            }
            R.id.menu_action_add_random_products -> {
                viewModel.addRandomProducts(10)
                return true
            }
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products) {
                // Scroll to the top
                // fixme: scroll the list only when sorting occurs
                (bind.recyclerView.layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(0, 0)
            }
        }

        return true
    }

    private fun createAdapter(): ProductListAdapter {
        return ProductListAdapter(
            requireContext(),
            { product -> // Edit the product
                findNavController().navigate(
                    ProductListFragmentDirections.actionNavProductsToNavAddProduct(product.id)
                )
            },
            { product -> // Remove the product
                viewModel.deleteProduct(product)
            }
        )
    }

    private fun buildSelectionTracker(): SelectionTracker<Long> {
        return SelectionTracker.Builder(
            "mySelection",
            bind.recyclerView,
            MyItemKeyProvider(adapter),
            MyItemDetailsLookup(bind.recyclerView),
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()
    }

    private fun selectionObserver() = object : SelectionTracker.SelectionObserver<Long>() {
        override fun onSelectionChanged() {
            super.onSelectionChanged()

            if (actionMode == null) {
                actionMode = (activity as AppCompatActivity)
                    .startSupportActionMode(this@ProductListFragment)
            }

            val itemNumber = tracker.selection.size()
            if (itemNumber > 0) {
                actionMode?.title = getString(R.string.action_mode_title, itemNumber)
            } else {
                actionMode?.finish()
            }
        }
    }

    // region ActionMode callback

    // Called after startActionMode
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        // Inflate a menu resource providing context menu items
        mode.menuInflater.inflate(R.menu.menu_action_mode, menu)
        return true
    }

    // Called each time the action mode is shown
    override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = true

    // Called when the user selects a contextual menu item
    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_mode_select_all -> {
                adapter.currentList.forEach { product ->
                    tracker.select(product.id)
                }
            }
            R.id.action_mode_delete -> {
                val selectedProducts = adapter.currentList.filter { product ->
                    tracker.isSelected(product.id)
                }
                viewModel.deleteProducts(selectedProducts)
                actionMode?.finish()
            }
        }
        return true
    }

    // Called when the action mode is finished
    override fun onDestroyActionMode(mode: ActionMode) {
        actionMode = null
        tracker.clearSelection()
    }

    // endregion
}
