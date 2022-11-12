package com.danielarog.myfirstapp.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.CategoryListAdapter
import com.danielarog.myfirstapp.ProductCategory
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.databinding.FragmentItemFeedBinding
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.viewmodels.AuthViewModel
import com.danielarog.myfirstapp.viewmodels.ShoppingListViewModel

class ItemFeedFragment : BaseFragment() {


    private lateinit var viewModel: ShoppingListViewModel
    private lateinit var authViewModel: AuthViewModel

    private var binding: FragmentItemFeedBinding? = null

    private val productRecyclerView: RecyclerView by lazy {
        binding!!.feedProductsRv
    }
    private val productCategoryRv: RecyclerView by lazy {
        binding!!.productCategoryRv
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        viewModel = ViewModelProvider(this)[ShoppingListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemFeedBinding.inflate(inflater)

        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        authViewModel.currentUser.observe(viewLifecycleOwner) {
//            println(it.address)
//        }
        val adapter = CategoryListAdapter {
            changeCategory(it.category)
        }
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.HORIZONTAL
        productCategoryRv.layoutManager = layoutManager

        val dev =  DividerItemDecoration(activity, LinearLayoutManager.HORIZONTAL)
        dev.setDrawable(ResourcesCompat.getDrawable(resources,R.drawable.devidor,null)!!)
        productCategoryRv.addItemDecoration(dev)
        productCategoryRv.adapter = adapter

        viewModel.shoppingItemsLiveData.observe(viewLifecycleOwner) {
            // assign adapter
        }

        viewModel.exceptionLiveData.observe(viewLifecycleOwner) {
            // show error
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun changeCategory(category: ProductCategory) {
       Toast.makeText(requireContext(),"Changed Category: ${category.value}",Toast.LENGTH_LONG).show()
    }

}