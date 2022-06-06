package com.skysam.hchirinos.digitalforce.ui.catalog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Product

/**
 * Created by Hector Chirinos on 05/06/2022.
 */

class CatalogAdapter(private val products: MutableList<Product>): RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val image: ImageView = view.findViewById(R.id.image)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_catalog_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        holder.price.text = Classes.convertDoubleToString(item.price)
        Glide.with(context)
            .load(item.image)
            .centerCrop()
            .placeholder(R.drawable.ic_image_64)
            .into(holder.image)

        //holder.card.isChecked = listToDeleted.contains(item.id)

        holder.card.setOnClickListener {

        }
        holder.card.setOnLongClickListener {
            holder.card.isChecked = !holder.card.isChecked
            true
        }
    }

    override fun getItemCount(): Int = products.size
}