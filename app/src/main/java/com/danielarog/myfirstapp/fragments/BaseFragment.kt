package com.danielarog.myfirstapp.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.snackbar.Snackbar

open class BaseFragment : Fragment() {


    var progressDialog : ProgressDialog? = null


    fun showLoading(message:String) {
        if(progressDialog == null) {
            progressDialog = ProgressDialog(requireContext())
            progressDialog!!.setCancelable(false)
            progressDialog!!.setTitle("Shop")
        }
        progressDialog!!.setMessage(message)
        progressDialog!!.show()
    }

    fun dismissLoading() {
        progressDialog?.dismiss()
    }

    fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun snackBar(message: String, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    fun navigate(actionId: Int, b: Bundle = Bundle()) {
        findNavController(this).navigate(actionId, b)
    }


    fun isValidFields(fields:List<EditText>) : Boolean {
        for (field in fields) {
            if(field.text.toString().isEmpty()) {
                field.requestFocus()
                return false
            }
        }
        return true
    }
}