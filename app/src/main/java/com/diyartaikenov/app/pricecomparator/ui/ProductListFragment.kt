package com.diyartaikenov.app.pricecomparator.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
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
import com.diyartaikenov.app.pricecomparator.utils.PREF_SORT_ORDER_ORDINAL
import com.diyartaikenov.app.pricecomparator.utils.SortOrder
import com.diyartaikenov.app.pricecomparator.utils.getIntPreference
import com.diyartaikenov.app.pricecomparator.utils.saveIntPreference

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
    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var sortActionMenuItems: List<MenuItem>

    /** Alert dialog is showed on attempt of deleting selected products. */
    private lateinit var alertDialogBuilder: AlertDialog.Builder

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

        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products) {
                // Scroll to the top
                // fixme: scroll the list only when sorting occurs
                (bind.recyclerView.layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(0, 0)
            }
        }

        tracker = buildSelectionTracker()
        adapter.tracker = tracker
        tracker.addObserver(selectionObserver())

        alertDialogBuilder = buildAlertDialogBuilder()

        bind.fabAddProduct.setOnClickListener {
            actionMode?.finish()
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
            menu.findItem(R.id.sort_by_price),
        )
        val menuItemIndex = getIntPreference(requireActivity(), PREF_SORT_ORDER_ORDINAL)
        // Apply the stored sort order on app launch
        onOptionsItemSelected(sortActionMenuItems[menuItemIndex])
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_by_default -> {
                item.isChecked = true
                viewModel.updateProductsListWithParams(SortOrder.DEFAULT)
            }
            R.id.sort_by_protein_price -> {
                item.isChecked = true
                viewModel.updateProductsListWithParams(SortOrder.BY_PROTEIN_PRICE)
            }
            R.id.sort_by_protein_quantity -> {
                item.isChecked = true
                viewModel.updateProductsListWithParams(SortOrder.BY_PROTEIN_QUANTITY)
            }
            R.id.sort_by_price -> {
                item.isChecked = true
                viewModel.updateProductsListWithParams(SortOrder.BY_PRICE)
            }

            R.id.filter_by_food_group -> {
                // todo: show a window of food group checkables
            }
            R.id.filter_by_protein -> {
                item.isChecked = !item.isChecked
                // todo: update the list
            }

            R.id.add_random_products -> {
                viewModel.addRandomProducts(10)
            }
        }

        return super.onOptionsItemSelected(item)
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
                val deleteProductListener = DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> viewModel.deleteProduct(product)
                        DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
                    }
                }
                AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.question_delete_product, product.name))
                    .setPositiveButton(R.string.answer_ok, deleteProductListener)
                    .setNegativeButton(R.string.answer_cancel, deleteProductListener)
                    .show()
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

    private fun buildAlertDialogBuilder(): AlertDialog.Builder {

        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val selectedProducts = adapter.currentList.filter { product ->
                        tracker.isSelected(product.id)
                    }
                    viewModel.deleteProducts(selectedProducts)
                    actionMode?.finish()
                }
                DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.question_delete_selected_products))
            .setPositiveButton(getString(R.string.answer_ok), dialogClickListener)
            .setNegativeButton(getString(R.string.answer_cancel), dialogClickListener)
    }

    // region ActionMode callback

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        // Inflate a menu resource providing context menu items
        mode.menuInflater.inflate(R.menu.menu_action_mode, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = true

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_mode_select_all -> {
                adapter.currentList.forEach { product ->
                    tracker.select(product.id)
                }
            }
            R.id.action_mode_delete -> {
                alertDialogBuilder.show()
            }
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        actionMode = null
        tracker.clearSelection()
    }

    // endregion
}
