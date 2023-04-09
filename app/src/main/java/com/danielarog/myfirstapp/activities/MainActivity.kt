package com.danielarog.myfirstapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHost

import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.databinding.ActivityMainBinding
import com.danielarog.myfirstapp.repositories.UserRepository
import com.danielarog.myfirstapp.viewmodels.MainViewModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle

    lateinit var viewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        FirebaseAuth.getInstance().addAuthStateListener { state ->
            if(state.currentUser == null) {
                finish()
                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
            }
        }
        val navView = binding.navView
        val drawer = binding.drawer
        val controller =
            (supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment).navController
        toggle = ActionBarDrawerToggle(this, drawer, R.string.close, R.string.open)
        drawer.addDrawerListener(toggle)
        NavigationUI.setupWithNavController(navView, controller)
        setupActionBarWithNavController(controller, drawer)
        navView.setNavigationItemSelectedListener {
            handleMenuNavigation(it.itemId)
            drawer.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun handleMenuNavigation(menuItemID: Int) {
        val controller =
            (supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment).navController
        val bundle = Bundle()
        while (controller.previousBackStackEntry != null)
            controller.popBackStack()
        when (menuItemID) {
            R.id.Wcategory_shirts -> {
                bundle.putString("category", "SHIRTS")
                bundle.putString("gender", "FEMALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.favoriteProductsFragment -> {
                controller.navigate(R.id.action_homeFragment_to_favoritesFragment)
            }
            R.id.Wcategory_accesorise -> {
                bundle.putString("category", "ACCESSORIES")
                bundle.putString("gender", "FEMALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.Wcategory_dresses -> {
                bundle.putString("category", "DRESSES")
                bundle.putString("gender", "FEMALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.Wcategory_jackets -> {
                bundle.putString("category", "JACKETS")
                bundle.putString("gender", "FEMALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.Wcategory_jewelery -> {
                bundle.putString("category", "JEWELERY")
                bundle.putString("gender", "FEMALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.Wcategory_pants -> {
                bundle.putString("category", "PANTS")
                bundle.putString("gender", "FEMALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.Wcategory_skirts -> {
                bundle.putString("category", "SKIRTS")
                bundle.putString("gender", "FEMALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.Wcategory_swim -> {
                bundle.putString("category", "SWIM")
                bundle.putString("gender", "FEMALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.Wcategory_shoes -> {
                bundle.putString("category", "SHOES")
                bundle.putString("gender", "FEMALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }

            R.id.Mcategory_accesorise -> {
                bundle.putString("category", "ACCESSORIES")
                bundle.putString("gender", "MALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.Mcategory_jackets -> {
                bundle.putString("category", "JACKETS")
                bundle.putString("gender", "MALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.Mcategory_pants -> {
                bundle.putString("category", "PANTS")
                bundle.putString("gender", "MALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.Mcategory_swim -> {
                bundle.putString("category", "SWIM")
                bundle.putString("gender", "MALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.Mcategory_shirts -> {
                bundle.putString("category", "SHIRTS")
                bundle.putString("gender", "MALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.Mcategory_shoes -> {
                bundle.putString("category", "SHOES")
                bundle.putString("gender", "MALE")
                controller.navigate(R.id.action_homeFragment_to_itemListFragment, bundle)
            }
            R.id.menu_chat -> {
                controller.navigate(R.id.action_homeFragment_to_chatRoomsFragment)
            }
            R.id.menu_profile -> {
                val userString = Gson().toJson(viewModel.userLive.value)
                val i = Intent(this,ProfileActivity::class.java)
                i.putExtra("user",userString)
                startActivity(i)
            }
            R.id.menu_logout -> {
                AuthUI.getInstance().signOut(this@MainActivity)
            }
            else -> {}
        }
    }

    fun navigateUpTo(actionId:Int) {
        val controller =
            (supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment).navController

        while (controller.previousBackStackEntry != null)
            controller.popBackStack()
        controller.navigate(actionId)
    }


    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.navHost).navigateUp() || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (findNavController(R.id.navHost).currentDestination?.id != R.id.homeFragment) {
            return findNavController(R.id.navHost).popBackStack()
        }
        return toggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }


}