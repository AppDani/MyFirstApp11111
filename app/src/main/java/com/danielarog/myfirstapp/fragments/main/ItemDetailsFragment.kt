package com.danielarog.myfirstapp.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat.getExtras
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.databinding.ItemPageBinding
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.repositories.REQUEST_EXISTS
import com.danielarog.myfirstapp.repositories.SUCCESS
import com.danielarog.myfirstapp.repositories.USER_NOT_LOGGED
import com.danielarog.myfirstapp.repositories.UserRepository
import com.danielarog.myfirstapp.viewmodels.MainViewModel
import com.danielarog.myfirstapp.viewmodels.ProductRequestsViewModel
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


const val ITEM_DETAILS_ARG = "item"

class ItemDetailsFragment : BaseFragment {

    private var _binding: ItemPageBinding? = null
    val binding: ItemPageBinding get() = _binding!!
    private lateinit var viewModel: ProductRequestsViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var onClose: (Boolean) -> Unit


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemPageBinding.inflate(inflater)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[ProductRequestsViewModel::class.java]
        return binding.root
    }

    constructor(onClose: (Boolean) -> Unit) : this() {
        this.onClose = onClose
    }

    constructor() : super()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get product details from other screen
        val args = requireArguments()
        val itemString = args.getString(ITEM_DETAILS_ARG)
        val g = Gson()
        val item = g.fromJson(itemString, ShoppingItem::class.java)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val canMakeRequest = viewModel.canMakeRequest(item.id!!)
                withContext(Dispatchers.Main) {
                    binding.addToCartBtn.isEnabled = canMakeRequest
                }
            }
        }
        binding.addToCartBtn.setOnClickListener {
            it.isEnabled = false
            viewModel.makeRequest(item.publisherId!!, item.id!!)
        }

        if (mainViewModel.isFavorite(item.id!!)) {
            binding.itemLikeBtn.setImageResource(R.drawable.ic_baseline_favorite_24)
        }

        setLikeClickListener(item)
        observeRequestStatus(item)
        populateProductDetails(item)
        observeFavorites(item)

    }


    private fun observeFavorites(item: ShoppingItem) {
        mainViewModel.favorites.observe(viewLifecycleOwner) {
            if (mainViewModel.isFavorite(item.id!!)) {
                binding.itemLikeBtn.setImageResource(R.drawable.ic_baseline_favorite_24)
            } else {
                binding.itemLikeBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
        }
    }

    private fun setLikeClickListener(product: ShoppingItem) {

        binding.itemLikeBtn.setOnClickListener {
            if (mainViewModel.isFavorite(product.id!!)) {
                println("Favorite removed")
                mainViewModel.removeProductToFavorites(
                    product.id,
                    product.category!!,
                    product.subCategory!!
                )
            } else {
                println("Favorite Added")
                mainViewModel.addProductToFavorites(
                    product.id,
                    product.category!!,
                    product.subCategory!!
                )
            }
        }
    }


    private fun observeRequestStatus(item: ShoppingItem) {
        viewModel.requestStatusLiveData.observe(viewLifecycleOwner) { result ->
            when (result.status) {
                SUCCESS -> toast("Successfully made a request for item ${item.itemName}, the publisher will contact you soon")
                REQUEST_EXISTS -> toast("You have already made a request for this item ${item.itemName}")
                USER_NOT_LOGGED -> toast("Please login in order to make requests")
            }
        }
    }

    private fun populateProductDetails(item: ShoppingItem) {
        binding.ItemDescription.text = item.description
        binding.itemName.text = item.itemName
        binding.itemSize.text = item.size
        binding.itemPrice.text = item.price
        binding.itemGender.text = item.gender
        binding.itemLocationCity.text = item.location
        binding.sellerName.text = item.publisherName
        binding.dateUploaded.text = item.date
        binding.itemCondition.text = item.condition
        Picasso.get()
            .load(item.image)
            .into(binding.ItemImage)
    }


}