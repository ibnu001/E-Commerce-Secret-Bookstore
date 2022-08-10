package com.ibnu.tugasakhiribnuadiv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.ibnu.tugasakhiribnuadiv.R
import com.ibnu.tugasakhiribnuadiv.model.HistoryOrder
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class HistoryOrderAdapter(
    val context: Context,
    val dataHistoryOrder: ArrayList<HistoryOrder>,
) : RecyclerView.Adapter<HistoryOrderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById<TextView>(R.id.tv_ilmo_title)
        val tvPrice = itemView.findViewById<TextView>(R.id.tv_ilmo_price)
        val ivCover = itemView.findViewById<ImageView>(R.id.iv_ilmo_cover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.item_list_my_order, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataHistoryOrder[position]

        holder.apply {
            tvTitle.text = currentItem.title
            tvPrice.text = formatRupiah(currentItem.price!!.toDouble())

            Glide.with(itemView)
                .load(currentItem.cover)
                .placeholder(R.drawable.ic_cover)
                .error(R.drawable.ic_cover)
                .fitCenter()
                .transform(RoundedCorners(20))
                .into(ivCover)
        }
    }

    override fun getItemCount(): Int {
        return dataHistoryOrder.size
    }

    private fun formatRupiah(number: Double): String? {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number)
    }
}