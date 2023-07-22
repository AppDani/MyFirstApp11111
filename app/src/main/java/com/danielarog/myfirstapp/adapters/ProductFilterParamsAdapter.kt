package com.danielarog.myfirstapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.databinding.SpinnerProductFilterItemBinding

//
//enum FilterParam {
//    Size,
//    Color
//}

class ProductFilterParamsAdapter(
    context: Context,
    val filterParams: List<String>
) : ArrayAdapter<String>(
    context,
    R.layout.spinner_product_filter_item,
    R.id.product_filter_param,
    filterParams
) {


}