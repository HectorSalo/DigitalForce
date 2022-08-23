package com.skysam.hchirinos.digitalforce.ui.customer

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Customer
import com.skysam.hchirinos.digitalforce.databinding.DialogAddCustomerBinding

/**
 * Created by Hector Chirinos on 01/06/2022.
 */

class UpdateCustomerDialog: DialogFragment() {
    private var _binding: DialogAddCustomerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomersViewModel by activityViewModels()
    private val customers = mutableListOf<Customer>()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private lateinit var customer: Customer

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddCustomerBinding.inflate(layoutInflater)

        val listUnits = listOf(*resources.getStringArray(R.array.identificador))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        binding.spinner.adapter = adapterUnits
        binding.tfLocation.visibility = View.GONE
        binding.btnCancel.visibility = View.GONE
        binding.btnSave.visibility = View.GONE

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_edit_customer))
            .setView(binding.root)
            .setPositiveButton(R.string.text_save, null)
            .setNegativeButton(R.string.text_cancel, null)

        val dialog = builder.create()
        dialog.show()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateCostumer() }

        loadViewModel()

        return dialog
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.customerToUpdate.observe(requireParentFragment()) {
            customer = it
            if (_binding != null) {
                loadView()
            }
        }
        viewModel.customers.observe(requireActivity()) {
            if (_binding != null) {
                customers.clear()
                customers.addAll(it)
            }
        }
    }

    private fun loadView() {
        binding.etName.setText(customer.name)
        when(customer.typeIdentifier) {
            "J-" -> binding.spinner.setSelection(0)
            "V-" -> binding.spinner.setSelection(1)
            "E-" -> binding.spinner.setSelection(2)
            "G-" -> binding.spinner.setSelection(3)
        }
        binding.etId.setText(customer.numberIdentifier.toString())
    }

    private fun validateCostumer() {
        binding.tfName.error = null
        binding.tfId.error = null

        val name = binding.etName.text.toString()
        if (name.isEmpty()) {
            binding.tfName.error = getString(R.string.error_field_empty)
            binding.etName.requestFocus()
            return
        }
        val rif = binding.etId.text.toString()
        if (rif.isEmpty()) {
            binding.tfId.error = getString(R.string.error_field_empty)
            binding.etId.requestFocus()
            return
        }
        val identifier = binding.spinner.selectedItem.toString()
        if (name != customer.name) {
            var costumerExists = false
            for (cos in customers) {
                if (cos.name == name) {
                    binding.tfName.error = getString(R.string.error_customer_exists)
                    binding.etName.requestFocus()
                    costumerExists = true
                    break
                }
            }
            if (costumerExists) return
        }
        val iden = "${customer.typeIdentifier}${customer.numberIdentifier}"
        if ("$identifier$rif" != iden) {
            var identifierExists = false
            for (cos in customers) {
                val iden2 = "${cos.typeIdentifier}${cos.numberIdentifier}"
                if (iden2 == "$identifier$rif") {
                    if (cos.name != name) {
                        binding.tfId.error = getString(R.string.error_identifier_exists)
                        binding.etId.requestFocus()
                        identifierExists = true
                        break
                    }
                }
            }
            if (identifierExists) return
        }

        Classes.close(binding.root)
        /*val customer1 = Customer(customer.id, name, identifier, rif.toInt(), customer.locations)
        viewModel.updateCustomer(customer1)*/
        dialog?.dismiss()
    }
}