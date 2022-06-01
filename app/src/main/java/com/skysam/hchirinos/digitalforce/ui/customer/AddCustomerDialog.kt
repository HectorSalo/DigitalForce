package com.skysam.hchirinos.digitalforce.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Customer
import com.skysam.hchirinos.digitalforce.databinding.DialogAddCustomerBinding

/**
 * Created by Hector Chirinos on 31/05/2022.
 */

class AddCustomerDialog: DialogFragment() {
    private var _binding: DialogAddCustomerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomersViewModel by activityViewModels()
    private val customers = mutableListOf<Customer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etName.doAfterTextChanged { binding.tfName.error = null }
        binding.etId.doAfterTextChanged { binding.tfId.error = null }
        binding.etLocation.doAfterTextChanged { binding.tfLocation.error = null }
        val listUnits = listOf(*resources.getStringArray(R.array.identificador))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        binding.spinner.adapter = adapterUnits
        binding.btnSave.setOnClickListener { validateCostumer() }
        binding.btnCancel.setOnClickListener { dialog?.dismiss() }

        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.customers.observe(this.requireActivity()) {
            if (_binding != null) {
                customers.clear()
                customers.addAll(it)
            }
        }
    }

    private fun validateCostumer() {
        binding.tfName.error = null
        binding.tfId.error = null
        binding.tfLocation.error = null

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
        val location = binding.etLocation.text.toString()
        if (location.isEmpty()) {
            binding.tfLocation.error = getString(R.string.error_field_empty)
            binding.etLocation.requestFocus()
            return
        }
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
        var identifierExists = false
        for (cos in customers) {
            val iden = "${cos.typeIdentifier}${cos.numberIdentifier}"
            if (iden == "$identifier$rif") {
                if (cos.name != name) {
                    binding.tfId.error = getString(R.string.error_identifier_exists)
                    binding.etId.requestFocus()
                    identifierExists = true
                    break
                }
            }
        }
        if (identifierExists) return
        val locations = mutableListOf<String>()
        locations.add(location)

        Classes.close(binding.root)
        val customer = Customer("", name, identifier, rif.toInt(), locations)
        viewModel.addCustomer(customer)
        dialog?.dismiss()
    }
}