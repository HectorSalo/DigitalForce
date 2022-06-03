package com.skysam.hchirinos.digitalforce.ui.customer

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.WrapLayoutManager
import com.skysam.hchirinos.digitalforce.dataClass.Customer
import com.skysam.hchirinos.digitalforce.databinding.FragmentCustomersBinding

class CustomersFragment : Fragment(), OnClick {

    private var _binding: FragmentCustomersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CustomersViewModel by activityViewModels()
    private lateinit var adapterCostumer: CustomersAdapter
    private lateinit var wrapLayoutManager: WrapLayoutManager
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

        adapterCostumer = CustomersAdapter(customers, this)
        wrapLayoutManager = WrapLayoutManager(requireContext(),
            RecyclerView.VERTICAL, false)
        binding.rvCostumers.apply {
            setHasFixedSize(true)
            adapter = adapterCostumer
            layoutManager = wrapLayoutManager
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
            val dialog = AddCustomerDialog()
            dialog.show(requireActivity().supportFragmentManager, tag)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        //inflater.inflate(R.menu.main, menu)
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

    override fun deleteLocation(customer: Customer) {
        val locationsToDelete = mutableListOf<String>()
        val arrayLocations = customer.locations.toTypedArray()
        val arrayChecked = BooleanArray(customer.locations.size)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_delete_locations))
            .setMultiChoiceItems(arrayLocations, arrayChecked) { _, which, isChecked ->
                if (isChecked) {
                    locationsToDelete.add(arrayLocations[which])
                } else {
                    locationsToDelete.remove(arrayLocations[which])
                }
            }
            .setPositiveButton(R.string.text_delete, null)
            .setNegativeButton(R.string.text_cancel, null)

        val dialog = builder.create()
        dialog.show()
        val buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener {
            if (locationsToDelete.size == customer.locations.size) {
                Toast.makeText(requireContext(), getString(R.string.error_delete_all_locations), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (locationsToDelete.isEmpty()) {
                dialog.dismiss()
                return@setOnClickListener
            }
            viewModel.deleteLocations(customer.id, locationsToDelete)
            dialog.dismiss()
        }
    }

    override fun delete(customer: Customer) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_delete_dialog))
            .setPositiveButton(R.string.text_delete) { _, _ ->
                viewModel.deleteCustomer(customer)
            }
            .setNegativeButton(R.string.text_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }

    override fun edit(customer: Customer) {
        viewModel.customerToUpdate(customer)
        val updateCustomerDialog = UpdateCustomerDialog()
        updateCustomerDialog.show(childFragmentManager, tag)
    }
}