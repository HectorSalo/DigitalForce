package com.skysam.hchirinos.digitalforce.ui.services.newService

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.dataClass.Service
import com.skysam.hchirinos.digitalforce.databinding.FragmentSecondNewServiceBinding
import com.skysam.hchirinos.digitalforce.ui.sales.newSale.NewSaleAdapter
import com.skysam.hchirinos.digitalforce.ui.sales.newSale.OnClick
import com.skysam.hchirinos.digitalforce.ui.services.ServicesViewModel
import java.text.DateFormat


class SecondFragment : Fragment(), OnClick {

    private var _binding: FragmentSecondNewServiceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesViewModel by activityViewModels()
    private lateinit var adapterItem: NewSaleAdapter
    private lateinit var service: Service
    private val products = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondNewServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterItem = NewSaleAdapter(products, this, false)
        binding.rvList.apply {
            setHasFixedSize(true)
            adapter = adapterItem
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        binding.btnSale.setOnClickListener {
            viewModel.addService(service)
            requireActivity().finish()
        }

        loadViewModels()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadViewModels() {
        viewModel.serviceToSend.observe(viewLifecycleOwner) {
            if (_binding != null) {
                service = it
                binding.tvNameCostumer.text = service.nameCustomer
                binding.tvLocationCostumer.text = service.location
                binding.tvDate.text = DateFormat.getDateInstance().format(service.date)
                binding.tvTotalIvaDolar.text = Classes.convertDoubleToString(service.total * 0.16)
                binding.tvTotalMontoDolar.text = Classes.convertDoubleToString(service.total * 1.16)
                products.clear()
                products.addAll(service.list)
                adapterItem.notifyDataSetChanged()

                if (service.invoice == 0) binding.tvInvoice.visibility = View.GONE
                else binding.tvInvoice.text = getString(R.string.text_invoice_item, service.invoice.toString())
            }
        }
    }

    override fun delete(product: Product) {

    }
}