package com.danielarog.myfirstapp.fragments.main

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.danielarog.myfirstapp.databinding.AddItemDialogBinding
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.models.Category
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.viewmodels.AddItemViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddItemFragment : BaseFragment() {


    var _binding: AddItemDialogBinding? = null

    var product = ShoppingItem()
    var imageUri: Uri? = null
    lateinit var viewModel: AddItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddItemDialogBinding.inflate(inflater)
        viewModel = ViewModelProvider(this)[AddItemViewModel::class.java]
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createSpinnerCategory()
        createSpinnerSubcategory()
        val binding = _binding!!
        val submitBtn = binding.dialogItemSubmit

        submitBtn.setOnClickListener {

            val list = mutableListOf<EditText>()
            list.add(binding.dialogItemName)
            list.add(binding.dialogItemDesc)
            list.add(binding.dialogItemPrice)

            if (imageUri == null) {
                toast("Please select an image")
            } else if (!isValidFields(list)) {
                toast("Please fill all the fields")
            } else {
                product.itemName = binding.dialogItemName.text.toString()
                product.price = binding.dialogItemPrice.text.toString()
                product.description = binding.dialogItemDesc.text.toString()
                val formatter = DateTimeFormatter.ISO_DATE
                val date = LocalDate.now().format(formatter)
                product.date = date
                lifecycleScope.launch {
                    viewModel.addItem(product)
                    toast("Successfully added item")
                    findNavController(this@AddItemFragment).popBackStack()
                }
            }
        }
    }

    fun createSpinnerCategory() {
        val binding = _binding!!
        val spinner = binding.dialogItemCategory
        val categoryList = Category.categoryList().map { category -> category.category.value }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryList)
        spinner.adapter = adapter

        spinner.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, index, _ ->
                val category = categoryList[index]
                product.category = category
            }
    }

    fun createSpinnerSubcategory() {
        val binding = _binding!!
        val spinner = binding.dialogItemSubCategory
        val subCategoryList = Category.subCategoryList()
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subCategoryList)
        spinner.adapter = adapter

        spinner.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, index, _ ->
                val subCategory = subCategoryList[index]
                product.subCategory = subCategory.value
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}