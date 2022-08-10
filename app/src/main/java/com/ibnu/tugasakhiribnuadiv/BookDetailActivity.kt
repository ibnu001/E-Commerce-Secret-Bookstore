package com.ibnu.tugasakhiribnuadiv

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ibnu.tugasakhiribnuadiv.databinding.ActivityBookDetailBinding
import com.ibnu.tugasakhiribnuadiv.model.BookOrder
import java.text.NumberFormat
import java.util.*

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBookSale()

        updateBookDetail()

        actionbarAndStatusbar()

        fAuth = FirebaseAuth.getInstance()

        val uidUser = intent.getStringExtra("uidUser")
        val currentUser = fAuth.currentUser?.uid.toString()

        if (currentUser == uidUser) {
            binding.btBdAddToCart.visibility = View.GONE
        } else {
            binding.btBdAddToCart.visibility = View.VISIBLE

            binding.btBdAddToCart.setOnClickListener {
                when (intent.getStringExtra("stock")!!.toInt()) {
                    0 -> {
                        showToast("Sorry the stock is empty now")
                    }
                    else -> {
                        fAuth = FirebaseAuth.getInstance()

                        when (fAuth.currentUser?.uid) {
                            null -> {
                                startActivity(Intent(this, LoginActivity::class.java))
                            }
                            else -> {
                                addBookToCart()

                                Snackbar.make(it, "Add to Order", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(resources.getColor(R.color.spaceCadet))
                                    .setTextColor(resources.getColor(R.color.brightGray))
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }

    /** Update Book Stock **/
    private fun updateBookDetail() {
        val uidProduct = intent.getStringExtra("uidBook")
        val uidUser = intent.getStringExtra("uidUser")

        fAuth = FirebaseAuth.getInstance()
        val currentUser = fAuth.currentUser?.uid.toString()

        binding.apply {
            if (currentUser != uidUser) {
                btBdUpdateStock.visibility = View.GONE
            } else {
                btBdUpdateStock.visibility = View.VISIBLE

                btBdUpdateStock.setOnClickListener {
                    val builder = AlertDialog.Builder(this@BookDetailActivity)
                    val dialogLayout = layoutInflater.inflate(R.layout.edit_text_stock, null)
                    val etUpdateStock = dialogLayout.findViewById<EditText>(R.id.et_ets_updateStock)

                    etUpdateStock.hint = "Stock"

                    val adTitle =
                        (Html.fromHtml("<font color=\"#1A374D\">" + "Enter Stock!" + "</font>"))
                    val adButton = (Html.fromHtml("<font color=\"#1A374D\">" + "Apply" + "</font>"))

                    dbRef = FirebaseDatabase.getInstance().reference
                    val pathStock = dbRef.child("book_sale").child(uidProduct!!).child("stock")

                    with(builder) {
                        setTitle(adTitle)
                        setPositiveButton(adButton) { dialog, which ->
                            val stock = etUpdateStock.text.toString()

                            pathStock.setValue(stock)

                            tvBdStock.text = stock
                        }

                        setView(dialogLayout)
                        show()
                    }
                }
            }
        }
    }

    /** --- Update Book Stock --- **/

    @SuppressLint("SetTextI18n")
    private fun getBookSale() {
        val title = intent.getStringExtra("title")
        val author = intent.getStringExtra("author")
        val publishing = intent.getStringExtra("publishing")
        val pages = intent.getStringExtra("pages")
        val category = intent.getStringExtra("category")
        val price = intent.getStringExtra("price")
        val stock = intent.getStringExtra("stock")
        val description = intent.getStringExtra("description")
        val cover = intent.getStringExtra("cover")

        binding.apply {
            // Head title and author
            tvBdHeadTitle.text = title
            tvBdHeadAuthor.text = "by $author"

            // detail book
            tvBdTitle.text = title
            tvBdAuthor.text = "by $author"
            tvBdPublishing.text = publishing
            tvBdPages.text = pages
            tvBdCategory.text = category
            tvBdBookPrice.text = formatRupiah(price!!.toDouble())
            tvBdStock.text = stock
            tvBdDescription.text = description

            Glide.with(this@BookDetailActivity)
                .load(cover)
                .placeholder(R.drawable.ic_cover)
                .error(R.drawable.ic_cover)
                .fitCenter()
                .transform(RoundedCorners(20))
                .into(ivBdCoverbook)
        }
    }

    private fun addBookToCart() {
        val title = intent.getStringExtra("title").toString()
        val price = intent.getStringExtra("price").toString()
        val cover = intent.getStringExtra("cover").toString()
        val getStock = intent.getStringExtra("stock")!!.toInt()
        val stock = (getStock - 1).toString()

        val uidProduct = intent.getStringExtra("uidBook")

        dbRef = FirebaseDatabase.getInstance().reference
        fAuth = FirebaseAuth.getInstance()

        val userUid = fAuth.currentUser?.uid.toString()
        val orderUid = UUID.randomUUID().toString()

        val pathStock = dbRef.child("book_sale").child(uidProduct!!).child("stock")

        dbRef.child("order").child(userUid).child(orderUid)
            .setValue(
                BookOrder(
                    uidOrder = orderUid,
                    uidUser = userUid,
                    title = title,
                    price = price,
                    cover = cover
                )
            )

        pathStock.setValue(stock)

        binding.tvBdStock.text = stock
    }

    private fun showToast(note: String) {
        Toast.makeText(this, note, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun actionbarAndStatusbar() {
        /** action bar **/
        val brightGrayCl = Color.parseColor("#E7E7F3")
        val abTitle = (Html.fromHtml("<font color=\"#1A374D\">" + "Book" + "</font>"))
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(brightGrayCl))
        supportActionBar!!.title = abTitle
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0f

        /** window bar / status bar **/
        window.statusBarColor = ContextCompat.getColor(this, R.color.brightGray)
        // change icon color in status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun formatRupiah(number: Double): String? {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        return formatRupiah.format(number)
    }

}