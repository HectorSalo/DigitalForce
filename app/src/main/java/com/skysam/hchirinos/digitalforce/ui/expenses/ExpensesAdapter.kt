package com.skysam.hchirinos.digitalforce.ui.expenses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Expense
import java.text.DateFormat

/**
 * Created by Hector Chirinos on 09/07/2022.
 */

class ExpensesAdapter(private val expenses: MutableList<Expense>): RecyclerView.Adapter<ExpensesAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_expense_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpensesAdapter.ViewHolder, position: Int) {
        val item = expenses[position]
        //holder.name.text = item.nameSupplier
        holder.price.text = Classes.convertDoubleToString(item.total)
        holder.date.text = DateFormat.getDateInstance().format(item.date)

        holder.card.setOnClickListener {
           /* val popMenu = PopupMenu(context, holder.card)
            popMenu.inflate(R.menu.menu_expenses_item)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_view -> onClick.viewExpense(item)
                    R.id.menu_edit-> onClick.edit(item)
                    R.id.menu_delete-> onClick.delete(item)
                }
                false
            }
            popMenu.show()*/
        }
    }

    override fun getItemCount(): Int = expenses.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val date: TextView = view.findViewById(R.id.tv_date)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }
}