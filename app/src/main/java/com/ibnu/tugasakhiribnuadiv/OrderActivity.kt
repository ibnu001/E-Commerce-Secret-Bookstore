package com.ibnu.tugasakhiribnuadiv

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ibnu.tugasakhiribnuadiv.adapter.BookOrderAdapter
import com.ibnu.tugasakhiribnuadiv.databinding.ActivityOrderBinding
import com.ibnu.tugasakhiribnuadiv.model.BookOrder


class OrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderBinding

    private lateinit var orderRv: RecyclerView
    private lateinit var dataBookOrder: ArrayList<BookOrder>
    private lateinit var adapter: BookOrderAdapter

    private lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderRv = findViewById(R.id.rv_order)
        orderRv.layoutManager = LinearLayoutManager(this@OrderActivity)
        orderRv.setHasFixedSize(true)

        dataBookOrder = ArrayList()
        adapter = BookOrderAdapter(this@OrderActivity, dataBookOrder)

        getListBookOrder()

        actionbarAndStatusbar()
    }

    private fun getListBookOrder() {
        fAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference

        val userUid = fAuth.currentUser?.uid.toString()

        // make path inside varible
        val pathOrderByCurrentUser = dbRef.child("order").child(userUid)

        // variable ValueEventListener for showing data from firebase
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    dataBookOrder.clear()

                    for (listBookOrder in snapshot.children) {
                        val bookOrder = listBookOrder.getValue(BookOrder::class.java)
                        // make newest item on top
                        dataBookOrder.add(0, bookOrder!!)
                        adapter.notifyDataSetChanged()
                    }
                    orderRv.adapter = BookOrderAdapter(this@OrderActivity, dataBookOrder)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cancel", error.message)
            }
        }
        // call path firebase and show
        pathOrderByCurrentUser.addValueEventListener(postListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun actionbarAndStatusbar() {
        /** action bar **/
        val brightGrayCl = Color.parseColor("#E7E7F3")
        val abTitle = (Html.fromHtml("<font color=\"#1A374D\">" + "Order" + "</font>"))
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
}