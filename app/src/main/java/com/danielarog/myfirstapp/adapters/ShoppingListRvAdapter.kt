package com.danielarog.myfirstapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.models.ShoppingItem
import com.squareup.picasso.Picasso

class ShoppingListRvAdapter(
    private val list: MutableList<ShoppingItem>,
    private val viewItem: (item: ShoppingItem) -> Unit
) : RecyclerView.Adapter<ShoppingListRvAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemNameTv: TextView = view.findViewById(R.id.productName_itemRow)
        private val itemPriceTv: TextView = view.findViewById(R.id.productPrice_itemRow)

        private val itemSizeTv: TextView = view.findViewById(R.id.productSize_itemRow)
        private val itemPubNameTv: TextView = view.findViewById(R.id.productPubName_itemRow)
        private val itemImageIv: ImageView = view.findViewById(R.id.productImage_itemRow)
        fun bind(item: ShoppingItem, viewItem: () -> Unit) {
            itemNameTv.text = item.itemName
            itemPriceTv.text = "${item.price}$"
            itemSizeTv.text = "L"
            itemPubNameTv.text = item.publisherName

            item.image?.let {
                if(it.isEmpty()) {
                    itemImageIv.setImageResource(R.drawable.noimage)
                } else Picasso.get().load(item.image).into(itemImageIv)
            } ?: run {
                itemImageIv.setImageResource(R.drawable.noimage)
            }

            itemView.setOnClickListener {
                viewItem.invoke()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val shoppingItemView =
            LayoutInflater.from(parent.context).inflate(R.layout.product_row_item, parent, false)
        return ViewHolder(shoppingItemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item) { viewItem.invoke(item) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}