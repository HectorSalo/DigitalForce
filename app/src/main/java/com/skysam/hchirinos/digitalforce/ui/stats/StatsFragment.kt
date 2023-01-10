package com.skysam.hchirinos.digitalforce.ui.stats

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Expense
import com.skysam.hchirinos.digitalforce.dataClass.Sale
import com.skysam.hchirinos.digitalforce.dataClass.Service
import com.skysam.hchirinos.digitalforce.databinding.FragmentStatsBinding
import java.util.*

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatsViewModel by activityViewModels()
    private val sales = mutableListOf<Sale>()
    private val expenses = mutableListOf<Expense>()
    private val services = mutableListOf<Service>()
    private var isByRange = true
    private var monthSelected = 0
    private var yearSelected = 0
    private lateinit var dateStart: Date
    private lateinit var dateFinal: Date

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        dateStart = calendar.time
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
        dateFinal = calendar.time
        monthSelected = calendar[Calendar.MONTH]
        yearSelected = calendar[Calendar.YEAR]

        binding.etDate.setText(getString(R.string.text_date_range,
            Classes.convertDateToString(dateStart), Classes.convertDateToString(dateFinal)))

        binding.etDate.setOnClickListener { selecDate() }

        binding.chipMonth.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isByRange = false
                configAdapter()
                binding.spinnerMonth.visibility = View.VISIBLE
                binding.spinnerYear.visibility = View.VISIBLE
                binding.tfDate.visibility = View.INVISIBLE
            }
        }
        binding.chipWeek.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isByRange = true
                binding.spinnerMonth.visibility = View.GONE
                binding.spinnerYear.visibility = View.GONE
                binding.tfDate.visibility = View.VISIBLE
                loadStats()
            }
        }
        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                monthSelected = position
                loadStats()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                yearSelected = 2022 + position
                loadStats()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun selecDate() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val calendar = Calendar.getInstance()

        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection: Pair<Long, Long> ->
            val timeZone = TimeZone.getDefault()
            val offset = timeZone.getOffset(Date().time) * -1
            calendar.timeInMillis = selection.first
            calendar.timeInMillis = calendar.timeInMillis + offset
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            dateStart = calendar.time
            calendar.timeInMillis = selection.second
            calendar.timeInMillis = calendar.timeInMillis + offset
            calendar[Calendar.HOUR_OF_DAY] = 23
            calendar[Calendar.MINUTE] = 59
            dateFinal = calendar.time
            binding.etDate.setText(getString(R.string.text_date_range,
                Classes.convertDateToString(dateStart), Classes.convertDateToString(dateFinal)))
            loadStats()
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }

    private fun configAdapter() {
        val listSpinnerMonth = listOf(*resources.getStringArray(R.array.months))
        val listSpinnerYear = listOf(*resources.getStringArray(R.array.years))

        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listSpinnerMonth)
        binding.spinnerMonth.apply {
            adapter = adapterUnits
            setSelection(monthSelected)
        }

        val adapterYear = ArrayAdapter(requireContext(), R.layout.layout_spinner, listSpinnerYear)
        binding.spinnerYear.apply {
            adapter = adapterYear
            setSelection(yearSelected - 2022)
        }
    }

    private fun loadViewModel() {
        viewModel.sales.observe(viewLifecycleOwner) {
            if (_binding != null) {
                sales.clear()
                sales.addAll(it)
                loadStats()
            }
        }
        viewModel.expenses.observe(viewLifecycleOwner) {
            if (_binding != null) {
                expenses.clear()
                expenses.addAll(it)
                loadStats()
            }
        }
        viewModel.services.observe(viewLifecycleOwner) {
            if (_binding != null){
                services.clear()
                services.addAll(it)
                loadStats()
            }
        }
    }

    private fun loadStats() {
        var totalSales = 0.0
        for (sale in sales) {
            if (isByRange) {
                if (sale.date.after(dateStart) && sale.date.before(dateFinal)) {
                    totalSales += sale.total
                }
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = sale.date
                if (calendar[Calendar.MONTH] == monthSelected && calendar[Calendar.YEAR] == yearSelected) {
                    totalSales += sale.total
                }
            }
        }
        var totalExpenses = 0.0
        for (expense in expenses) {
            if (isByRange) {
                if (expense.date.after(dateStart) && expense.date.before(dateFinal)) {
                    totalExpenses += expense.total
                }
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = expense.date
                if (calendar[Calendar.MONTH] == monthSelected && calendar[Calendar.YEAR] == yearSelected) {
                    totalExpenses += expense.total
                }
            }
        }
        var totalServices = 0.0
        for (service in services) {
            if (isByRange) {
                if (service.date.after(dateStart) && service.date.before(dateFinal)) {
                    totalServices += service.total
                }
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = service.date
                if (calendar[Calendar.MONTH] == monthSelected && calendar[Calendar.YEAR] == yearSelected) {
                    totalServices += service.total
                }
            }
        }

        binding.tvSale.text = getString(R.string.text_amount_add_graph,
            Classes.convertDoubleToString(totalSales))
        binding.tvService.text = getString(R.string.text_amount_add_graph,
            Classes.convertDoubleToString(totalServices))
        binding.tvExpense.text = getString(R.string.text_amount_rest_graph,
            Classes.convertDoubleToString(totalExpenses))

        val total = totalSales + totalServices - totalExpenses
        if (total > 0.0) {
            binding.tvTotal.text = getString(R.string.text_total_graph_superavit,
                Classes.convertDoubleToString(total))
            binding.tvTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_normal))
        }  else {
            binding.tvTotal.text = getString(R.string.text_total_graph_deficit,
                Classes.convertDoubleToString(total))
            binding.tvTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_normal))
        }
        if (total == 0.0) {
            binding.tvTotal.text = getString(R.string.text_total_graph_zero)
            binding.tvTotal.setTextColor(requireContext().resolveColorAttr(android.R.attr.textColorSecondary))
        }
        binding.progressBar.visibility = View.GONE
    }

    @ColorInt
    fun Context.resolveColorAttr(@AttrRes colorAttr: Int): Int {
        val resolvedAttr = resolveThemeAttr(colorAttr)
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
        return ContextCompat.getColor(requireContext(), colorRes)
    }

    private fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue
    }

}