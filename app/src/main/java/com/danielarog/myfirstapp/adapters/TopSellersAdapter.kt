package com.danielarog.myfirstapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.databinding.TopSellerItemBinding
import com.danielarog.myfirstapp.models.AppUser
import com.squareup.picasso.Picasso

class TopSellersAdapter(
    private val topSellers: List<AppUser>,
    private val onTopSellerClickListener: (AppUser) -> Unit
) : RecyclerView.Adapter<TopSellersAdapter.TopSellerViewHolder>() {




    class TopSellerViewHolder(
        val binding: TopSellerItemBinding,
        private val onTopSellerClickListener: (AppUser) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topSeller: AppUser) {
            binding.topSellerName.text = topSeller.name
            binding.topSellerRating.text = "Ratings: ${topSeller.rating}"
            if (topSeller.image.isNotEmpty())
                Picasso.get().load(topSeller.image).into(binding.topSellerImage)

            binding.root
                .setOnClickListener {
                    onTopSellerClickListener.invoke(topSeller)
                }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopSellerViewHolder {
        val binding = TopSellerItemBinding.inflate(LayoutInflater.from(parent.context))
        return TopSellerViewHolder(binding,onTopSellerClickListener)
    }

    override fun onBindViewHolder(holder: TopSellerViewHolder, position: Int) {
        val topSeller = topSellers[position]
        holder.bind(topSeller)
    }

    override fun getItemCount(): Int {
        return topSellers.size
    }
}