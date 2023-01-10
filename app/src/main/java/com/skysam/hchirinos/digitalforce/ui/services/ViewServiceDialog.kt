package com.skysam.hchirinos.digitalforce.ui.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Service
import com.skysam.hchirinos.digitalforce.databinding.DialogViewExpenseBinding
import com.skysam.hchirinos.digitalforce.ui.expenses.ViewExpenseAdapter
import java.text.DateFormat

/**
 * Created by Hector Chirinos on 10/01/2023.
 */

class ViewServiceDialog: DialogFragment() {
    private var _binding: DialogViewExpenseBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ViewExpenseAdapter
    private lateinit var service: Service
    private val viewModel: ServicesViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogViewExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.VISIBLE
        viewModel.serviceToView.observe(this.requireActivity()) {
            if (_binding != null) {
                service = it
                loadData()
            }
        }
    }

    private fun loadData() {
        binding.tvDate.text = DateFormat.getDateInstance().format(service.date)
        if (service.invoice > 0) binding.tvInvoice.text = getString(
            R.string.text_invoice_item,
            service.invoice.toString()) else binding.tvInvoice.visibility = View.GONE
        if (service.rate > 0) {
            binding.tvRate.text = getString(
                R.string.text_rate_view,
                Classes.convertDoubleToString(service.rate))
        } else binding.tvRate.visibility = View.GONE
        binding.tvTotal.text = getString(
            R.string.text_total_dolar_amount,
            Classes.convertDoubleToString(service.total))
        adapter = ViewExpenseAdapter(service.list.sortedWith(compareBy { it.name }).toMutableList())
        binding.rvList.adapter = adapter
        binding.rvList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}