package com.danielarog.myfirstapp

import com.danielarog.myfirstapp.models.ProductCategory

object Utils {


    fun getCategoryEnumName(categoryString:String) : ProductCategory {
        return ProductCategory.valueOf(
            categoryString.uppercase()
                .replace(" ","_")
        )
    }
    fun getSubCategoryEnumName(subCategoryString:String) : ProductCategory.SubCategory {
        return ProductCategory.SubCategory.valueOf(
            subCategoryString.uppercase()
                .replace(" ","_")
        )
    }
}