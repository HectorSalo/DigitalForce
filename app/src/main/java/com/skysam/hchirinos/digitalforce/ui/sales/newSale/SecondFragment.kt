package com.skysam.hchirinos.digitalforce.ui.sales.newSale

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
import com.skysam.hchirinos.digitalforce.dataClass.Sale
import com.skysam.hchirinos.digitalforce.databinding.FragmentNewSaleSecondBinding
import com.skysam.hchirinos.digitalforce.ui.sales.SalesViewModel
import java.text.DateFormat


class SecondFragment : Fragment(), OnClick {

    private var _binding: FragmentNewSaleSecondBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()
    private lateinit var adapterItem: NewSaleAdapter
    private lateinit var sale: Sale
    private val products = mutableListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewSaleSecondBinding.inflate(inflater, container, false)
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
            viewModel.addSale(sale)
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
        viewModel.saleToSend.observe(viewLifecycleOwner) {
            if (_binding != null) {
                sale = it
                binding.tvNameCostumer.text = sale.nameCustomer
                binding.tvLocationCostumer.text = sale.location
                binding.tvDate.text = DateFormat.getDateInstance().format(sale.date)
                binding.tvTotalIvaDolar.text = Classes.convertDoubleToString(sale.total * 0.16)
                binding.tvTotalMontoDolar.text = Classes.convertDoubleToString(sale.total * 1.16)
                products.clear()
                products.addAll(sale.listProducts)
                adapterItem.notifyDataSetChanged()

                if (sale.invoice == 0) binding.tvInvoice.visibility = View.GONE
                else binding.tvInvoice.text = getString(R.string.text_invoice_item, sale.invoice.toString())
            }
        }
    }

    override fun delete(product: Product) {

    }
}