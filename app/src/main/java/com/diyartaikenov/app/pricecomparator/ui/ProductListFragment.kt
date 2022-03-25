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
import com.diyartaikenov.app.pricecomparator.utils.SortOrder
import com.diyartaikenov.app.pricecomparator.utils.log

class ProductListFragment: Fragment() {

    private val viewModel: ProductViewModel by activityViewModels {
        ProductViewModelFactory(
            (activity?.application as BaseApplication).database.getProductDao()
        )
    }

    private var _bind: FragmentProductListBinding? = null
    private val bind get() = _bind!!

    private lateinit var adapter: ProductListAdapter

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
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_by_default -> {
                viewModel.sortInOrder(SortOrder.DEFAULT)
                viewModel.products.observe(viewLifecycleOwner) { products ->
                    adapter.submitList(products)
                }
                bind.recyclerView.adapter = adapter
            }
            R.id.sort_by_protein_price -> {
                viewModel.sortInOrder(SortOrder.BY_PROTEIN_PRICE)
                viewModel.products.observe(viewLifecycleOwner) { products ->
                    adapter.submitList(products)
                }
                bind.recyclerView.adapter = adapter
            }
            R.id.sort_by_protein_quantity -> {
                viewModel.sortInOrder(SortOrder.BY_PROTEIN_QUANTITY)
                viewModel.products.observe(viewLifecycleOwner) { products ->
                    adapter.submitList(products)
                }
                bind.recyclerView.adapter = adapter
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
                viewModel.deleteProduct(product)
            }
        )
    }
}
