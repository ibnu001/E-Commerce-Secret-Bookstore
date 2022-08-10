package com.ibnu.tugasakhiribnuadiv

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ibnu.tugasakhiribnuadiv.databinding.ActivityCheckoutDetailBinding
import com.ibnu.tugasakhiribnuadiv.model.HistoryOrder
import java.text.NumberFormat
import java.util.*

class CheckoutDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutDetailBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth

    var ppn: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBookOrder()

        paymentCheckout()

        actionbarAndStatusbar()

        binding.btCdPlaceOrder.setOnClickListener {
            val rb1 = binding.rb1CdJne.isChecked
            val rb2 = binding.rb2CdSicepat.isChecked
            val rb3 = binding.rb3CdJnt.isChecked

            val dbRef = FirebaseDatabase.getInstance().reference
            val fAuth = FirebaseAuth.getInstance()

            val uidOrder = intent.getStringExtra("uidOrder").toString()
            val uidUser = fAuth.currentUser?.uid.toString()

            when {
                rb1.not() && rb2.not() && rb3.not() -> {
                    showToast("Please select your shipping option")
                }
                else -> {
                    dbRef.child("order").child(uidUser).child(uidOrder).removeValue()

                    addBookToHistoryOrder()

                    hideActionAndStatusbar()

                    binding.constraintLMain.visibility = View.GONE
                    binding.linearLSecond.visibility = View.VISIBLE

                    Glide.with(this)
                        .asGif()
                        .load(R.drawable.fast_delivery)
                        .fitCenter()
                        .circleCrop()
                        .into(binding.ivFcGif)

                    Handler().postDelayed({
                        val mIntent = Intent(this, DashboardActivity::class.java).also {
                            it.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        finish()
                        startActivity(mIntent)
                    }, 5000)
                }
            }
        }
    }

    private fun paymentCheckout() {
        val price = intent.getStringExtra("priceOrder")!!.toInt()

        binding.apply {
            rb1CdJne.setOnClickListener {
                // JNE
                val shipPrice = 10_000

                val totalDelivery = shipPrice
                val total = totalDelivery + price
                ppn = (total * 11f) / 100f
                val totalPayment = totalDelivery + ppn

                // show value to UI
                tvCdStforProduct.text = formatRupiah(price.toDouble())
                tvCdStDelivery.text = formatRupiah(totalDelivery.toDouble())
                tvCdPpn.text = formatRupiah(ppn.toDouble())
                tvCdTotalPayment.text = formatRupiah(totalPayment.toDouble())
                tvCdPoTotalPayment.text = formatRupiah(totalPayment.toDouble())
            }
            rb2CdSicepat.setOnClickListener {
                // SiCepat
                val shipPrice = 12_000

                val totalDelivery = shipPrice
                val total = totalDelivery + price
                ppn = (total * 11f) / 100f
                val totalPayment = totalDelivery + ppn

                // show value to UI
                tvCdStforProduct.text = formatRupiah(price.toDouble())
                tvCdStDelivery.text = formatRupiah(totalDelivery.toDouble())
                tvCdPpn.text = formatRupiah(ppn.toDouble())
                tvCdTotalPayment.text = formatRupiah(totalPayment.toDouble())
                tvCdPoTotalPayment.text = formatRupiah(totalPayment.toDouble())
            }
            rb3CdJnt.setOnClickListener {
                // JNT
                val shipPrice = 15_000

                val totalDelivery = shipPrice
                val total = totalDelivery + price
                ppn = (total * 11f) / 100f
                val totalPayment = totalDelivery + ppn


                // show value to UI
                tvCdStforProduct.text = formatRupiah(price.toDouble())
                tvCdStDelivery.text = formatRupiah(totalDelivery.toDouble())
                tvCdPpn.text = formatRupiah(ppn.toDouble())
                tvCdTotalPayment.text = formatRupiah(totalPayment.toDouble())
                tvCdPoTotalPayment.text = formatRupiah(totalPayment.toDouble())
            }
        }
    }

    private fun getBookOrder() {
        val title = intent.getStringExtra("titleOrder")
        val price = intent.getStringExtra("priceOrder")
        val cover = intent.getStringExtra("coverOrder")

        binding.apply {
            tvCdTitle.text = title
            tvCdPrice.text = formatRupiah(price!!.toDouble())

            Glide.with(this@CheckoutDetailActivity)
                .load(cover)
                .placeholder(R.drawable.ic_cover)
                .error(R.drawable.ic_cover)
                .fitCenter()
                .transform(RoundedCorners(20))
                .into(ivCdCover)
        }
    }

    private fun addBookToHistoryOrder() {
        val title = intent.getStringExtra("titleOrder").toString()
        val price = intent.getStringExtra("priceOrder").toString()
        val cover = intent.getStringExtra("coverOrder").toString()

        dbRef = FirebaseDatabase.getInstance().reference
        fAuth = FirebaseAuth.getInstance()

        val addPpn = Math.round(ppn).toString()

        val userUid = fAuth.currentUser?.uid.toString()
        val hOrderUid = UUID.randomUUID().toString()

        dbRef.child("history_order").child(userUid).child(hOrderUid)
            .setValue(HistoryOrder(hOrderUid, userUid, title, price, addPpn, cover))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun actionbarAndStatusbar() {
        /** action bar **/
        val brightGrayCl = Color.parseColor("#E7E7F3")
        val abTitle = (Html.fromHtml("<font color=\"#1A374D\">" + "Checkout" + "</font>"))
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

    fun hideActionAndStatusbar() {
        supportActionBar!!.hide()
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

    private fun showToast(note: String?) {
        Toast.makeText(this@CheckoutDetailActivity, note, Toast.LENGTH_SHORT).show()
    }
}