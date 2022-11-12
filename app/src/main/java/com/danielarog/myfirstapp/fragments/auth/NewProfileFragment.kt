package com.danielarog.myfirstapp.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.databinding.FragmentNewProfileBinding
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class NewProfileFragment : BaseFragment() {

    var _binding: FragmentNewProfileBinding? = null

    lateinit var viewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewProfileBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameEditText = _binding!!.newProfileFullName
        val addressEditText = _binding!!.newProfileAddress
        val addressCityEditText = _binding!!.newProfileAddressCity

        val genderRg = _binding!!.genderRg

        _binding!!.newProfileBack.setOnClickListener {
            findNavController(this).popBackStack()
        }
        _binding!!.newProfileCreate.setOnClickListener {

            val gender = when (genderRg.checkedRadioButtonId) {
                R.id.genderMaleOpt -> "Male"
                R.id.genderFemaleOpt -> "Female"
                else -> "Other"
            }


            if (!isValidFields(listOf(nameEditText, addressEditText, addressCityEditText))) {
                toast("Please fill all the fields")
                return@setOnClickListener
            }
            val newAppUser = AppUser(
                FirebaseAuth.getInstance().uid!!,
                FirebaseAuth.getInstance().currentUser!!.email!!,
                nameEditText.text.toString(),
                gender,
                addressCityEditText.text.toString(),
                addressEditText.text.toString(),
                0
            )
            lifecycleScope.launch {
                viewModel.saveUser(newAppUser)
                findNavController(this@NewProfileFragment).popBackStack()
                toast("Successfully created profile, you may login")
            }

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}