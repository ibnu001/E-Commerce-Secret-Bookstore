package com.ibnu.tugasakhiribnuadiv.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.ibnu.tugasakhiribnuadiv.BookDetailActivity
import com.ibnu.tugasakhiribnuadiv.R
import com.ibnu.tugasakhiribnuadiv.model.BookSell
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class BookSellAdapter(
    val context: Context?,
    val dataBookSellList: ArrayList<BookSell>
) : RecyclerView.Adapter<BookSellAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvTitle = itemView.findViewById<TextView>(R.id.tv_itembook_title)
        var tvAuthor = itemView.findViewById<TextView>(R.id.tv_itembook_author)
        var tvPrice = itemView.findViewById<TextView>(R.id.tv_itembook_price)
        var ivCover = itemView.findViewById<ImageView>(R.id.iv_itemdb_cover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_dashboard, parent, false)

        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataBookSellList[position]

        holder.apply {

            // put value to ui
            tvTitle.text = currentItem.title
            tvAuthor.text = "by ${currentItem.author}"
            tvPrice.text = formatRupiah(currentItem.price!!.toDouble())

            // put image value(type url) to item cover book
            Glide.with(itemView)
                .load(currentItem.cover)
                .placeholder(R.drawable.ic_cover)
                .error(R.drawable.ic_cover)
                .fitCenter()
                .transform(RoundedCorners(20))
                .into(ivCover)

            itemView.setOnClickListener {
                val intent = Intent(context, BookDetailActivity::class.java)

                intent.apply {
                    putExtra("uidBook", currentItem.uidBookSell)
                    putExtra("uidUser", currentItem.uidUserSell)
                    putExtra("title", currentItem.title)
                    putExtra("author", currentItem.author)
                    putExtra("publishing", currentItem.publishing)
                    putExtra("pages", currentItem.pages)
                    putExtra("category", currentItem.category)
                    putExtra("price", currentItem.price)
                    putExtra("stock", currentItem.stock)
                    putExtra("description", currentItem.description)
                    putExtra("cover", currentItem.cover)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataBookSellList.size
    }

    private fun formatRupiah(number: Double): String? {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number)
    }
}