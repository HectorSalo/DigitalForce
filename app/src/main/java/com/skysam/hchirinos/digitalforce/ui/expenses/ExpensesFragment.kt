package com.skysam.hchirinos.digitalforce.ui.expenses

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.dataClass.Expense
import com.skysam.hchirinos.digitalforce.databinding.FragmentExpensesBinding

class ExpensesFragment : Fragment(), OnClick {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpensesViewModel by activityViewModels()
    private lateinit var adapterExpense: ExpensesAdapter
    private val expenses = mutableListOf<Expense>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterExpense = ExpensesAdapter(expenses, this)
        binding.rvExpenses.apply {
            setHasFixedSize(true)
            adapter = adapterExpense
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
            val dialog = AddExpenseDialog()
            dialog.show(requireActivity().supportFragmentManager, tag)
        }

        loadViewModel()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadViewModel() {
        viewModel.expenses.observe(viewLifecycleOwner) {
            if (_binding != null) {
                expenses.clear()
                if (it.isNotEmpty()) {
                    expenses.addAll(it)
                    adapterExpense.notifyDataSetChanged()
                    binding.rvExpenses.visibility = View.VISIBLE
                    binding.tvListEmpty.visibility = View.GONE
                } else {
                    binding.rvExpenses.visibility = View.GONE
                    binding.tvListEmpty.visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun viewExpense(expense: Expense) {
        val dialog = ViewExpenseDialog(expense)
        dialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun edit(expense: Expense) {

    }

    override fun delete(expense: Expense) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_delete_dialog))
            .setPositiveButton(R.string.text_delete) { _, _ ->
                viewModel.deleteExpense(expense)
            }
            .setNegativeButton(R.string.text_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }

}