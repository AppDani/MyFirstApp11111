package com.danielarog.myfirstapp.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.activities.MainActivity
import com.danielarog.myfirstapp.adapters.TopSellersAdapter
import com.danielarog.myfirstapp.databinding.FragmentHomeBinding
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.models.AppUser
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
        viewModel.topSellersLiveData.observe(viewLifecycleOwner) { topSellers ->

            val adapter = TopSellersAdapter(topSellers) { selectedTopSeller ->
                Navigation.findNavController(binding.root)
                    .navigate(
                        R.id.action_homeFragment_to_itemListPerPublisher,
                        bundleOf(
                            Pair("userId", selectedTopSeller.uid)
                        )
                    )
            }
            binding.rvTopSeller.adapter = adapter
        }

        val mainActivity = activity as MainActivity
        val mainViewModel = mainActivity.viewModel
        mainViewModel.userLive.observe(viewLifecycleOwner) {
            setScreenName("Hi ${it.name}")
        }
    }


}