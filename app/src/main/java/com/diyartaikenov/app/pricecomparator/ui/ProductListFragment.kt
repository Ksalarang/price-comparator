package com.diyartaikenov.app.pricecomparator.ui

import android.os.Bundle
import android.view.*
import androidx.constraintlayout.widget.Group
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.diyartaikenov.app.pricecomparator.BaseApplication
import com.diyartaikenov.app.pricecomparator.MainActivity
import com.diyartaikenov.app.pricecomparator.R
import com.diyartaikenov.app.pricecomparator.databinding.FragmentProductListBinding
import com.diyartaikenov.app.pricecomparator.ui.adapter.ProductListAdapter
import com.diyartaikenov.app.pricecomparator.ui.viewmodel.ProductViewModel
import com.diyartaikenov.app.pricecomparator.ui.viewmodel.ProductViewModelFactory
import com.diyartaikenov.app.pricecomparator.utils.SortOrder

class ProductListFragment: Fragment() {

    private val viewModel: ProductViewModel by activityViewModels {
        ProductViewModelFactory(
            (activity?.application as BaseApplication).database.getProductDao()
        )
    }

    private var _bind: FragmentProductListBinding? = null
    private val bind get() = _bind!!

    private lateinit var adapter: ProductListAdapter

    // Options menu sort action items
    private lateinit var sortByDefaultMenuItem: MenuItem
    private lateinit var sortByProteinPriceMenuItem: MenuItem
    private lateinit var sortByProteinQuantityMenuItem: MenuItem

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

        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }
        bind.recyclerView.adapter = adapter

        bind.apply {
            fabAddProduct.setOnClickListener {
                findNavController().navigate(
                    ProductListFragmentDirections.actionNavProductsToNavAddProduct()
                )
            }
        }
    }

    override fun onDestroyView() {
        _bind = null
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_buttons_menu, menu)

        sortByDefaultMenuItem = menu.findItem(R.id.sort_by_default)
        sortByProteinPriceMenuItem = menu.findItem(R.id.sort_by_protein_price)
        sortByProteinQuantityMenuItem = menu.findItem(R.id.sort_by_protein_quantity)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_by_default -> {
                sortByDefaultMenuItem.isChecked = true
                viewModel.sortInOrder(SortOrder.DEFAULT)
            }
            R.id.sort_by_protein_price -> {
                sortByProteinPriceMenuItem.isChecked = true
                viewModel.sortInOrder(SortOrder.BY_PROTEIN_PRICE)
            }
            R.id.sort_by_protein_quantity -> {
                sortByProteinQuantityMenuItem.isChecked = true
                viewModel.sortInOrder(SortOrder.BY_PROTEIN_QUANTITY)
            }
        }

        //
        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }
        bind.recyclerView.adapter = adapter

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
                viewModel.deleteProduct(product)
            }
        )
    }
}
