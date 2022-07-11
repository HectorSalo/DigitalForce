package com.skysam.hchirinos.digitalforce.ui.expenses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Product

class ItemListAdapter(private val products: MutableList<Product>, private val onClickList: OnClickList):
    RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product_list, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        holder.quantity.text = context.getString(R.string.text_price_item,
            item.quantity.toString(), Classes.convertDoubleToString(item.price))
        holder.priceQuantity.text = Classes.convertDoubleToString(item.price * item.quantity)
        holder.buttonDelete.setOnClickListener { onClickList.deleteItem(item) }
        holder.buttonEdit.setOnClickListener { onClickList.editItem(item) }
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name_item)
        val quantity: TextView = view.findViewById(R.id.tv_quantity)
        val price: TextView = view.findViewById(R.id.tv_price)
        val priceQuantity: TextView = view.findViewById(R.id.tv_price_quantity)
        val buttonDelete: ImageButton = view.findViewById(R.id.ib_delete_item)
        val buttonEdit: ConstraintLayout = view.findViewById(R.id.constraint)
    }
}
