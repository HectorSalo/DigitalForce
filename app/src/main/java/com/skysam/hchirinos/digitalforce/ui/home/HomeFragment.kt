package com.skysam.hchirinos.digitalforce.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.dataClass.Expense
import com.skysam.hchirinos.digitalforce.dataClass.Sale
import com.skysam.hchirinos.digitalforce.dataClass.Service
import com.skysam.hchirinos.digitalforce.databinding.FragmentHomeBinding
import com.skysam.hchirinos.digitalforce.ui.settings.SettingsActivity
import java.util.*

class HomeFragment : Fragment(), MenuProvider {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()
    private var month = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendar = Calendar.getInstance()
        month = calendar.get(Calendar.MONTH)

        binding.btnExpense.setOnClickListener {

        }

        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.expenses.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    val listMonth = mutableListOf<Expense>()
                    for (expense in it) {
                        val calendar = Calendar.getInstance()
                        calendar.time = expense.date
                        if (month > -1 && calendar.get(Calendar.MONTH) == month) {
                            listMonth.add(expense)
                        }
                    }
                    if (listMonth.isNotEmpty()) binding.tvExpense.text = getString(R.string.text_number_expenses, listMonth.size.toString())
                    else binding.tvExpense.text = getString(R.string.text_not_expense)
                } else {
                    binding.tvExpense.text = getString(R.string.text_not_expense)
                }
            }
        }

        viewModel.sales.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    val listMonth = mutableListOf<Sale>()
                    for (sale in it) {
                        val calendar = Calendar.getInstance()
                        calendar.time = sale.date
                        if (month > -1 && calendar.get(Calendar.MONTH) == month) {
                            listMonth.add(sale)
                        }
                    }
                    if (listMonth.isNotEmpty()) binding.tvSale.text = getString(R.string.text_number_sales, listMonth.size.toString())
                    else binding.tvSale.text = getString(R.string.text_not_sales)
                } else {
                    binding.tvSale.text = getString(R.string.text_not_sales)
                }
            }
        }

        viewModel.services.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    val listMonth = mutableListOf<Service>()
                    for (service in it) {
                        val calendar = Calendar.getInstance()
                        calendar.time = service.date
                        if (month > -1 && calendar.get(Calendar.MONTH) == month) {
                            listMonth.add(service)
                        }
                    }
                    if (listMonth.isNotEmpty()) binding.tvService.text = getString(R.string.text_number_services, listMonth.size.toString())
                    else binding.tvService.text = getString(R.string.text_not_services)
                } else {
                    binding.tvService.text = getString(R.string.text_not_services)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
                true
            }
            else -> false
        }
    }
}