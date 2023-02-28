package com.danielarog.myfirstapp.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.adapters.TopSellersAdapter
import com.danielarog.myfirstapp.databinding.FragmentHomeBinding
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.viewmodels.HomeViewModel

class HomeFragment : BaseFragment() {


    private lateinit var viewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = RecyclerView.HORIZONTAL
        binding.rvTopSeller.layoutManager = layoutManager
        viewModel.topSellersLiveData.observe(viewLifecycleOwner) {

            val adapter = TopSellersAdapter(it)
            binding.rvTopSeller.adapter = adapter
        }
    }


}