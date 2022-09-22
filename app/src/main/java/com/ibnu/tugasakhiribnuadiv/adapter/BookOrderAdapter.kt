package com.ibnu.tugasakhiribnuadiv.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ibnu.tugasakhiribnuadiv.CheckoutDetailActivity
import com.ibnu.tugasakhiribnuadiv.R
import com.ibnu.tugasakhiribnuadiv.model.BookOrder
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class BookOrderAdapter(
    val context: Context,
    val dataBookOrder: ArrayList<BookOrder>
) : RecyclerView.Adapter<BookOrderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle = itemView.findViewById<TextView>(R.id.tv_ilo_title)
        val tvPrice = itemView.findViewById<TextView>(R.id.tv_ilo_price)
        val ivCover = itemView.findViewById<ImageView>(R.id.iv_ilo_cover)
        val btCheckout = itemView.findViewById<Button>(R.id.bt_ilo_checkout)
        val btDelete = itemView.findViewById<Button>(R.id.bt_ilo_delete)
        val mCardView = itemView.findViewById<MaterialCardView>(R.id.mcv_item_list_order)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.item_list_order, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataBookOrder[position]

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

            btCheckout.setOnClickListener {
                val intent = Intent(context, CheckoutDetailActivity::class.java)

                intent.apply {
                    putExtra("titleOrder", currentItem.title)
                    putExtra("priceOrder", currentItem.price)
                    putExtra("coverOrder", currentItem.cover)
                    putExtra("uidOrder", currentItem.uidOrder)
                }
                itemView.context.startActivity(intent)

            }

            btDelete.setOnClickListener {
                val dbRef = FirebaseDatabase.getInstance().reference
                val fAuth = FirebaseAuth.getInstance()
                val uidUser = fAuth.currentUser?.uid.toString()
                val uidOrder = currentItem.uidOrder.toString()

                dbRef.child("order").child(uidUser).child(uidOrder).removeValue()
                mCardView.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return dataBookOrder.size
    }

    private fun formatRupiah(number: Double): String? {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number)
    }
}