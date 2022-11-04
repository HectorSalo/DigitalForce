package com.skysam.hchirinos.digitalforce.ui.sales.newSale

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Customer
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.dataClass.Sale
import com.skysam.hchirinos.digitalforce.databinding.FragmentNewSaleFirstBinding
import com.skysam.hchirinos.digitalforce.ui.common.ExitDialog
import com.skysam.hchirinos.digitalforce.ui.common.OnClickExit
import com.skysam.hchirinos.digitalforce.ui.sales.SalesViewModel
import java.text.DateFormat
import java.util.*


class FirstFragment : Fragment(), OnClickExit, TextWatcher, OnClick {

    private var _binding: FragmentNewSaleFirstBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()
    private lateinit var adapterItem: NewSaleAdapter
    private lateinit var dateSelected: Date
    private val customers = mutableListOf<Customer>()
    private val productsInList = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewSaleFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getOut()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        dateSelected = Date()
        binding.etDate.setText(DateFormat.getDateInstance().format(Date()))
        binding.etPrice.addTextChangedListener(this)
        binding.etQuantity.doAfterTextChanged { binding.tfQuantity.error = null }
        binding.etProduct.doAfterTextChanged { binding.tfProduct.error = null }
        binding.etPrice.doAfterTextChanged { binding.tfPrice.error = null }

        adapterItem = NewSaleAdapter(productsInList, this, true)
        binding.rvList.apply {
            setHasFixedSize(true)
            adapter = adapterItem
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        binding.spCustomer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (position > 0) {
                    val customer = customers[position - 1]
                    val adapterLocations =
                        ArrayAdapter(requireContext(), R.layout.layout_spinner, customer.locations)
                    binding.spLocation.adapter = adapterLocations
                    binding.spLocation.visibility = View.VISIBLE
                } else {
                    binding.spLocation.visibility = View.INVISIBLE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        binding.btnAdd.setOnClickListener { addProduct() }
        binding.btnExit.setOnClickListener { getOut() }
        binding.etDate.setOnClickListener { selecDate() }
        binding.btnTotal.setOnClickListener { validateData() }

        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadViewModel() {
        viewModel.customers.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isEmpty()) {
                    binding.spCustomer.visibility = View.GONE
                    binding.spLocation.visibility = View.GONE
                } else {
                    customers.clear()
                    customers.addAll(it)
                    val names = mutableListOf<String>()
                    names.add("Seleccione Cliente")
                    for (cus in customers) {
                        names.add(cus.name)
                    }
                    val adapterCustomers =
                        ArrayAdapter(requireContext(), R.layout.layout_spinner, names)
                    binding.spCustomer.adapter = adapterCustomers
                }
            }
        }
        viewModel.productsInList.observe(viewLifecycleOwner) {
            if (_binding != null) {
                productsInList.clear()
                productsInList.addAll(it)
                adapterItem.notifyDataSetChanged()
            }
        }
    }

    private fun selecDate() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val calendar = Calendar.getInstance()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection: Long? ->
            calendar.timeInMillis = selection!!
            val timeZone = TimeZone.getDefault()
            val offset = timeZone.getOffset(Date().time) * -1
            calendar.timeInMillis = calendar.timeInMillis + offset
            dateSelected = calendar.time
            binding.etDate.setText(DateFormat.getDateInstance().format(dateSelected))
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onClickExit() {
        requireActivity().finish()
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

    override fun delete(product: Product) {
        viewModel.removeProductInList(product)
    }

    private fun addProduct() {
        binding.tfProduct.error = null
        binding.tfPrice.error = null
        binding.tfQuantity.error = null

        var exists = false
        val nameSelected = binding.etProduct.text.toString()
        val quantityS = binding.etQuantity.text.toString()
        val priceS = binding.etPrice.text.toString()

        if (nameSelected.isEmpty()) {
            binding.tfProduct.error = getString(R.string.error_field_empty)
            binding.etProduct.requestFocus()
            return
        }
        if (quantityS.isEmpty()) {
            binding.tfQuantity.error = getString(R.string.error_field_empty)
            binding.etQuantity.requestFocus()
            return
        }
        for (prod in productsInList) {
            if (prod.name == nameSelected){
                exists = true
                Toast.makeText(requireContext(), getString(R.string.product_added), Toast.LENGTH_SHORT).show()
                binding.rvList.scrollToPosition(productsInList.indexOf(prod))
                break
            }
        }
        if (!exists) {
            val product = Product(
                "",
                nameSelected,
                Classes.convertStringToDoublePrice(priceS),
                quantityS.toInt(),
                "",
                 null
            )
            binding.etProduct.setText("")
            binding.etQuantity.setText(getString(R.string.text_quantity_init))
            binding.etPrice.setText(getString(R.string.text_price_init))
            binding.etProduct.requestFocus()
            Classes.close(binding.root)
            viewModel.addProductInList(product)
        }
    }

    private fun validateData() {
        if (binding.spCustomer.selectedItemPosition == 0) {
            Toast.makeText(requireContext(), getString(R.string.error_without_customer), Toast.LENGTH_SHORT).show()
            return
        }
        if (productsInList.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.error_without_products), Toast.LENGTH_SHORT).show()
            return
        }

        val invoice = if (binding.etInvoice.text.toString().isEmpty()) 0
        else binding.etInvoice.text.toString().toInt()

        var total = 0.0
        for (pro in productsInList) {
            total += (pro.price * pro.quantity)
        }

        val sale = Sale(
            "",
            binding.spCustomer.selectedItem.toString(),
            binding.spLocation.selectedItem.toString(),
            invoice,
            productsInList,
            total,
            dateSelected
        )
        viewModel.sendSale(sale)
        findNavController().navigate(R.id.action_First2Fragment_to_Second2Fragment)
    }
}