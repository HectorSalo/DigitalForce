package com.skysam.hchirinos.digitalforce.ui.services

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
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.dataClass.Customer
import com.skysam.hchirinos.digitalforce.dataClass.Service
import com.skysam.hchirinos.digitalforce.databinding.FragmentServicesBinding
import com.skysam.hchirinos.digitalforce.ui.services.newService.NewServiceActivity

class ServicesFragment : Fragment(), OnClick {

    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ServicesViewModel by activityViewModels()
    private val services = mutableListOf<Service>()
    private val customers = mutableListOf<Customer>()
    private lateinit var serviceAdapter: ServiceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceAdapter = ServiceAdapter(services, this)
        binding.rvServices.apply {
            setHasFixedSize(true)
            adapter = serviceAdapter
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
            if (customers.isNotEmpty()) {
                startActivity(Intent(requireContext(), NewServiceActivity::class.java))
            } else {
                Snackbar.make(binding.root, "Debe crear un Cliente", Snackbar.LENGTH_SHORT).show()
            }
        }

        loadViewModels()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadViewModels() {
        viewModel.services.observe(viewLifecycleOwner) {
            if (_binding != null) {
                binding.progressBar.visibility = View.GONE
                if (it.isNotEmpty()) {
                    services.clear()
                    services.addAll(it)
                    serviceAdapter.notifyDataSetChanged()
                    binding.rvServices.visibility = View.VISIBLE
                    binding.tvListEmpty.visibility = View.GONE
                } else {
                    binding.rvServices.visibility = View.GONE
                    binding.tvListEmpty.visibility = View.VISIBLE
                }
            }
        }
        viewModel.customers.observe(viewLifecycleOwner) {
            if (_binding != null) {
                customers.clear()
                customers.addAll(it)
            }
        }
    }

    override fun view(service: Service) {
        viewModel.viewService(service)
        val viewServiceDialog = ViewServiceDialog()
        viewServiceDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun delete(service: Service) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_delete_dialog))
            .setPositiveButton(R.string.text_delete) { _, _ ->
                viewModel.deleteService(service)
            }
            .setNegativeButton(R.string.text_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }
}