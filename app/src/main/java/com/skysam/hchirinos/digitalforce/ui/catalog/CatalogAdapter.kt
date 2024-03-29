package com.skysam.hchirinos.digitalforce.ui.catalog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.skysam.hchirinos.digitalforce.R
import com.skysam.hchirinos.digitalforce.common.Classes
import com.skysam.hchirinos.digitalforce.dataClass.Product
import java.io.File


/**
 * Created by Hector Chirinos on 05/06/2022.
 */

class CatalogAdapter(private val products: MutableList<Product>, private val onClick: OnClick): RecyclerView.Adapter<CatalogAdapter.ViewHolder>() {
    private lateinit var context: Context

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val image: ImageView = view.findViewById(R.id.image)
        val menu: TextView = view.findViewById(R.id.tv_menu)
        val share: MaterialButton = view.findViewById(R.id.btn_share)
        val sell: MaterialButton = view.findViewById(R.id.btn_sell)
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
        holder.price.text = context.getString(R.string.text_price_symbol,
            Classes.convertDoubleToString(item.price))
        Glide.with(context)
            .load(item.image)
            .centerCrop()
            .placeholder(R.drawable.ic_image_64)
            .into(holder.image)

        if (item.pdf != null) holder.share.visibility = View.VISIBLE
        else holder.share.visibility = View.GONE

        holder.menu.setOnClickListener {
            val popMenu = PopupMenu(context, holder.menu)
            popMenu.inflate(R.menu.menu_catalog_item)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_edit-> onClick.edit(item)
                    R.id.menu_delete-> onClick.delete(item)
                }
                false
            }
            popMenu.show()
        }

        holder.share.setOnClickListener {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            builder.detectFileUriExposure()
            /*val emojiPin = String(Character.toChars(0x1F4CC))
            val selection = StringBuilder()
            selection.append("Producto disponible: ")
            selection.append("\n\n").append("$emojiPin ${item.name}.")
            selection.append("\n").append("Precio: \$${Classes.convertDoubleToString(item.price)}")*/

            val refurl = Firebase.storage.getReferenceFromUrl(item.pdf!!)

            //val localFile = File.createTempFile("brochures", ".pdf")

            /*val rootPath = File(Environment.getExternalStorageDirectory(), "file_name_test")
            if (!rootPath.exists()) {
                rootPath.mkdirs()
            }

            val localFile = File(rootPath, "brochure.pdf")*/

            val path = context.getExternalFilesDir(null)?.absolutePath.toString() + "/brochure.pdf"
            val localFile = File(path)

            refurl.getFile(localFile).addOnSuccessListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "application/pdf"
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(localFile))
                context.startActivity(Intent.createChooser(intent, context.getString(R.string.title_share_dialog)))
            }.addOnFailureListener {
                Log.e("error", it.toString())
            }
        }
    }

    override fun getItemCount(): Int = products.size
}