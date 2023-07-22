package com.danielarog.myfirstapp.fragments.auth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.activities.MainActivity
import com.danielarog.myfirstapp.databinding.FragmentLoginBinding
import com.danielarog.myfirstapp.dialogs.LoadingDialog
import com.danielarog.myfirstapp.fragments.BaseFragment
import com.danielarog.myfirstapp.viewmodels.AuthViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.timer

class LoginFragment : BaseFragment() {



    lateinit var viewModel: AuthViewModel
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        binding.signInBtn.setOnClickListener {
            signInLauncher.launch(signInIntent)
        }

        FirebaseAuth.getInstance().currentUser?.let {
            showLoading()
            determineMainRoute()
        } ?: run {
            signInLauncher.launch(signInIntent)
        }
    }

    fun determineMainRoute() {
        lifecycleScope.launch {
            val appUser = viewModel.getUser()
            dismissLoading()
            if (appUser == null) {
                navigate(
                    R.id.action_loginFragment_to_newProfileFragment,
                    bundleOf(Pair(PROFILE_CREATION_MODE, PROFILE_CREATE))
                )
            } else {
                startActivity(Intent(this@LoginFragment.requireContext(), MainActivity::class.java))
                this@LoginFragment.requireActivity().finish()
            }
        }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                toast("Unknown error occurred, Please try again later")
                return
            }

            showLoading("Loading..")
            determineMainRoute()
        } else {

            //login failed
        }
    }
}