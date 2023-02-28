package com.danielarog.myfirstapp.activities

import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class BaseActivity : AppCompatActivity() {


    fun isValidFields(fields: List<EditText>): Boolean {
        for (field in fields) {
            if (field.text.toString().isEmpty()) {
                field.requestFocus()
                return false
            }
        }
        return true
    }
}