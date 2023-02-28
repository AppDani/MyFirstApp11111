package com.danielarog.myfirstapp.dialogs

import android.graphics.Bitmap
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.danielarog.myfirstapp.databinding.AddItemDialogBinding
import com.danielarog.myfirstapp.fragments.*
import com.danielarog.myfirstapp.models.*
import com.danielarog.myfirstapp.viewmodels.ProfileViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


data class ProductImageSelection(
    var imageUri: Uri? = null,
    var imageByteArray: ByteArray? = null
)

class ProductActionsFragment(var viewModel: ProfileViewModel?) : BaseFragment(), PictureConsumer {
    var mode: String = "add"
    private var _binding: AddItemDialogBinding? = null
    private val binding: AddItemDialogBinding by lazy { _binding!! }

    // existing product that is being edited
    private var _product: ShoppingItem? = null
    private val product: ShoppingItem get() = _product!!
    private lateinit var onClose: (Boolean) -> Unit

    // main image
    private var imageUri: Uri? = null
    private var imageByteArray: ByteArray? = null

    // additional images
    private var additionalImagesArray = mutableListOf<ProductImageSelection>()


    private var edittingImage: Int? = null
    private var subCategoryList = Category.subCategoryList()
    private var conditionList = Condition.conditionList()
    private var genderList = Gender.genderList()

    private fun isEditMode() = "edit".equals(mode)

    private fun didSelectMainImage() = imageUri != null || imageByteArray !== null
    private fun didSelectAllImage() = additionalImagesArray.size >= 3

    constructor(
        viewModel: ProfileViewModel?,
        onClose: (Boolean) -> Unit
    ) : this(viewModel) {
        this.onClose = onClose
    }

    constructor(
        viewModel: ProfileViewModel?,
        editItem: ShoppingItem,
        onClose: (Boolean) -> Unit
    ) : this(viewModel) {
        this._product = editItem
        this.onClose = onClose
        this.mode = "edit"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddItemDialogBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createSpinnerCategory()
        createSpinnerSubcategory()
        createSpinnerGender()
        createSpinnerCondition()
        populateProductDetails()
        initPictureChooser(this)
        binding.dialogItemSubmit.text = if (isEditMode()) {
            "Save Changes"
        } else
            "Add product"

        attachSubmitProductAction()
        attachGalleryImageActions()


        binding.dialogItemPhoto.setOnClickListener {
            if (didSelectAllImage()) {
                toast("Cannot add more then 3 additional images, you may change selected images by clicking them")
                return@setOnClickListener
            }
            PictureChooserDialog(this)
                .show(parentFragmentManager, "choose picture")
        }
    }

    private fun attachGalleryImageActions() {
        binding.dialogItemPhotoIv.setOnClickListener {
            edittingImage = 0
            PictureChooserDialog(this)
                .show(parentFragmentManager, "choose picture")
        }
        binding.imageGalleryItem1.setOnClickListener {
            edittingImage = 1
            PictureChooserDialog(this)
                .show(parentFragmentManager, "choose picture")
        }
        binding.imageGalleryItem2.setOnClickListener {
            edittingImage = 2
            PictureChooserDialog(this)
                .show(parentFragmentManager, "choose picture")
        }
        binding.imageGalleryItem3.setOnClickListener {
            edittingImage = 3
            PictureChooserDialog(this)
                .show(parentFragmentManager, "choose picture")
        }
    }


    private fun attachSubmitProductAction() {
        val submitBtn = binding.dialogItemSubmit
        submitBtn.setOnClickListener {
            val list = mutableListOf<EditText>()
            list.add(binding.dialogItemName)
            list.add(binding.dialogItemDesc)
            list.add(binding.dialogItemPrice)

            if (imageUri == null && imageByteArray == null && !isEditMode()) {
                toast("Please select an image")
            } else if (!isValidFields(list)) {
                toast("Please fill all the fields")
            } else {
                if (isEditMode())
                    showLoading("Saving changes..")
                else
                    showLoading("Adding new item..")

                updateProductFromFields()
                if (!isEditMode()) {
                    val formatter = DateTimeFormatter.ISO_DATE
                    val date = LocalDate.now().format(formatter)
                    product.date = date
                }

                submitProduct()
            }
        }
    }

    private fun submitProduct() {
        lifecycleScope.launch {
            if (isEditMode()) {
                viewModel?.editItem(product, imageUri, imageByteArray)
                toast("Successfully saved changes")
            } else {
                viewModel?.addItem(product, imageUri, imageByteArray, additionalImagesArray)
                toast("Successfully added item")
            }
            dismissLoading()
            onClose.invoke(true) /* success */
        }
    }

    private fun updateProductFromFields() {
        product.itemName = binding.dialogItemName.text.toString()
        product.price = binding.dialogItemPrice.text.toString()
        product.gender = genderList[binding.dialogItemGender.selectedItemPosition]
        product.description = binding.dialogItemDesc.text.toString()
        product.condition = conditionList[binding.dialogItemCondition.selectedItemPosition]
        product.size = binding.dialogItemSize.text.toString()
    }


    private fun populateProductDetails() {
        _product?.let {
            binding.dialogItemName.setText(it.itemName)
            binding.dialogItemPrice.setText(it.price)
            binding.dialogItemDesc.setText(it.description)
            binding.dialogItemSize.setText(it.size)

            Picasso.get().load(it.image)
                .into(binding.dialogItemPhotoIv)

            val listOfImages = mutableListOf<ImageView>()
            listOfImages.add(binding.imageGalleryItem1)
            listOfImages.add(binding.imageGalleryItem2)
            listOfImages.add(binding.imageGalleryItem3)
            it.additionalImages?.let { images ->
                for (i in images.indices) {
                    additionalImagesArray.add(ProductImageSelection())
                    listOfImages[i].visibility = VISIBLE
                    Picasso.get().load(images[i])
                        .into(listOfImages[i])
                }
            }

        } ?: run {
            _product = ShoppingItem()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        viewModel = null
    }

    private fun createSpinnerGender() {
        val binding = _binding!!
        val spinner = binding.dialogItemGender
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderList)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val category = genderList[p2]
                product.category = category
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun createSpinnerCategory() {
        val binding = _binding!!
        val spinner = binding.dialogItemCategory
        val categoryList = Category.categoryList().map {
                category -> category.category.value.lowercase()
        }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryList)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val category = categoryList[p2]
                product.category = category
                val categoryEn = ProductCategory.valueOf(product.category!!.uppercase())
                subCategoryList = categoryEn.getSubCategories()
                createSpinnerSubcategory()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun createSpinnerCondition() {
        val binding = _binding!!
        val spinner = binding.dialogItemCondition

        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                conditionList
            )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val subCategory = subCategoryList[p2]
                product.subCategory = subCategory.value
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }


    private fun createSpinnerSubcategory() {
        val binding = _binding!!
        val spinner = binding.dialogItemSubCategory
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                subCategoryList.map {
                    it.value.lowercase()
                }
            )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val subCategory = subCategoryList[p2]
                product.subCategory = subCategory.value
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    override fun consumeCameraPicture(uri: Uri?) {
        uri?.let {

            edittingImage?.let { editing -> //  image re-select mode
                when (editing) {
                    0 -> {
                        binding.dialogItemPhotoIv.setImageURI(it)
                        imageUri = it
                    }
                    1 ->  {
                        binding.imageGalleryItem1.setImageURI(it)
                        additionalImagesArray[0].imageUri = it
                    }
                    2 ->  {
                        binding.imageGalleryItem2.setImageURI(it)
                        additionalImagesArray[1].imageUri = it
                    }
                    3 ->  {
                        binding.imageGalleryItem3.setImageURI(it)
                        additionalImagesArray[2].imageUri = it
                    }
                }
                edittingImage = null
            } ?: run { // normal image select mode


                if (didSelectMainImage()) {
                    when (additionalImagesArray.size) {
                        0 -> {
                            binding.imageGalleryItem1.visibility = VISIBLE
                            binding.imageGalleryItem1.setImageURI(it)
                        }
                        1 -> {
                            binding.imageGalleryItem2.visibility = VISIBLE
                            binding.imageGalleryItem2.setImageURI(it)
                        }
                        2 -> {
                            binding.imageGalleryItem3.visibility = VISIBLE
                            binding.imageGalleryItem3.setImageURI(it)
                        }
                    }
                    additionalImagesArray.add(ProductImageSelection(imageUri = it))
                    return@let
                }
                binding.dialogItemPhotoIv.setImageURI(it)
                imageUri = it
            }
        }
    }

    override fun consumeGalleryPicture(bitmap: Bitmap?) {
        bitmap?.let {
            edittingImage?.let { editing -> //  image re-select mode
                when (editing) {
                    0 -> {
                        binding.dialogItemPhotoIv.setImageBitmap(it)
                        imageByteArray = it.toByteArray()
                    }
                    1 -> {
                        binding.imageGalleryItem1.setImageBitmap(it)
                        additionalImagesArray[0].imageByteArray = it.toByteArray()
                    }
                    2 -> {
                        binding.imageGalleryItem2.setImageBitmap(it)
                        additionalImagesArray[1].imageByteArray = it.toByteArray()
                    }
                    3 -> {
                        binding.imageGalleryItem3.setImageBitmap(it)
                        additionalImagesArray[2].imageByteArray = it.toByteArray()
                    }
                }
                edittingImage = null
            } ?: run { // normal image select mode
                if (didSelectMainImage()) {
                    when (additionalImagesArray.size) {
                        0 -> {
                            binding.imageGalleryItem1.visibility = VISIBLE
                            binding.imageGalleryItem1.setImageBitmap(it)
                        }
                        1 -> {
                            binding.imageGalleryItem2.visibility = VISIBLE
                            binding.imageGalleryItem2.setImageBitmap(it)
                        }
                        2 -> {
                            binding.imageGalleryItem3.visibility = VISIBLE
                            binding.imageGalleryItem3.setImageBitmap(it)
                        }
                    }
                    additionalImagesArray.add(ProductImageSelection(imageByteArray = it.toByteArray()))
                    return@let
                }
                binding.dialogItemPhotoIv.setImageBitmap(it)
                imageByteArray = it.toByteArray()
            }
        }
    }

}
