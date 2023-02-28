package com.danielarog.myfirstapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.models.Category



class CategoryListAdapter(val categoryClicked : (category:Category) -> Unit) : RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {
    private val categories = Category.categoryList()

    class CategoryViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        val catBtn: ImageView = view.findViewById(R.id.catBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_category_col,parent,false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.catBtn.setImageResource(category.icon)
        holder.itemView.setOnClickListener {
            categoryClicked(category)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}