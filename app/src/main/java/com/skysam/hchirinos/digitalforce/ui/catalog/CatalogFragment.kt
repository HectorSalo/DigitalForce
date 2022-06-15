package com.skysam.hchirinos.digitalforce.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.digitalforce.common.WrapLayoutManager
import com.skysam.hchirinos.digitalforce.dataClass.Product
import com.skysam.hchirinos.digitalforce.databinding.FragmentCatalogBinding

class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatalogViewModel by activityViewModels()
    private lateinit var catalogAdapter: CatalogAdapter
    private lateinit var wrapLayoutManager: WrapLayoutManager
    private val products: MutableList<Product> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        catalogAdapter = CatalogAdapter(products)
        wrapLayoutManager = WrapLayoutManager(requireContext(),
            RecyclerView.VERTICAL, false)
        binding.rvProducts.apply {
            setHasFixedSize(true)
            adapter = catalogAdapter
            layoutManager = wrapLayoutManager
        }

        binding.fab.setOnClickListener {
            val addProductDialog = AddProductDialog()
            addProductDialog.show(requireActivity().supportFragmentManager, tag)
        }

        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.products.observe(viewLifecycleOwner) {
            if (_binding != null) {
                binding.progressBar.visibility = View.GONE
                if (it.isEmpty()) {
                    binding.tvListEmpty.visibility = View.VISIBLE
                    binding.rvProducts.visibility = View.GONE
                } else {
                    products.clear()
                    products.addAll(it)
                    catalogAdapter.notifyItemRangeInserted(0, products.size)
                    binding.tvListEmpty.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                }
            }
        }
    }
}