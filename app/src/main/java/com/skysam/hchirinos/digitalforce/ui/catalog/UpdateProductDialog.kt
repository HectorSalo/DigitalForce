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
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.databinding.DialogAddProductBinding
import java.util.*

/**
 * Created by Hector Chirinos on 07/07/2022.
 */

class UpdateProductDialog: DialogFragment(), TextWatcher {
    private var _binding: DialogAddProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatalogViewModel by activityViewModels()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private var image: String? = null
    private var pdf: String? = null
    private lateinit var name: String
    private var price = 0.0
    private val products = mutableListOf<Product>()
    private lateinit var product: Product

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

    private val requestIntentPdf = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            pdf = result.data!!.dataString
            savePdf()
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

        viewModel.productToEdit.observe(this.requireActivity()) {
            if (_binding != null) {
                product = it
                name = product.name
                image = product.image
                price = product.price
                binding.etName.setText(it.name)
                binding.etPrice.setText(Classes.convertDoubleToString(it.price))
                Glide.with(requireContext())
                    .load(it.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_add_a_photo_232)
                    .into(binding.ivImage)
                if (it.pdf != null) {
                    pdf = it.pdf
                    binding.ivPdf.setImageResource(R.drawable.ic_file_check_24)
                    binding.tvProgressPdf.visibility = View.VISIBLE
                    binding.tvProgressPdf.text = getString(R.string.text_pdf_load)
                }
            }
        }

        binding.etName.doAfterTextChanged { binding.tfName.error = null }
        binding.etPrice.addTextChangedListener(this)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_edit_producto_dialog, product.name))
            .setView(binding.root)
            .setPositiveButton(R.string.text_update, null)
            .setNegativeButton(R.string.text_cancel, null)

        val dialog = builder.create()
        dialog.show()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateData() }

        binding.ivImage.setOnClickListener { requestPermission() }
        binding.ivPdf.setOnClickListener { uploadPdf() }
        return dialog
    }


    private fun validateData() {
        binding.tfName.error = null
        name = binding.etName.text.toString().trim()
        var priceS = binding.etPrice.text.toString().trim()
        if (name.isEmpty()) {
            binding.tfName.error = getString(R.string.error_field_empty)
            binding.etName.requestFocus()
            return
        }
        for (prod in products) {
            if (prod.name.equals(name, true) && prod.name != product.name) {
                binding.tfName.error = getString(R.string.error_name_exists)
                binding.etName.requestFocus()
                return
            }
        }
        if (priceS.isEmpty()) {
            binding.tfPrice.error = getString(R.string.error_field_empty)
            binding.etPrice.requestFocus()
            return
        }
        if (priceS == "0,00") {
            binding.tfPrice.error = getString(R.string.error_price_zero)
            binding.etPrice.requestFocus()
            return
        }
        priceS = priceS.replace(".", "").replace(",", ".")
        price = priceS.toDouble()
        Classes.close(binding.root)
        buttonNegative.isEnabled = false
        buttonPositive.isEnabled = false
        binding.ivImage.setOnClickListener(null)
        dialog?.setCancelable(false)

        if (image != null && image != product.image) {
            viewModel.uploadImage(Uri.parse(image)).observe(this.requireActivity()) {
                if (_binding != null) {
                    if (it.equals(getString(R.string.error_data))) {
                        binding.progressBar.visibility = View.GONE
                        binding.tvProgressImage.visibility = View.GONE
                        buttonNegative.isEnabled = false
                        buttonPositive.isEnabled = false
                        binding.ivImage.setOnClickListener { requestPermission() }
                        dialog?.setCancelable(true)
                        Toast.makeText(requireContext(), getString(R.string.error_upload_image), Toast.LENGTH_LONG).show()
                    } else {
                        if (it.contains("https")) {
                            if (product.image.isNotEmpty()) viewModel.deleteOldImage(product.image)
                            image = it
                            updateProduct()
                        } else {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.tvProgressImage.visibility = View.VISIBLE
                            binding.tvProgressImage.text = getString(R.string.text_progress_load, it)
                        }
                    }
                }
            }
        } else {
            updateProduct()
        }
    }

    private fun updateProduct() {
        if (name != product.name || image != product.image ||
            price != product.price || pdf != product.pdf) {
            val product = Product(
                product.id,
                name,
                price,
                image = image!!,
                pdf = pdf
            )
            viewModel.updateProduct(product)
        }
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

    private fun uploadPdf() {
        val intent = Intent().setAction(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        requestIntentPdf.launch(intent)
    }

    private fun savePdf() {
        viewModel.uploadPdf(Uri.parse(pdf)).observe(this.requireActivity()) {
            if (_binding != null) {
                if (it.equals(getString(R.string.error_data))) {
                    binding.progressBar.visibility = View.GONE
                    binding.tvProgressPdf.visibility = View.GONE
                    buttonNegative.isEnabled = true
                    buttonPositive.isEnabled = true
                    binding.ivImage.setOnClickListener { requestPermission() }
                    dialog?.setCancelable(true)
                    Toast.makeText(requireContext(), getString(R.string.error_upload_pdf), Toast.LENGTH_LONG).show()
                } else {
                    if (it.contains("https")) {
                        pdf = it
                        binding.progressBar.visibility = View.GONE
                        binding.ivPdf.setImageResource(R.drawable.ic_file_check_24)
                        binding.tvProgressPdf.text = getString(R.string.text_pdf_load)
                        buttonPositive.isEnabled = true
                        buttonNegative.isEnabled = true
                        dialog?.setCancelable(true)
                    } else {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.tvProgressPdf.visibility = View.VISIBLE
                        buttonPositive.isEnabled = false
                        buttonNegative.isEnabled = false
                        dialog?.setCancelable(false)
                        binding.tvProgressPdf.text = getString(R.string.text_progress_load, it)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        var cadena = s.toString()
        cadena = cadena.replace(",", "").replace(".", "")
        val cantidad: Double = cadena.toDouble() / 100
        cadena = String.format(Locale.GERMANY, "%,.2f", cantidad)

        if (s.toString() == binding.etPrice.text.toString()) {
            binding.etPrice.removeTextChangedListener(this)
            binding.etPrice.setText(cadena)
            binding.etPrice.setSelection(cadena.length)
            binding.etPrice.addTextChangedListener(this)
        }
    }
}