package com.skysam.hchirinos.digitalforce.ui.services

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Service
import java.text.DateFormat

/**
 * Created by Hector Chirinos on 09/01/2023.
 */

class ServiceAdapter(private val services: MutableList<Service>, private val onClick: OnClick):
    RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_expense_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceAdapter.ViewHolder, position: Int) {
        val item = services[position]
        holder.name.text = "$${Classes.convertDoubleToString(item.total)}"
        holder.items.text = "${item.nameCustomer} - ${item.location}"
        holder.date.text = DateFormat.getDateInstance().format(item.date)

        holder.menu.setOnClickListener {
            val popMenu = PopupMenu(context, holder.menu)
            popMenu.inflate(R.menu.menu_expenses_item)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_view -> onClick.view(item)
                    R.id.menu_delete-> onClick.delete(item)
                }
                false
            }
            popMenu.show()
        }
    }

    override fun getItemCount(): Int = services.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_expense)
        val items: TextView = view.findViewById(R.id.tv_items)
        val date: TextView = view.findViewById(R.id.tv_date)
        val menu: TextView = view.findViewById(R.id.tv_menu)
    }
}