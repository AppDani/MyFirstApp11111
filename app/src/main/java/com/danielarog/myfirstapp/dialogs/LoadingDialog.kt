package com.danielarog.myfirstapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.danielarog.myfirstapp.R

class LoadingDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.loading_dialog,null,false)

        val dialog = AlertDialog.Builder(context)
            .setTitle("SECOND")
            .setView(view)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)
        return dialog
    }
}