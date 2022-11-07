package com.skysam.hchirinos.digitalforce.ui.sales

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.dataClass.Sale
import com.skysam.hchirinos.digitalforce.databinding.FragmentSalesBinding
import com.skysam.hchirinos.digitalforce.ui.sales.newSale.NewSaleActivity

class SalesFragment : Fragment(), OnClick {

    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private val sales = mutableListOf<Sale>()
    private val viewModel: SalesViewModel by activityViewModels()
    private lateinit var saleAdapter: SaleAdapter

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

        saleAdapter = SaleAdapter(sales, this)
        binding.rvSales.apply {
            setHasFixedSize(true)
            adapter = saleAdapter
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        binding.fab.hide()
                    } else {
                        binding.fab.show()
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(requireContext(), NewSaleActivity::class.java))
        }

        loadViewModels()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadViewModels() {
         viewModel.sales.observe(viewLifecycleOwner) {
             if (_binding != null) {
                 binding.progressBar.visibility = View.GONE
                 if (it.isNotEmpty()) {
                     sales.clear()
                     sales.addAll(it)
                     saleAdapter.notifyDataSetChanged()
                     binding.rvSales.visibility = View.VISIBLE
                     binding.tvListEmpty.visibility = View.GONE
                 } else {
                     binding.rvSales.visibility = View.GONE
                     binding.tvListEmpty.visibility = View.VISIBLE
                 }
             }
         }
    }

    override fun view(sale: Sale) {
        viewModel.viewSale(sale)
        val viewSaleDialog = ViewSaleDialog()
        viewSaleDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun edit(sale: Sale) {

    }

    override fun delete(sale: Sale) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_delete_dialog))
            .setPositiveButton(R.string.text_delete) { _, _ ->
                viewModel.deleteSale(sale)
            }
            .setNegativeButton(R.string.text_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }
}