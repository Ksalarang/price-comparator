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
import androidx.navigation.fragment.navArgs

import com.diyartaikenov.app.pricecomparator.BaseApplication
import com.diyartaikenov.app.pricecomparator.R
import com.diyartaikenov.app.pricecomparator.databinding.FragmentAddProductBinding
import com.diyartaikenov.app.pricecomparator.model.FoodGroup
import com.diyartaikenov.app.pricecomparator.model.Product
import com.diyartaikenov.app.pricecomparator.ui.viewmodel.ProductViewModel
import com.diyartaikenov.app.pricecomparator.ui.viewmodel.ProductViewModelFactory
import com.diyartaikenov.app.pricecomparator.utils.getIntValue
import com.diyartaikenov.app.pricecomparator.utils.hideSoftInput
import com.diyartaikenov.app.pricecomparator.utils.showSoftInput
import kotlin.math.roundToInt

class AddProductFragment: Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel: ProductViewModel by activityViewModels {
        ProductViewModelFactory(
            (activity?.application as BaseApplication).database.getProductDao()
        )
    }

    private val navArgs: AddProductFragmentArgs by navArgs()

    private var _bind: FragmentAddProductBinding? = null
    private val bind get() = _bind!!

    private val name get() = bind.nameInputEditText.text.toString()
    private val weight get() = bind.weightInputEditText.getIntValue()
    private val price get() = bind.priceInputEditText.getIntValue()
    private val proteinQuantity get() = bind.proteinQuantityInputEditText.getIntValue()
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

        if (navArgs.id > 0) { // Update the existing product
            viewModel.getProductById(navArgs.id).observe(viewLifecycleOwner) { product ->
                bindProduct(product)
            }

            bind.fabSaveProduct.setOnClickListener {
                if (validateFields()) {
                    viewModel.updateProduct(buildProductInstance())
                    findNavController().navigate(R.id.action_nav_add_product_to_nav_products)
                }
            }
        } else { // Add a new product
            bind.nameInputEditText.requestFocus()
            activity?.showSoftInput(bind.nameInputEditText)

            bind.fabSaveProduct.setOnClickListener {
                if (validateFields()) {
                    viewModel.addProduct(buildProductInstance())
                    activity?.hideSoftInput(bind.nameInputEditText)
                    findNavController().navigate(R.id.action_nav_add_product_to_nav_products)
                }
            }
        }

        bind.apply {
            foodGroupSpinner.adapter = createAdapter()
            foodGroupSpinner.onItemSelectedListener = this@AddProductFragment
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

    private fun createAdapter(): ArrayAdapter<CharSequence> {
        return ArrayAdapter.createFromResource(
            requireContext(),
            R.array.food_groups,
            android.R.layout.simple_spinner_item
        ).run {
            // Specify the layout to use when the list of choices appears
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            this
        }
    }

    private fun bindProduct(product: Product) {
        bind.apply {
            nameInputEditText.setText(product.name)
            weightInputEditText.setText(product.weight.toString())
            priceInputEditText.setText(product.price.toString())
            proteinQuantityInputEditText.setText(product.proteinQuantity.toString())
            foodGroupSpinner.setSelection(product.foodGroup.ordinal)
        }
    }

    /**
     * Create a [Product] instance from the input fields.
     * Only call after validateFields() returns true.
     */
    private fun buildProductInstance(): Product {
        val totalProteinQuantity = (proteinQuantity * (weight / 100.0)).roundToInt()
        val relativePrice = (price / (weight / 100.0)).roundToInt()

        val proteinPrice = if (totalProteinQuantity == 0) {
            0.0
        } else {
            price / totalProteinQuantity.toDouble()
        }

        return Product(
            id = navArgs.id,
            name = name,
            weight = weight,
            price = price,
            proteinQuantity = proteinQuantity,
            foodGroup = foodGroup,
            totalProteinQuantity = totalProteinQuantity,
            relativePrice = relativePrice,
            proteinPrice = proteinPrice
        )
    }

    private fun validateFields(): Boolean {
        val isNameValid: Boolean
        val isWeightValid: Boolean
        val isPriceValid: Boolean

        if (name.isNotBlank()) {
            bind.nameInputLayout.error = null
            isNameValid = true
        } else {
            bind.nameInputLayout.error = getString(R.string.name_input_layout_error)
            isNameValid = false
        }

        if (weight > 9) {
            bind.weightInputLayout.error = null
            isWeightValid = true
        } else {
            bind.weightInputLayout.error = getString(R.string.weight_input_layout_error)
            isWeightValid = false
        }

        if (price > 9) {
            bind.priceInputLayout.error = null
            isPriceValid = true
        } else {
            bind.priceInputLayout.error = getString(R.string.price_input_layout_error)
            isPriceValid = false
        }

        return isNameValid && isWeightValid && isPriceValid
    }
}
