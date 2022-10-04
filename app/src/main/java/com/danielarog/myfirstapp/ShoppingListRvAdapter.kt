package com.danielarog.myfirstapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.models.ShoppingItem
import com.squareup.picasso.Picasso

class ShoppingListRvAdapter(private val list:MutableList<ShoppingItem>,
                            private val deleteItem: (item:ShoppingItem) -> Unit) : RecyclerView.Adapter<ShoppingListRvAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val  itemNameTv : TextView = view.findViewById(R.id.itemNameTv)
        private val  itemPriceTv : TextView = view.findViewById(R.id.itemPriceTv)
        private val  itemDateTv : TextView  = view.findViewById(R.id.itemDateTv)
        private val  itemCommentsTv : TextView = view.findViewById(R.id.itemCommentsTv)
        private val  itemImageIv : ImageView = view.findViewById(R.id.itemImageIv)
        private val  itemDeleteBtn : ImageView = view.findViewById(R.id.item_deleteBtn)
        fun bind(item : ShoppingItem, deleteItem : () -> Unit) {
            itemNameTv.text = item.itemName
            itemPriceTv.text = item.price
            itemDateTv.text = item.date
            itemCommentsTv.text = item.comments
            itemNameTv.text = item.itemName
            itemNameTv.text = item.itemName
            Picasso.get().load(item.image).into(itemImageIv)
            itemDeleteBtn.setOnClickListener { deleteItem() }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val shoppingItemView = LayoutInflater.from(parent.context).inflate(R.layout.shopping_list_item,parent,false)
        return ViewHolder(shoppingItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item) { deleteItem(item) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}