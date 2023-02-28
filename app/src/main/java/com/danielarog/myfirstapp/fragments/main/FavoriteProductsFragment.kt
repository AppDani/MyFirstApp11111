package com.danielarog.myfirstapp.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielarog.myfirstapp.adapters.ShoppingListRvAdapter
import com.danielarog.myfirstapp.databinding.FragmentFavoritesBinding
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.viewmodels.MainViewModel
import kotlinx.coroutines.launch

class FavoriteProductsFragment : BaseFragment() {


    lateinit var mainViewModel: MainViewModel

    var _binding : FragmentFavoritesBinding? = null
    val binding : FragmentFavoritesBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        _binding = FragmentFavoritesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.favoritesRv.layoutManager = LinearLayoutManager(requireContext())
        mainViewModel.favoriteProducts.observe(viewLifecycleOwner) {
            val adapter = ShoppingListRvAdapter(it.toMutableList()) {

            }
            binding.favoritesRv.adapter = adapter
        }
        lifecycleScope.launch {
            mainViewModel.getFavoriteProducts()
        }
    }
}