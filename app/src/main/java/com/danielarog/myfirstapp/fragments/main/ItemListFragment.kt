package com.danielarog.myfirstapp.fragments.main

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.adapters.ProductFilterParamsAdapter
import com.danielarog.myfirstapp.adapters.ShoppingListRvAdapter
import com.danielarog.myfirstapp.databinding.FragmentItemListBinding
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.models.ProductCategory
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.viewmodels.ShoppingListViewModel
import com.google.api.Distribution.BucketOptions.Linear
import com.google.gson.Gson

class ItemListFragment : BaseFragment() {

    private var _binding: FragmentItemListBinding? = null
    val binding get() = _binding!!
    private lateinit var viewModel: ShoppingListViewModel

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListBinding.inflate(inflater)
        viewModel = ViewModelProvider(this)[ShoppingListViewModel::class.java]
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val layoutManager = binding.itemListRv.layoutManager as LinearLayoutManager
        val lastRecyclerViewPosition = layoutManager.findFirstVisibleItemPosition()
        outState.putInt("scroll_pos", lastRecyclerViewPosition)
        val list = viewModel.shoppingItemsLiveData.value
        list?.let {
            outState.putParcelableArrayList("shopping_list", ArrayList(it))
        }
        super.onSaveInstanceState(outState)
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productRecyclerView = binding.itemListRv
        productRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)


        if (savedInstanceState != null) {
            val scrollPos = savedInstanceState.getInt("scroll_pos")
            val dataList =
                savedInstanceState.getParcelableArrayList("shopping_list", ShoppingItem::class.java)
            productRecyclerView.adapter =
                ShoppingListRvAdapter(dataList!!.toMutableList()) { shoppingItem ->
                    val shoppingItemAsJson = Gson().toJson(shoppingItem)
                    Navigation.findNavController(binding.root)
                        .navigate(
                            R.id.action_itemListFragment_to_itemDetailsFragment,
                            bundleOf(
                                Pair(ITEM_DETAILS_ARG, shoppingItemAsJson)
                            )
                        )
                }
            binding.itemListRv.scrollToPosition(scrollPos)
        } else {
            viewModel.shoppingItemsLiveData.observe(viewLifecycleOwner) {
                productRecyclerView.adapter =
                    ShoppingListRvAdapter(it.toMutableList()) { shoppingItem ->
                        val shoppingItemAsJson = Gson().toJson(shoppingItem)
                        Navigation.findNavController(binding.root)
                            .navigate(
                                R.id.action_itemListFragment_to_itemDetailsFragment,
                                bundleOf(
                                    Pair(ITEM_DETAILS_ARG, shoppingItemAsJson)
                                )
                            )
                    }
            }
            val category = arguments?.getString("category") ?: "PANTS"
            val gender = arguments?.getString("gender")?.lowercase() ?: "female"


            setScreenName(category)

            val categoryEnum = ProductCategory.valueOf(category)
            val spinner = binding.spinnerSubCategories
            val subCategories = categoryEnum.getSubCategories().toMutableList()
            subCategories.add(0, ProductCategory.SubCategory.ALL)
            spinner.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subCategories)

            changeCategory(
                categoryEnum,
                gender
            )

            val categoryTv = binding.categoryNameTvLisItems
            val subCategoryTv = binding.subCategoryNameTvLisItems



            categoryTv.text = category
            var firstCategoryLoadingState = true
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                    if (firstCategoryLoadingState) {
                        firstCategoryLoadingState = false
                        return
                    }
                    changeCategory(
                        categoryEnum,
                        gender,
                        subCategories[index]
                    )
                    subCategoryTv.text = subCategories[index].value
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }

            val colorList = listOf(
                "Select Color",
                "RED",
                "GREEN",
                "BLUE",
                "YELLOW",
                "PINK",
                "BLACK",
                "WHITE",
                "PURPLE"
            )
            binding.colorFilterList.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 == 0) {  // "Select Size Option"
                        viewModel.resetListToFull()
                        return
                    }

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }
            val sizeList = listOf("Select Size", "XL", "L", "M", "S", "XS","43")

            binding.sizeFilterList.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 == 0) {  // "Select Size Option"
                        viewModel.resetListToFull()
                        return
                    }

                    viewModel.filterBySize(sizeList[p2])
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }


            binding.sizeFilterList.adapter = ProductFilterParamsAdapter(
                requireContext(),
                sizeList
            )

            binding.colorFilterList.adapter = ProductFilterParamsAdapter(
                requireContext(),
                colorList
            )
        }
    }


    private fun changeCategory(
        category: ProductCategory,
        gender: String,
        subCategory: ProductCategory.SubCategory = ProductCategory.SubCategory.ALL
    ) {
        viewModel.getAllProducts(Pair(category, subCategory), gender = gender,
            {
                showLoading("Loading products..")
            }) { dismissLoading() }
    }

}