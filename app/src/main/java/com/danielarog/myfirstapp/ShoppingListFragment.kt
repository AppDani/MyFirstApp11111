package com.danielarog.myfirstapp

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danielarog.myfirstapp.models.ShoppingItem
import com.danielarog.myfirstapp.viewmodels.ShoppingListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*


class ShoppingListFragment : Fragment() {

    lateinit var shoppingViewModel: ShoppingListViewModel
    lateinit var addItemBtn : FloatingActionButton
    lateinit var shoppingListRv: RecyclerView
    lateinit var shoppingListRvAdapter: ShoppingListRvAdapter
    var dialog : AlertDialog? = null
    var isShowingAddDialog = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shoppingViewModel = ViewModelProvider(this)[ShoppingListViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_shopping_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shoppingListRv = view.findViewById(R.id.shoppingListRv)
        shoppingListRv.layoutManager = LinearLayoutManager(requireContext())
        addItemBtn = view.findViewById(R.id.addItemBtn)


        addItemBtn.setOnClickListener {
            if(isShowingAddDialog) {
                return@setOnClickListener
            }
            if(dialog !=null) {
                openDialog()
                return@setOnClickListener
            }

            val dialogView = layoutInflater.inflate(R.layout.add_item_dialog,null,false)
            val itemNameEt = dialogView.findViewById<EditText>(R.id.dialog_item_name)
            val itemImageEt = dialogView.findViewById<EditText>(R.id.dialog_item_image)
            val itemTypeEt = dialogView.findViewById<EditText>(R.id.dialog_item_type)
            val itemPriceEt = dialogView.findViewById<EditText>(R.id.dialog_item_price)
            val itemCommentsEt = dialogView.findViewById<EditText>(R.id.dialog_item_desc)

            val date = Date()
            val calendar = Calendar.getInstance()
            calendar.time = date
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val todayDateString = "$day/$month/$year"
            dialog = AlertDialog.Builder(requireContext())
                .setTitle("Shopping List")
                .setView(dialogView)
                .setPositiveButton("Add") { _, _ ->

                    val newItem = ShoppingItem(0,
                        itemNameEt.text.toString(),
                        itemTypeEt.text.toString(),
                        todayDateString,
                        itemPriceEt.text.toString(),
                        itemImageEt.text.toString(),
                        itemCommentsEt.text.toString())

                    shoppingViewModel.insertItem(newItem)
                    closeDialog()
                }
                .setNegativeButton("Cancel") { _, _ ->
                    closeDialog()
                }.create()
            openDialog()
        }
        shoppingViewModel.initializeRepo(requireContext().applicationContext)
        // pass the list to the recyclerview from the view model live data
        shoppingViewModel.shoppingItemsLiveData.observe(viewLifecycleOwner) {
            // create the adapter for the items
            shoppingListRvAdapter = ShoppingListRvAdapter(it.toMutableList()) { itemToDelete ->
                shoppingViewModel.deleteItem(itemToDelete)
            }
            // attach to recyclerview
            shoppingListRv.adapter = shoppingListRvAdapter
        }

    }

    private fun closeDialog() {
        dialog?.dismiss()
        isShowingAddDialog = false
    }
    private fun openDialog() {
        dialog?.show()
        isShowingAddDialog = true
    }


}