package com.danielarog.myfirstapp.models

import com.danielarog.myfirstapp.ProductCategory
import com.danielarog.myfirstapp.R

data class Category(
    val category: ProductCategory,
    val icon : Int
) {
    companion object {
        fun categoryList() : List<Category> {
            val categories = mutableListOf<Category>()
            categories.add(Category(ProductCategory.SHIRTS, R.drawable.shirt))
            categories.add(Category(ProductCategory.PANTS, R.drawable.pants))
            categories.add(Category(ProductCategory.SKIRTS, R.drawable.skirt))
            categories.add(Category(ProductCategory.DRESSES, R.drawable.dress))
            categories.add(Category(ProductCategory.JACKETS, R.drawable.jacket))
            categories.add(Category(ProductCategory.SWIM, R.drawable.swimsuits))
            categories.add(Category(ProductCategory.SHOES, R.drawable.shoes))
            categories.add(Category(ProductCategory.JEWELERY, R.drawable.jewelry))
            categories.add(Category(ProductCategory.ACCESSORIES, R.drawable.accesories))
            return categories
        }
        fun subCategoryList() : List<ProductCategory.SubCategory> {
            return ProductCategory.SubCategory.values().toList()
        }
    }
}
