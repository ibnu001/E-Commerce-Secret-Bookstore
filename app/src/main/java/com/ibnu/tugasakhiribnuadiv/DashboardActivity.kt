package com.ibnu.tugasakhiribnuadiv

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ibnu.tugasakhiribnuadiv.adapter.BookSellAdapter
import com.ibnu.tugasakhiribnuadiv.databinding.ActivityDashboardBinding
import com.ibnu.tugasakhiribnuadiv.model.BookSell

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    private lateinit var DasboardRv: RecyclerView
    private lateinit var dataBookSaleList: ArrayList<BookSell>
    private lateinit var adapter: BookSellAdapter

    private lateinit var fAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // make recyclerView have Grid layout instead linear layout
        DasboardRv = findViewById(R.id.rv_dashboard)
        DasboardRv.layoutManager = GridLayoutManager(this, 2)
        DasboardRv.setHasFixedSize(true)

        dataBookSaleList = ArrayList()
        adapter = BookSellAdapter(this@DashboardActivity, dataBookSaleList)

        // get all list book sale from firebase
        getListBookSale()

        actionbarAndStatusbarAndBottomNav()
    }

    private fun getListBookSale() {
        dbRef = FirebaseDatabase.getInstance().reference.child("book_sale")
        fAuth = FirebaseAuth.getInstance()

        dbRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    dataBookSaleList.clear()

                    for (listBookSale in snapshot.children) {
                        val bookSale = listBookSale.getValue(BookSell::class.java)
                        dataBookSaleList.add(bookSale!!)
                    }
                    adapter.notifyDataSetChanged()
                    DasboardRv.adapter = BookSellAdapter(this@DashboardActivity, dataBookSaleList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cancel", error.message)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun actionbarAndStatusbarAndBottomNav() {
        /** action bar **/
        val brightGrayCl = Color.parseColor("#E7E7F3")
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(brightGrayCl))
        supportActionBar!!.elevation = 0f

        // title, color title
        val abTitle = (Html.fromHtml("<font color=\"#1A374D\">" + "Secret Bookstore" + "</font>"))
        supportActionBar!!.title = abTitle

        /** window bar / status bar **/
        window.statusBarColor = ContextCompat.getColor(this, R.color.brightGray)
        // change icon color in status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        /** bottom navigation bar **/
        val shapeDrawable = binding.bottomNavigation.background as MaterialShapeDrawable
        shapeDrawable.shapeAppearanceModel = shapeDrawable.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, 500F)
            .build()

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bot_app_order -> {
                    startActivity(Intent(this, OrderActivity::class.java))
                    true
                }

                R.id.bot_app_account -> {
                    val currentUser = fAuth.currentUser

                    if (currentUser != null) {
                        startActivity(Intent(this, AccountActivity::class.java))
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }
    }
}