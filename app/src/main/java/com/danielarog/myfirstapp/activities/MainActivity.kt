package com.danielarog.myfirstapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
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


        navView.setNavigationItemSelectedListener {
            handleMenuNavigation(it.itemId)
            drawer.closeDrawers()
            true
        }

        val toolbarThing = findViewById<ConstraintLayout>(R.id.toolbar)
        val toggleButton = toolbarThing.findViewById<ImageView>(R.id.toolbarToggle)

        val backButton = toolbarThing.findViewById<ImageView>(R.id.backButtonMenu)

        toggleButton.setOnClickListener {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawers()
            } else {
                drawer.openDrawer(GravityCompat.START)
                toolbarThing.animate()
                    .translationY(-200.0f)
                    .setDuration(100)
                    .start()

                drawer.animate()
                    .translationY(-200.0f)
                    .setDuration(100)
                    .start()


            }
        }

        drawer.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                toolbarThing.animate()
                    .translationY(0.0f)
                    .setDuration(100)
                    .start()
                drawer.animate()
                    .translationY(0.0f)
                    .setDuration(100)
                    .start()
            }
            override fun onDrawerStateChanged(newState: Int) { }
        })


        backButton.setOnClickListener {
            if(controller.previousBackStackEntry !=null)
                controller.popBackStack()
            if(controller.previousBackStackEntry ==null)
                backButton.visibility = GONE
        }

    }


    private fun handleMenuNavigation(menuItemID: Int) {
        val controller =
            (supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment).navController
        val bundle = Bundle()

        val toolbarThing = findViewById<ConstraintLayout>(R.id.toolbar)
        val backButton = toolbarThing.findViewById<ImageView>(R.id.backButtonMenu)
        backButton.visibility  = VISIBLE

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
        return findNavController(R.id.navHost).navigateUp() ||
                super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (findNavController(R.id.navHost).currentDestination?.id
            != R.id.homeFragment) {
            return findNavController(R.id.navHost).popBackStack()
        }
        return super.onOptionsItemSelected(item)
    }


}