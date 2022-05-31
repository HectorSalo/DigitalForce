package com.skysam.hchirinos.digitalforce.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.digitalforce.dataClass.Customer
import com.skysam.hchirinos.digitalforce.databinding.FragmentCustomersBinding

class CustomersFragment : Fragment() {

    private var _binding: FragmentCustomersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomersViewModel by activityViewModels()
    private lateinit var adapterCostumer: CustomersAdapter
    private val customers = mutableListOf<Customer>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomersBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterCostumer = CustomersAdapter(customers)
        binding.rvCostumers.apply {
            setHasFixedSize(true)
            adapter = adapterCostumer
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        binding.floatingActionButton.hide()
                    } else {
                        binding.floatingActionButton.show()
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })

            loadViewModel()
        }

        binding.floatingActionButton.setOnClickListener {
            /*val dialog = AddCustomerDialog()
            dialog.show(requireActivity().supportFragmentManager, tag)*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.floatingActionButton.show()
    }

    override fun onPause() {
        super.onPause()
        binding.floatingActionButton.hide()
    }

    private fun loadViewModel() {
        viewModel.customers.observe(viewLifecycleOwner) {
            if (_binding != null) {
                binding.progressBar.visibility = View.GONE
                if (it.isEmpty()) {
                    binding.tvListEmpty.visibility = View.VISIBLE
                    binding.rvCostumers.visibility = View.GONE
                } else {
                    customers.clear()
                    customers.addAll(it)
                    adapterCostumer.notifyItemRangeInserted(0, customers.size)
                    binding.tvListEmpty.visibility = View.GONE
                    binding.rvCostumers.visibility = View.VISIBLE
                }
            }
        }
    }

}