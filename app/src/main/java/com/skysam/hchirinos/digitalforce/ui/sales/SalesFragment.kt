package com.skysam.hchirinos.digitalforce.ui.sales

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.digitalforce.dataClass.Sale
import com.skysam.hchirinos.digitalforce.databinding.FragmentSalesBinding
import com.skysam.hchirinos.digitalforce.ui.sales.newSale.NewSaleActivity

class SalesFragment : Fragment() {

    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private val sales = mutableListOf<Sale>()
    private val viewModel: SalesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            startActivity(Intent(requireContext(), NewSaleActivity::class.java))
        }

        loadViewModels()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModels() {
         viewModel.sales.observe(viewLifecycleOwner) {
             if (_binding != null) {
                 binding.progressBar.visibility = View.GONE
                 if (it.isNotEmpty()) {
                     sales.clear()
                     sales.addAll(it)
                 } else {
                     binding.rvSales.visibility = View.GONE
                     binding.tvListEmpty.visibility = View.VISIBLE
                 }
             }
         }
    }
}