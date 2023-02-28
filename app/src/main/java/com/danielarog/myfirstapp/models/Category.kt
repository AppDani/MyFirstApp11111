package com.danielarog.myfirstapp.models

import com.danielarog.myfirstapp.R
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

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

        fun pick3Categories() : List<Category> {
            val categories = categoryList()
            val side_a = Random(1).nextInt(categories.indices)
            val side_b = Random(1).nextInt(1 until categories.size)
           return categoryList().subList(min(side_a,side_b), max(side_a,side_b))
        }
        fun subCategoryList() : List<ProductCategory.SubCategory> {
            return ProductCategory.SubCategory.values().toList()
        }
    }
}
