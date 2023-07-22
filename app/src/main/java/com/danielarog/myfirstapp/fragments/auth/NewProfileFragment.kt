package com.danielarog.myfirstapp.fragments.auth

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.databinding.FragmentNewProfileBinding
import com.danielarog.myfirstapp.fragments.*
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.launch


const val PROFILE_CREATION_MODE = "profile_mode"
const val PROFILE_EDIT = "profile_edit"
const val PROFILE_EDIT_USER = "profile_edit_user"
const val PROFILE_CREATE = "profile_create"

class NewProfileFragment : BaseFragment, PictureConsumer {
    private var _binding: FragmentNewProfileBinding? = null
    private val binding get() = _binding!!

    private var imageUri:Uri? = null
    private var imageByteArray:ByteArray? = null

    private var _user: AppUser? = null
    private val user: AppUser get() = _user!!

    private lateinit var viewModel: AuthViewModel

    private lateinit var onClose : (Boolean) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentNewProfileBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        return binding.root
    }

    constructor(onClose: (Boolean) -> Unit) :  this() {
        this.onClose = onClose
    }

    constructor()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNewProfileFragment()
        binding.newProfileBack.setOnClickListener { close(submitted = false) }
        binding.newProfileCreate.setOnClickListener { createProfile() }
        initPictureChooser(this)

        binding.newProfileImageView.setOnClickListener {
            PictureChooserDialog(this)
                .show(parentFragmentManager, "choose picture")
        }
    }



    private fun initNewProfileFragment() {
        when (requireArguments().getString(PROFILE_CREATION_MODE)) {
            PROFILE_EDIT -> {
                val args = requireArguments()
                val g = Gson()
                val userString = args.getString(PROFILE_EDIT_USER)
                _user = g.fromJson(userString, AppUser::class.java)
                binding.newProfileCreate.text = "Save Changes"



                binding.newProfileFullName.setText(user.name)
                binding.newProfileAddressCity.setText(user.address_city)
                binding.newProfileAddress.setText(user.address)

                when(user.gender.lowercase()) {
                    "male" -> binding.genderMaleOpt.isChecked = true
                    "female" -> binding.genderFemaleOpt.isChecked = true
                    else -> binding.genderOtherOpt.isChecked = true
                }
            }
            PROFILE_CREATE -> {}
        }
    }

    override fun consumeCameraPicture(uri: Uri?) {
        uri?.let {
            binding.newProfileImageView.setImageURI(it)
            imageUri = it
        }
    }

    override fun consumeGalleryPicture(bitmap: Bitmap?) {
        bitmap?.let {
            binding.newProfileImageView.setImageBitmap(it)
            imageByteArray = it.toByteArray()
        }
    }

    private fun close(submitted:Boolean) {
        when (requireArguments().getString(PROFILE_CREATION_MODE)) {
            PROFILE_EDIT -> {
                onClose(submitted)
            }
            PROFILE_CREATE -> {
                back()
            }
        }
    }

    private fun createProfile() {
        val genderRg = binding.genderRg

        val nameEditText = binding.newProfileFullName
        val addressEditText = binding.newProfileAddress
        val addressCityEditText = binding.newProfileAddressCity

        val gender = when (genderRg.checkedRadioButtonId) {
            R.id.genderMaleOpt -> "Male"
            R.id.genderFemaleOpt -> "Female"
            else -> "Other"
        }

        if (!isValidFields(listOf(nameEditText, addressEditText, addressCityEditText))) {
            toast("Please fill all the fields")
            return
        }

        // edit mode
        _user?.let { user ->
            user.name = nameEditText.text.toString()
            user.gender = gender
            user.address_city = addressCityEditText.text.toString()
            user.image = ""
            user.address = addressEditText.text.toString()
        } ?: run { // new profile
            _user = AppUser(
                FirebaseAuth.getInstance().uid!!,
                FirebaseAuth.getInstance().currentUser!!.email!!,
                nameEditText.text.toString(),
                gender,
                addressCityEditText.text.toString(),
                "",
                addressEditText.text.toString(),
                0
            )
        }
        showLoading(when(requireArguments().getString(PROFILE_CREATION_MODE)) {
            PROFILE_CREATE -> "Creating new profile..."
            else -> "Saving changes..."
        })

        lifecycleScope.launch {
            // @TODO save the user with the image URI
            viewModel.saveUser(_user!!, imageByteArray, imageUri)
            dismissLoading()
            snackBar(when(requireArguments().getString(PROFILE_CREATION_MODE)) {
                PROFILE_CREATE -> "Successfully created profile, you may login"
                else -> "Saved changes"
            }, binding.root)
            close(submitted = true)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}