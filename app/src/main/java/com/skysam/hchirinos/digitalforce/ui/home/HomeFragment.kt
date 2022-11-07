package com.skysam.hchirinos.digitalforce.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.databinding.FragmentHomeBinding
import com.skysam.hchirinos.digitalforce.ui.settings.SettingsActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnExpense.setOnClickListener {

        }

        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.expenses.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    binding.tvExpense.text = getString(R.string.text_number_expenses, it.size.toString())
                } else {
                    binding.tvExpense.text = getString(R.string.text_not_expense)
                }
            }
        }

        viewModel.sales.observe(viewLifecycleOwner) {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    binding.tvSale.text = getString(R.string.text_number_sales, it.size.toString())
                } else {
                    binding.tvSale.text = getString(R.string.text_not_sales)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.main, menu)
        val item = menu.findItem(R.id.action_settings)
        item.setOnMenuItemClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
            true
        }
    }
}