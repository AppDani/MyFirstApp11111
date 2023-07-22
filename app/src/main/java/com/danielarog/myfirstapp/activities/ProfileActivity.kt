package com.danielarog.myfirstapp.activities


import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.R
import com.danielarog.myfirstapp.adapters.ProfileItemClickListener
import com.danielarog.myfirstapp.adapters.ProfileItemsListAdapter
import com.danielarog.myfirstapp.databinding.ActivityProfileBinding
import com.danielarog.myfirstapp.dialogs.ProductActionsFragment
import com.danielarog.myfirstapp.dialogs.DialogButton
import com.danielarog.myfirstapp.dialogs.FragmentDialog
import com.danielarog.myfirstapp.dialogs.FragmentDialogArgs
import com.danielarog.myfirstapp.fragments.auth.NewProfileFragment
import com.danielarog.myfirstapp.fragments.auth.PROFILE_CREATION_MODE
import com.danielarog.myfirstapp.fragments.auth.PROFILE_EDIT
import com.danielarog.myfirstapp.fragments.auth.PROFILE_EDIT_USER
import com.danielarog.myfirstapp.fragments.main.ITEM_DETAILS_ARG
import com.danielarog.myfirstapp.fragments.main.ItemDetailsFragment
import com.danielarog.myfirstapp.models.AppUser
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.viewmodels.ProfileViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity(), ProfileItemClickListener {

    lateinit var viewModel: ProfileViewModel
    lateinit var binding: ActivityProfileBinding
    lateinit var profileItemsListAdapter: ProfileItemsListAdapter
    private val profileItemsRecyclerView: RecyclerView by lazy { binding.profileItemsRv }
    private lateinit var user: AppUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        user = Gson().fromJson(
            intent.getStringExtra("user"),
            AppUser::class.java
        )
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        attachEditProductAction()
        attachAddProductAction()
        updateUIUserDetails()
        observeAppProducts()

    }

    private fun attachAddProductAction() {
        binding.profileAddProductBtn.setOnClickListener {
            val dialog = FragmentDialog(fragmentProvider = { dialog ->

                FragmentDialogArgs(
                    ProductActionsFragment(viewModel, user) { submitted ->
                        dialog.dismiss()
                        dialog.onClose?.invoke(submitted)
                        dialog.childFragmentManager.clearBackStack("AddItem")
                    }, null
                )
            }, dialogControls = null) { submitted ->
                if (submitted)
                    Snackbar.make(binding.root, "Saved changes!", Snackbar.LENGTH_SHORT).show()
            }
            dialog.show(supportFragmentManager, "AddItem")
        }
    }


    private fun attachEditProductAction() {
        binding.EditProfileBtn.setOnClickListener {
            val dialog = FragmentDialog(fragmentProvider = { dialog ->
                val bundleForFragment = bundleOf(
                    Pair(PROFILE_CREATION_MODE, PROFILE_EDIT),
                    Pair(PROFILE_EDIT_USER, Gson().toJson(user))
                )
                FragmentDialogArgs(
                    NewProfileFragment { submitted ->
                        dialog.dismiss()
                        dialog.onClose?.invoke(submitted)
                        dialog.childFragmentManager.clearBackStack("EditProfile")
                    }, bundleForFragment
                )
            }) { submitted ->
                if (submitted)
                    Snackbar.make(binding.root, "Saved changes!", Snackbar.LENGTH_SHORT).show()
            }
            dialog.show(supportFragmentManager, "EditProfile")
        }
    }


    private fun observeAppProducts() {
        val userProductsObserver = Observer<List<ShoppingItem>> {
            profileItemsRecyclerView.adapter = ProfileItemsListAdapter(it, this)
        }

        viewModel.userProductsLiveData.observe(this, userProductsObserver)
    }

    private fun updateUIUserDetails() {
        binding.profileSellerName.text = user.name
        binding.profileSellerRating.text = "Rating: ${user.rating}"
        binding.profileSellerCity.text = "Location: ${user.address_city}"
        if (user.image.isNotBlank())
            Picasso.get().load(user.image).into(binding.sellerProfileImage)
    }

    private fun editItem(item: ShoppingItem) {
        val dialog = FragmentDialog(fragmentProvider = { dialog ->

            FragmentDialogArgs(
                ProductActionsFragment(
                    viewModel,
                    editItem = item,
                    user = user
                ) { submitted ->
                    dialog.dismiss()
                    dialog.onClose?.invoke(submitted)
                    dialog.childFragmentManager.clearBackStack("AddItem")
                }, null
            )
        }) { submitted ->
            if (submitted)
                Snackbar.make(binding.root, "Saved changes!", Snackbar.LENGTH_SHORT).show()
        }
        dialog.show(supportFragmentManager, "AddItem")
    }


    private fun deleteItem(item: ShoppingItem) {
        val secureDeleteDialog = AlertDialog.Builder(this)
            .setMessage("Are you sure you would like to delete this item ?")
            .setPositiveButton(
                "Yes"
            ) { p0, p1 ->
                viewModel.deleteItem(item)
                Snackbar.make(binding.root, "Item deleted successfully", Snackbar.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton("Close", null)
        secureDeleteDialog.show()
    }

    override fun onClick(item: ShoppingItem) {
        val dialog = FragmentDialog(
            dialogControls = DialogButton("Edit") {
                val editDialogView =
                    layoutInflater.inflate(R.layout.product_actions_dialog, null, false)
                val editDialogBuilder = AlertDialog.Builder(this)
                    .setView(editDialogView)
                    .setNegativeButton("Close", null)
                var editDialog: AlertDialog? = null
                val editButton =
                    editDialogView.findViewById<Button>(R.id.editButtonProductActions)
                val deleteButton =
                    editDialogView.findViewById<Button>(R.id.deleteButtonProductActions)
                editButton.setOnClickListener {
                    editItem(item)
                    editDialog?.dismiss()
                }
                deleteButton.setOnClickListener {
                    deleteItem(item)
                    editDialog?.dismiss()
                }
                editDialog = editDialogBuilder.show()
            },
            fragmentProvider = { dialog ->

                val itemASJson = Gson().toJson(item)
                val bundleForFragment = bundleOf(
                    Pair(ITEM_DETAILS_ARG, itemASJson),
                )
                FragmentDialogArgs(
                    ItemDetailsFragment { submitted ->
                        dialog.dismiss()
                        dialog.onClose?.invoke(submitted)
                        dialog.childFragmentManager.clearBackStack("ItemDetails")
                    }, bundleForFragment
                )
            }) { submitted ->
            if (submitted)
                Snackbar.make(binding.root, "Saved changes!", Snackbar.LENGTH_SHORT).show()
        }
        dialog.show(supportFragmentManager, "ItemDetails")
    }

}