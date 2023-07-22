package com.danielarog.myfirstapp.fragments

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.activities.BaseActivity
import com.danielarog.myfirstapp.activities.MainActivity
import com.danielarog.myfirstapp.dialogs.LoadingDialog
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream


fun isValidFields(fields: List<EditText>): Boolean {
    for (field in fields) {
        if (field.text.toString().isEmpty()) {
            field.requestFocus()
            return false
        }
    }
    return true
}


class PictureChooserDialog(
    private val pictureChooser: PictureChooser
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.picture_chooser_dialog, null, false)

        val chooserFromGalleryLayout: LinearLayout =
            dialogView.findViewById(R.id.pictureChooseFromGallery)
        val chooserFromCameraLayout: LinearLayout =
            dialogView.findViewById(R.id.pictureChooseFromCamera)

        chooserFromGalleryLayout.setOnClickListener {
            pictureChooser.openGallery()
            dismiss()
        }
        chooserFromCameraLayout.setOnClickListener {
            pictureChooser.openCamera()
            dismiss()
        }

        return AlertDialog.Builder(context)
            .setView(dialogView)
            .create()
    }
}


interface PictureConsumer {
    fun consumeCameraPicture(uri: Uri?)

    fun consumeGalleryPicture(bitmap: Bitmap?)
}

interface PictureChooser {
    fun openCamera()
    fun openGallery()
}

class DefaultPictureConsumer : PictureConsumer {
    override fun consumeCameraPicture(uri: Uri?) {}
    override fun consumeGalleryPicture(bitmap: Bitmap?) {}
}

fun Bitmap.toByteArray(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    return outputStream.toByteArray()
}

fun Fragment.checkPermission(permission: String): Boolean {
    val permissionResult = ContextCompat.checkSelfPermission(requireContext(), permission)
    if (permissionResult != PERMISSION_GRANTED) {
        requestPermission(permission)
        return false
    }
    return true
}

fun Fragment.requestPermission(permission: String) {
    ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), 0)
}

abstract class BaseFragment : Fragment(), PictureChooser {
    private var progressDialog: LoadingDialog? = null
    lateinit var pictureResultLauncher: ActivityResultLauncher<Intent>

    override fun openGallery() {
        if (checkPermission(READ_EXTERNAL_STORAGE)) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pictureResultLauncher.launch(intent)
        }
    }

    override fun openCamera() {
        if (checkPermission(CAMERA)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            pictureResultLauncher.launch(intent)
        }
    }

    fun initPictureChooser(consumer: PictureConsumer = DefaultPictureConsumer()) {
        pictureResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val bundle: Bundle? = data?.extras
                if (data != null) {
                    val selectedImageUri: Uri? = data.data
                    if (selectedImageUri == null) {
                        val bmp = bundle?.getParcelable("data") as? Bitmap
                        consumer.consumeGalleryPicture(bmp)
                    } else {
                        consumer.consumeCameraPicture(selectedImageUri)
                    }
                }
            }
        }
    }


    fun setScreenName(name: String) {
        val activityMain = activity as MainActivity
        val toolbarThing = activityMain.findViewById<ConstraintLayout>(R.id.toolbar)
        val sNameTv = toolbarThing.findViewById<TextView>(R.id.screenNameTv)
        sNameTv.text = name
    }


    fun showLoading(message: String = "") {
        if (progressDialog == null) {
            progressDialog = LoadingDialog()
        }
        progressDialog?.show(
            childFragmentManager,
            "Loading"
        )
    }

    fun dismissLoading() {
        progressDialog?.dismiss()
    }

    fun toast(message: String) {
        Toast.makeText(requireActivity().applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    fun snackBar(message: String, view: View) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    fun navigate(actionId: Int, b: Bundle = Bundle()) {
        findNavController(this)
            .navigate(actionId, b)
    }

    fun back() {
        findNavController(this).popBackStack()
    }


}