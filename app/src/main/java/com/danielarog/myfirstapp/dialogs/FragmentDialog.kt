package com.danielarog.myfirstapp.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import com.danielarog.myfirstapp.fragments.auth.PROFILE_CREATION_MODE
import com.danielarog.myfirstapp.fragments.auth.PROFILE_EDIT
import com.danielarog.myfirstapp.fragments.auth.PROFILE_EDIT_USER
import com.danielarog.myfirstapp.viewmodels.ProfileViewModel
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

typealias FragmentDialogArgs = Pair<Fragment, Bundle?>

data class DialogButton(val  label:String, val action : () -> Unit ) {
}

class FragmentDialog(
    private val name: String = "SECOND",
    private val fragmentProvider: (FragmentDialog) -> FragmentDialogArgs,
    private val dialogControls : DialogButton? = null,
    var onClose: ((Boolean) -> Unit)? = null
) : DialogFragment() {

    private lateinit var fragmentContainerView: FragmentContainerView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialogLayout = LinearLayout(requireContext())
        val dialogLayoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        dialogLayout.layoutParams = dialogLayoutParams
        dialogLayout.orientation = LinearLayout.VERTICAL

        fragmentContainerView = FragmentContainerView(requireContext())
        val layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)

        fragmentContainerView.layoutParams = layoutParams
        fragmentContainerView.id = View.generateViewId()

        dialogLayout.addView(fragmentContainerView)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogLayout)
            .setTitle(name)

        if(onClose != null) {
            dialog.setNegativeButton("Close", null)
        }
        dialogControls?.let {
            dialog.setPositiveButton(it.label
            ) { _, _ -> it.action.invoke() }
        }
        return dialog.create()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch {
            delay(100)
            val fragmentArgs = fragmentProvider.invoke(this@FragmentDialog)
            fragmentArgs.first.arguments = fragmentArgs.second
            nav(fragmentArgs.first)
        }
    }

    fun nav(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(fragmentContainerView.id, fragment)
            .commitNow()
    }

}