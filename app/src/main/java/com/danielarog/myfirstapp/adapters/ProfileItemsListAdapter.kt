package com.danielarog.myfirstapp.adapters

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.databinding.ProductRowItemBinding
import com.danielarog.myfirstapp.databinding.ProfileProductItemBinding
import com.danielarog.myfirstapp.models.ShoppingItem
import com.squareup.picasso.Picasso


interface ProfileItemClickListener {
    fun onClick(item:ShoppingItem)
}

class ProfileItemsListAdapter(val list: List<ShoppingItem>, val clickListener: ProfileItemClickListener) :
    RecyclerView.Adapter<ProfileItemsListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ProfileProductItemBinding) : RecyclerView.ViewHolder(binding.root) { }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val b = ProfileProductItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(b)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        item.image?.let {
            if (it.isEmpty()) {
                holder.binding.productImageItemRowProfile.setImageResource(R.drawable.noimage)
                return
            }
        }
        holder.binding.root.setOnClickListener {
            clickListener.onClick(item)
        }
        Picasso.get()
            .load(item.image).
            into(holder.binding.productImageItemRowProfile)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}