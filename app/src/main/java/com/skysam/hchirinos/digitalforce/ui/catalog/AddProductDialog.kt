package com.skysam.hchirinos.digitalforce.ui.catalog

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.databinding.DialogAddProductBinding

/**
 * Created by Hector Chirinos on 06/06/2022.
 */

class AddProductDialog: DialogFragment() {
    private var _binding: DialogAddProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatalogViewModel by activityViewModels()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private var image: String? = null
    private lateinit var name: String
    private var price = 0.0
    private val products = mutableListOf<Product>()

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            goGallery()
        } else {
            Toast.makeText(requireContext(), getString(R.string.txt_error_permiso_lectura), Toast.LENGTH_SHORT).show()
        }
    }

    private val requestIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            showImage(result.data!!)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddProductBinding.inflate(layoutInflater)

        viewModel.products.observe(this.requireActivity()) {
            if (_binding != null) {
                products.clear()
                products.addAll(it)
            }
        }

        binding.etName.doAfterTextChanged { binding.tfName.error = null }

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_add_producto_dialog))
            .setView(binding.root)
            .setPositiveButton(R.string.text_save, null)
            .setNegativeButton(R.string.text_cancel, null)

        val dialog = builder.create()
        dialog.show()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateData() }

        binding.ivImage.setOnClickListener { requestPermission() }
        return dialog
    }


    private fun validateData() {
        binding.tfName.error = null
        name = binding.etName.text.toString().trim()
        val priceS = binding.etPrice.text.toString().trim()
        if (name.isEmpty()) {
            binding.tfName.error = getString(R.string.error_field_empty)
            binding.etName.requestFocus()
            return
        }
        for (prod in products) {
            if (prod.name.equals(name, true)) {
                binding.tfName.error = getString(R.string.error_name_exists)
                binding.etName.requestFocus()
                return
            }
        }
        if (priceS.isNotEmpty()) price = priceS.toDouble()
        Classes.close(binding.root)
        buttonNegative.isEnabled = false
        buttonPositive.isEnabled = false
        binding.ivImage.setOnClickListener(null)
        dialog?.setCancelable(false)

        if (image != null) {
            viewModel.uploadImage(Uri.parse(image)).observe(this.requireActivity()) {
                if (_binding != null) {
                    if (it.equals(getString(R.string.error_data))) {
                        binding.progressBar.visibility = View.GONE
                        binding.tvProgress.visibility = View.GONE
                        buttonNegative.isEnabled = false
                        buttonPositive.isEnabled = false
                        binding.ivImage.setOnClickListener { requestPermission() }
                        dialog?.setCancelable(true)
                        Toast.makeText(requireContext(), getString(R.string.error_upload_image), Toast.LENGTH_LONG).show()
                    } else {
                        if (it.contains("https")) {
                            image = it
                            saveProduct()
                        } else {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.tvProgress.visibility = View.VISIBLE
                            binding.tvProgress.text = getString(R.string.text_progress_load_image, it)
                        }
                    }
                }
            }
        } else {
            image = ""
            saveProduct()
        }
    }

    private fun saveProduct() {
        val priceF = if (price > 0) price else 1.0
        val product = Product(
            "",
            name,
            priceF,
            image = image!!
        )
        viewModel.saveProduct(product)
        dismiss()
    }

    private fun requestPermission() {
        if (checkPermission()) {
            goGallery()
            return
        }
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun goGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        requestIntentLauncher.launch(intent)
    }

    private fun showImage(it: Intent) {
        image = it.dataString!!
        val sizeImagePreview = resources.getDimensionPixelSize(R.dimen.size_image_dialog_add_product)
        val bitmap = Classes.reduceBitmap(image, sizeImagePreview, sizeImagePreview)

        if (bitmap != null) {
            binding.ivImage.setImageBitmap(bitmap)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}