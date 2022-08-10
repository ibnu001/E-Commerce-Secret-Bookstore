package com.ibnu.tugasakhiribnuadiv

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ibnu.tugasakhiribnuadiv.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var fAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** firebase **/
        fAuth = FirebaseAuth.getInstance()
        /** --- firebase --- **/

        actionbarAndStatusbar()

        /** input login **/
        binding.apply {

            etLoginEmail.doOnTextChanged { text, start, before, count -> etLEmail(null) }
            etLoginPw.doOnTextChanged { text, start, before, count -> etLPassword(null) }

            // button Login and move to Dashboard
            btLogin.setOnClickListener {
                var email = etLoginEmail.text.toString()
                var password = etLoginPw.text.toString()

                when {
                    email.isEmpty() && password.isEmpty() -> {
                        etLEmail("Please fill your email")
                        etLPassword("Please fill your password")
                        etLoginEmail.requestFocus()
                    }

                    email.isEmpty() -> {
                        etLEmail("Please fill your email")
                        etLoginEmail.requestFocus()
                    }

                    password.isEmpty() -> {
                        etLPassword("Please fill your password")
                        etLoginPw.requestFocus()
                    }

                    else -> {
                        login(email, password)
                    }
                }
            }
            // button move to Sign Up
            btLoginSignUp.setOnClickListener { moveToAcSignUp() }
        }
        /** --- input login --- **/
    }

    private fun moveToAcSignUp() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun login(email: String, password: String) {
        fAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    moveToAcDashboard()
                } else {
                    showToast("Incorrect email and password")
                }
            }
    }

    private fun etLEmail(note: String?) {
        binding.etLLoginEmail.error = note
    }

    private fun etLPassword(note: String?) {
        binding.etLLoginPw.error = note
    }

    private fun moveToAcDashboard() {
        Intent(this, DashboardActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    private fun showToast(note: String) {
        Toast.makeText(this, note, Toast.LENGTH_SHORT).show()
    }

    // button back
    override fun onSupportNavigateUp(): Boolean {
        val currentUser = fAuth.currentUser

        return if (currentUser == null){
            onBackPressed()
            true
        } else {
            startActivity(Intent(this, AccountActivity::class.java))
            finish()
            true
        }
    }

    private fun actionbarAndStatusbar(){
        /** action bar **/
        val brightGrayCl = Color.parseColor("#E7E7F3")
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(brightGrayCl))
        supportActionBar!!.title = ""
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

    override fun onStart() {
        super.onStart()

        val currentUser = fAuth.currentUser
        if (currentUser != null) {
            moveToAcDashboard()
        }
    }
}