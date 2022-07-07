package com.skysam.hchirinos.digitalforce.ui.customer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.dataClass.Customer

/**
 * Created by Hector Chirinos on 30/05/2022.
 */

class CustomersAdapter(private val customers: MutableList<Customer>, private val onClick: OnClick):
    RecyclerView.Adapter<CustomersAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomersAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_customer_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomersAdapter.ViewHolder, position: Int) {
        val item = customers[position]
        holder.name.text = item.name

        holder.menu.setOnClickListener {
            val popMenu = PopupMenu(context, holder.menu)
            popMenu.inflate(R.menu.menu_customer_item)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_delete_location-> onClick.deleteLocation(item)
                    R.id.menu_edit-> onClick.edit(item)
                    R.id.menu_delete-> onClick.delete(item)
                }
                false
            }
            popMenu.show()
        }
    }

    override fun getItemCount(): Int = customers.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val menu: TextView = view.findViewById(R.id.tv_menu)
    }
}