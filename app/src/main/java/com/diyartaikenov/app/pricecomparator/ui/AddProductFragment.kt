package com.diyartaikenov.app.pricecomparator.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

import com.diyartaikenov.app.pricecomparator.BaseApplication
import com.diyartaikenov.app.pricecomparator.R
import com.diyartaikenov.app.pricecomparator.databinding.FragmentAddProductBinding
import com.diyartaikenov.app.pricecomparator.model.FoodGroup
import com.diyartaikenov.app.pricecomparator.ui.viewmodel.ProductViewModel
import com.diyartaikenov.app.pricecomparator.ui.viewmodel.ProductViewModelFactory

class AddProductFragment: Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: ProductViewModel by activityViewModels {
        ProductViewModelFactory(
            (activity?.application as BaseApplication).database.getProductDao()
        )
    }

    private var _bind: FragmentAddProductBinding? = null
    private val bind get() = _bind!!

    private val name get() = bind.nameInputEditText.text.toString()
    private val weight get() = bind.weight.text.toString().toInt()
    private val price get() = bind.price.text.toString().toInt()
    private val proteinQuantity get() = bind.proteinQuantity.text.toString().toInt()
    private var foodGroup = FoodGroup.UNDEFINED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentAddProductBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.food_groups,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            bind.foodGroupSpinner.adapter = adapter
        }

        bind.foodGroupSpinner.onItemSelectedListener = this

        bind.apply {
            fabSaveProduct.setOnClickListener {
                onSaveProductFabClicked()
            }
        }
    }

    override fun onDestroyView() {
        _bind = null
        super.onDestroyView()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        foodGroup = when(position) {
            FoodGroup.ANIMAL_PRODUCTS.ordinal -> FoodGroup.ANIMAL_PRODUCTS
            FoodGroup.DIARY.ordinal -> FoodGroup.DIARY
            FoodGroup.FLAVOR_PRODUCTS.ordinal -> FoodGroup.FLAVOR_PRODUCTS
            FoodGroup.FRUIT_AND_VEGETABLES.ordinal -> FoodGroup.FRUIT_AND_VEGETABLES
            FoodGroup.GRAIN_PRODUCTS.ordinal -> FoodGroup.GRAIN_PRODUCTS
            else -> FoodGroup.UNDEFINED
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun onSaveProductFabClicked() {
        if (validateProductName()) {
            val totalProteinQuantity = ((weight / 100.0) * proteinQuantity).toInt()
            val relativePrice = (price / (weight / 100.0)).toInt()
            val proteinPrice = price / totalProteinQuantity.toDouble()

            viewModel.addProduct(
                name,
                weight,
                price,
                proteinQuantity,
                foodGroup,
                totalProteinQuantity,
                relativePrice,
                proteinPrice
            )
            findNavController().navigate(
                AddProductFragmentDirections.actionNavAddProductToNavProducts()
            )
        }
    }

    private fun validateProductName(): Boolean {
        return if (name.isBlank()) {
            bind.nameInputLayout.error = getString(R.string.name_input_layout_error)
            false
        } else {
            bind.nameInputLayout.error = null
            true
        }
    }
}
