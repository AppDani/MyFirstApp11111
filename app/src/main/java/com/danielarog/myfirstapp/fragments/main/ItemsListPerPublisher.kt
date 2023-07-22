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
import com.danielarog.myfirstapp.databinding.FragmentPublisherItemListBinding
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.models.ProductCategory
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.viewmodels.ProductsByUserViewModel
import com.danielarog.myfirstapp.viewmodels.ShoppingListViewModel
import com.google.api.Distribution.BucketOptions.Linear
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

class ItemListPerPublisher : BaseFragment() {

    private var _binding: FragmentPublisherItemListBinding? = null
    val binding get() = _binding!!
    private lateinit var viewModel: ProductsByUserViewModel

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPublisherItemListBinding.inflate(inflater)
        viewModel = ViewModelProvider(this)[ProductsByUserViewModel::class.java]
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val layoutManager = binding.itemListRv.layoutManager as LinearLayoutManager
        val lastRecyclerViewPosition = layoutManager.findFirstVisibleItemPosition()
        outState.putInt("scroll_pos", lastRecyclerViewPosition)
        val list = viewModel.productsLiveData.value
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


        arguments?.getString("userId", FirebaseAuth.getInstance().uid)?.let { userId ->
            viewModel.getUser(userId)
            viewModel.getProductsByUserID(userId)
        }



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
            viewModel.productsLiveData.observe(viewLifecycleOwner) {
                productRecyclerView.adapter =
                    ShoppingListRvAdapter(it.toMutableList()) { shoppingItem ->
                        val shoppingItemAsJson = Gson().toJson(shoppingItem)
                        Navigation.findNavController(binding.root)
                            .navigate(
                                R.id.action_itemListPerPublisher_to_itemDetailsFragment,
                                bundleOf(
                                    Pair(ITEM_DETAILS_ARG, shoppingItemAsJson)
                                )
                            )
                    }
            }
            viewModel.publisherLiveData.observe(viewLifecycleOwner) { user ->
                setScreenName("${user.name}'s shop")
                binding.publisherNameTv.text = user.name
            }

        }
    }

}