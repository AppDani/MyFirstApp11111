package com.danielarog.myfirstapp.fragments.main

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.*
import com.danielarog.myfirstapp.adapters.ShoppingListRvAdapter
import com.danielarog.myfirstapp.adapters.SubCategoryListAdapter
import com.danielarog.myfirstapp.databinding.FragmentItemFeedBinding
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.models.ProductCategory
import com.danielarog.myfirstapp.adapters.CategoryListAdapter
import com.danielarog.myfirstapp.viewmodels.AuthViewModel
import com.danielarog.myfirstapp.viewmodels.ShoppingListViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ItemFeedFragment : BaseFragment() {


    private lateinit var viewModel: ShoppingListViewModel

    private lateinit var authViewModel: AuthViewModel
    private lateinit var subCategoryDialog: AlertDialog
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

        val adapter = CategoryListAdapter {
            changeCategory(it.category)
        }
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.HORIZONTAL
        productCategoryRv.layoutManager = layoutManager

        val dev = DividerItemDecoration(activity, LinearLayoutManager.HORIZONTAL)
        dev.setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.devidor, null)!!)
        productCategoryRv.addItemDecoration(dev)
        productCategoryRv.adapter = adapter
        productRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)

        observeAppProducts()
        observeExceptions()
    }


    fun observeAppProducts() {
        viewModel.shoppingItemsLiveData.observe(viewLifecycleOwner) { list ->
            productRecyclerView.adapter =
                ShoppingListRvAdapter(list.toMutableList()) { shoppingItem ->
                    // move to detail page
                    val bundle = Bundle()
                    val gson = Gson()
                    bundle.putString("item", gson.toJson(shoppingItem))
                  /*  NavHostFragment.findNavController(this)
                        .navigate(R.id.action_shoppingListFragment_to_itemDetailsFragment, bundle)
               */ }
        }
    }

    fun observeExceptions() {
        viewModel.exceptionLiveData.observe(viewLifecycleOwner) {
            println(it.message)
            toast(it.message ?: "")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }




    private fun changeCategory(category: ProductCategory) {

        lifecycleScope.launch {
            val subCategories = category.getSubCategories().map { Pair(category, it) }
                .plus(Pair(category, ProductCategory.SubCategory.ALL))
            val pickSubLayout = layoutInflater.inflate(R.layout.pick_sub_category, null, false)
            val rv: RecyclerView = pickSubLayout.findViewById(R.id.productSubCategoryRv)
            rv.layoutManager = LinearLayoutManager(requireContext())
            /*rv.adapter = SubCategoryListAdapter(subCategories) { categoryPair ->
                viewModel.getAllProducts(categoryPair,
                    {
                        showLoading("Loading products..")
                    }) { dismissLoading() }
                subCategoryDialog.dismiss()
            }*/
            subCategoryDialog = AlertDialog.Builder(requireContext())
                .setTitle("SECOND")
                .setView(pickSubLayout)
                .create()
            subCategoryDialog.show()
        }


        Toast.makeText(requireContext(), "Changed Category: ${category.value}", Toast.LENGTH_LONG)
            .show()
    }


}