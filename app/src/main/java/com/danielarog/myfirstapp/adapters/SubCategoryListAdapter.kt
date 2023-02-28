package com.danielarog.myfirstapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.repositories.CategoryMapping
import com.danielarog.myfirstapp.repositories.CategoryPair

class SubCategoryListAdapter(
    private val categories: CategoryMapping,
    val categoryClicked: (category: CategoryPair) -> Unit,
) : RecyclerView.Adapter<SubCategoryListAdapter.SubCategoryViewHolder>() {

    class SubCategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val catBtn: TextView = view.findViewById(R.id.sub_cat_tv)

        fun bind(sub: CategoryPair) {
            catBtn.text = sub.second.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sub_cateogry, parent, false)
        return SubCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
        val subCategory = categories[position]
        holder.itemView.setOnClickListener {
            categoryClicked(subCategory)
        }
        holder.bind(subCategory)
    }

    override fun getItemCount(): Int {
        return categories.size
    }


}
