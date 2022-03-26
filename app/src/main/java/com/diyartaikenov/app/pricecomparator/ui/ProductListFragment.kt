package com.diyartaikenov.app.pricecomparator.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.diyartaikenov.app.pricecomparator.BaseApplication
import com.diyartaikenov.app.pricecomparator.R
import com.diyartaikenov.app.pricecomparator.databinding.FragmentProductListBinding
import com.diyartaikenov.app.pricecomparator.ui.adapter.ProductListAdapter
import com.diyartaikenov.app.pricecomparator.ui.viewmodel.ProductViewModel
import com.diyartaikenov.app.pricecomparator.ui.viewmodel.ProductViewModelFactory
import com.diyartaikenov.app.pricecomparator.utils.PREF_SORT_ORDER_ORDINAL
import com.diyartaikenov.app.pricecomparator.utils.SortOrder
import com.diyartaikenov.app.pricecomparator.utils.getIntPreference
import com.diyartaikenov.app.pricecomparator.utils.saveIntPreference

class ProductListFragment: Fragment() {

    private val viewModel: ProductViewModel by activityViewModels {
        ProductViewModelFactory(
            (activity?.application as BaseApplication).database.getProductDao()
        )
    }

    private var _bind: FragmentProductListBinding? = null
    private val bind get() = _bind!!

    private lateinit var adapter: ProductListAdapter
    private lateinit var sortActionMenuItems: List<MenuItem>

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

        bind.apply {
            fabAddProduct.setOnClickListener {
                findNavController().navigate(
                    ProductListFragmentDirections.actionNavProductsToNavAddProduct()
                )
            }
        }
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

        sortActionMenuItems = listOf(
            menu.findItem(R.id.sort_by_default),
            menu.findItem(R.id.sort_by_protein_price),
            menu.findItem(R.id.sort_by_protein_quantity)
        )
        val menuItemIndex = getIntPreference(requireActivity(), PREF_SORT_ORDER_ORDINAL)
        onOptionsItemSelected(sortActionMenuItems[menuItemIndex])
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
        }

        // Observe changes and apply them to the recyclerView adapter
        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }
        bind.recyclerView.adapter = adapter

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
}
