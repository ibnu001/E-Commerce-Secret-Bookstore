package com.ibnu.tugasakhiribnuadiv

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ibnu.tugasakhiribnuadiv.adapter.HistoryOrderAdapter
import com.ibnu.tugasakhiribnuadiv.databinding.ActivityAccountBinding
import com.ibnu.tugasakhiribnuadiv.model.HistoryOrder
import java.util.*
import kotlin.collections.ArrayList

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    private lateinit var historyRv: RecyclerView
    private lateinit var dataHistoryOrder: ArrayList<HistoryOrder>
    private lateinit var adapter: HistoryOrderAdapter

    private lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        historyRv = findViewById(R.id.rv_myOrder)
        historyRv.layoutManager = GridLayoutManager(this@AccountActivity, 2)
        historyRv.setHasFixedSize(true)

        dataHistoryOrder = ArrayList()
        adapter = HistoryOrderAdapter(this@AccountActivity, dataHistoryOrder)

        getListHistoryOrder()

        // Firebase
        dbRef = FirebaseDatabase.getInstance().reference
        fAuth = FirebaseAuth.getInstance()

        // this will get uid current user when user login
        val uidUser = fAuth.currentUser?.uid.toString()

        // path String firebase, get spesific field key and value
        val pathUsername = dbRef.child("user").child(uidUser).child("username").get()
        val pathEmail = dbRef.child("user").child(uidUser).child("email").get()
        val pathFotoprofil = dbRef.child("user").child(uidUser).child("fotoProfil").get()

        // show value from path string when success or failed
        // path Username
        pathUsername.addOnSuccessListener { binding.tvAcUsername.text = it.value.toString() }
            .addOnFailureListener { Log.e("firebase", "Error getting data", it) }
        // path Email
        pathEmail.addOnSuccessListener { binding.tvAcEmail.text = it.value.toString() }
            .addOnFailureListener { Log.e("firebase", "Error getting data", it) }
        // path Foto Profil
        pathFotoprofil.addOnSuccessListener {
            Glide.with(this)
                .load(it.value.toString())
                .placeholder(R.drawable.ic_account)
                .error(R.drawable.ic_account)
                .fitCenter()
                .circleCrop()
                .into(binding.ivAcFotoProfil)
        }
            .addOnFailureListener { Log.e("firebase", "Error getting data", it) }

        actionbarAndStatusbar()

        binding.btAcAddSales.setOnClickListener {
            moveToAcAddBookSales()
        }

        binding.btAcChangePicture.setOnClickListener {
            startActivity(Intent(this@AccountActivity, SettingProfilActivity::class.java))
        }

        binding.btAcChangeUsername.setOnClickListener {
            val builder = AlertDialog.Builder(this@AccountActivity)
            val dialogLayout = layoutInflater.inflate(R.layout.edit_text_stock, null)
            val changeUnm = dialogLayout.findViewById<EditText>(R.id.et_ets_updateStock)

            dbRef = FirebaseDatabase.getInstance().reference
            val uidUsername = fAuth.currentUser?.uid.toString()
            val pathChgUsernameChg = dbRef.child("user").child(uidUsername).child("username")

            changeUnm.hint = "Change Username"
            changeUnm.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME

            val adTitle = (Html.fromHtml("<font color=\"#1A374D\">" + "Change Username" + "</font>"))
            val adButton = (Html.fromHtml("<font color=\"#1A374D\">" + "Apply" + "</font>"))


            with(builder) {
                setTitle(adTitle)
                setPositiveButton(adButton) { dialog, which ->
                    val cUsername = changeUnm.text.toString()

                    pathChgUsernameChg.setValue(cUsername)

                    binding.tvAcUsername.text = cUsername
                }

                setView(dialogLayout)
                show()
            }
        }

        binding.tvBtAcLogout.setOnClickListener { logout() }
    }

    private fun getListHistoryOrder() {
        fAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference

        val userUid = fAuth.currentUser?.uid.toString()

        // make path inside varible
        val pathHistoryByCurrentUser = dbRef.child("history_order").child(userUid)

        // variable ValueEventListener for showing data from firebase
        val postListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    dataHistoryOrder.clear()

                    for (listHistoryOrder in snapshot.children) {
                        val historyOrder = listHistoryOrder.getValue(HistoryOrder::class.java)
                        // make newest item on top
                        dataHistoryOrder.add(0, historyOrder!!)
                        adapter.notifyDataSetChanged()
                    }
                    historyRv.adapter = HistoryOrderAdapter(this@AccountActivity, dataHistoryOrder)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Cancel", error.message)
            }
        }
        // call path firebase and show
        pathHistoryByCurrentUser.addValueEventListener(postListener)
    }

    private fun moveToAcAddBookSales() {
        startActivity(Intent(this, AddBookSalesActivity::class.java))
    }

    // logout account
    private fun logout() {
        Firebase.auth.signOut()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun actionbarAndStatusbar() {
        /** action bar **/
        val brightGrayCl = Color.parseColor("#E7E7F3")
        val abTitle = (Html.fromHtml("<font color=\"#1A374D\">" + "Account" + "</font>"))
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(brightGrayCl))
        supportActionBar!!.title = abTitle
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar!!.elevation = 0f
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        /** --- action bar --- **/

        /** window bar / status bar **/
        window.statusBarColor = ContextCompat.getColor(this, R.color.brightGray)
        // change icon color in status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        /** --- window bar / status bar --- **/
    }
}
