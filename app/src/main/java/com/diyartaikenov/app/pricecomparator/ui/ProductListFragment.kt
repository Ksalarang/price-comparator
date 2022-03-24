package com.diyartaikenov.app.pricecomparator.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.diyartaikenov.app.pricecomparator.BaseApplication
import com.diyartaikenov.app.pricecomparator.R
import com.diyartaikenov.app.pricecomparator.databinding.FragmentProductListBinding
import com.diyartaikenov.app.pricecomparator.model.Product
import com.diyartaikenov.app.pricecomparator.ui.adapter.ProductListAdapter
import com.diyartaikenov.app.pricecomparator.ui.viewmodel.ProductViewModel
import com.diyartaikenov.app.pricecomparator.ui.viewmodel.ProductViewModelFactory

class ProductListFragment: Fragment() {

    private val viewModel: ProductViewModel by activityViewModels {
        ProductViewModelFactory(
            (activity?.application as BaseApplication).database.getProductDao()
        )
    }

    private var _bind: FragmentProductListBinding? = null
    private val bind get() = _bind!!

    private lateinit var products: List<Product>

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

        val adapter = ProductListAdapter(
            requireContext(),
            { product ->
                findNavController().navigate(
                    ProductListFragmentDirections.actionNavProductsToNavAddProduct(product.id)
                )
            },
            { product ->
                viewModel.deleteProduct(product)
            }
        )

        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            this.products = products
        }

        bind.apply {
            recyclerView.adapter = adapter

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
            R.id.sort_by_protein_price -> {
                bind.recyclerView.adapter
            }
            R.id.sort_by_protein_quantity -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun List<Product>.sortByProteinPrice(): List<Product> {
        val mutableList = this.toMutableList()
        mutableList.sortWith(compareBy { it.proteinPrice })

        return mutableList.toList()
    }
}
