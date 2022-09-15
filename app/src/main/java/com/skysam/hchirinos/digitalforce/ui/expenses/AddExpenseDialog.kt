package com.skysam.hchirinos.digitalforce.ui.expenses

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Expense
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.databinding.DialogAddExpenseBinding
import com.skysam.hchirinos.digitalforce.ui.common.ExitDialog
import com.skysam.hchirinos.digitalforce.ui.common.OnClickExit
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos on 10/07/2022.
 */

class AddExpenseDialog: DialogFragment(), TextWatcher, OnClickExit, OnClickList {
    private var _binding: DialogAddExpenseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpensesViewModel by activityViewModels()
    private lateinit var adapterItem: ItemListAdapter
    private lateinit var dateSelected: Date
    private val productsInList = mutableListOf<Product>()
    private lateinit var valueWeb: String
    private var total = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddExpenseBinding.inflate(inflater, container, false)
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
        adapterItem = ItemListAdapter(productsInList, this)
        binding.rvList.apply {
            setHasFixedSize(true)
            adapter = adapterItem
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
        binding.etRate.addTextChangedListener(this)
        dateSelected = Date()
        binding.etDate.setText(DateFormat.getDateInstance().format(dateSelected))
        binding.etDate.setOnClickListener { selecDate() }

        binding.etProduct.doAfterTextChanged {
            if (it.isNullOrEmpty()) binding.fabAdd.hide() else binding.fabAdd.show()
        }

        binding.fabCancel.setOnClickListener {
            getOut()
        }

        binding.fabSave.setOnClickListener { validateData() }

        binding.fabAdd.setOnClickListener {
            var exists = false
            val nameSelected = binding.etProduct.text.toString()
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
                    id = "",
                    nameSelected,
                    image = "",
                    pdf = null
                )
                binding.etProduct.setText("")
                Classes.close(binding.root)
                viewModel.addProductInList(product)
            }
        }

        loadViewModel()
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.valueWeb.observe(viewLifecycleOwner) {
            if (_binding != null) {
                valueWeb = it
                binding.tfRate.hint = getString(R.string.text_rate)
                binding.etRate.setText(it)
                if (it == "1,00") {
                    binding.tfRate.error = getString(R.string.error_rate)
                    binding.etRate.doAfterTextChanged { binding.tfRate.error = null }
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
        viewModel.priceTotal.observe(viewLifecycleOwner) {
            if (_binding != null) {
                total = it
                binding.tvTotal.text = getString(
                    R.string.text_total_dolar_amount,
                    Classes.convertDoubleToString(it)
                )
            }
        }
    }

    private fun validateData() {
        binding.tfDate.error = null

        val dateSelectedS = binding.etDate.text.toString()
        if (dateSelectedS.isEmpty()) {
            binding.tfDate.error = getString(R.string.error_field_empty)
            binding.etDate.requestFocus()
            return
        }
        var rate = binding.etRate.text.toString()
        if (rate.isEmpty()) {
            binding.tfRate.error = getString(R.string.error_field_empty)
            binding.etRate.requestFocus()
            return
        }
        if (rate == "0,00") {
            binding.tfRate.error = getString(R.string.error_price_zero)
            binding.etRate.requestFocus()
            return
        }
        rate = rate.replace(".", "").replace(",", ".")

        if (total == 0.0) {
            Toast.makeText(requireContext(), getString(R.string.error_price_zero), Toast.LENGTH_SHORT).show()
            return
        }

        val expense = Expense(
            "",
            productsInList,
            total,
            dateSelected,
            rate.toDouble()
        )
        viewModel.addExpense(expense)
        viewModel.clearFields()
        dialog?.dismiss()
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

    override fun deleteItem(product: Product) {
        viewModel.removeProductInList(product)
    }

    override fun editItem(product: Product) {
        val position = productsInList.indexOf(product)
        val rate = valueWeb.replace(".", "").replace(",", ".")
        val editProductDialog = EditProductDialog(product, position, rate.toDouble())
        editProductDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onClickExit() {
        viewModel.clearFields()
        dialog?.dismiss()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        var cadena = s.toString()
        cadena = cadena.replace(",", "").replace(".", "")
        val cantidad: Double = cadena.toDouble() / 100
        cadena = Classes.convertDoubleToString(cantidad)

        if (s.toString() == binding.etRate.text.toString()) {
            binding.etRate.removeTextChangedListener(this)
            binding.etRate.setText(cadena)
            binding.etRate.setSelection(cadena.length)
            binding.etRate.addTextChangedListener(this)
            valueWeb = cadena
        }
    }
}