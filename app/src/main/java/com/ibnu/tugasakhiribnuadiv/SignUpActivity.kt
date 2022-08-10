package com.ibnu.tugasakhiribnuadiv

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.ibnu.tugasakhiribnuadiv.databinding.ActivitySignUpBinding
import com.ibnu.tugasakhiribnuadiv.model.User

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private lateinit var fAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** firebase **/
        fAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference
        /** --- firebase --- **/

        actionbarAndStatusbar()

        /** input **/
        binding.apply {
            etSignupUsername.doOnTextChanged { text, start, before, count -> etLUsername(null) }
            etSignupEmail.doOnTextChanged { text, start, before, count -> etLEmail(null) }
            etSignupPw.doOnTextChanged { text, start, before, count -> etLPassword(null) }

            btSignUp.setOnClickListener {
                var username = etSignupUsername.text.toString()
                var email = etSignupEmail.text.toString()
                var password = etSignupPw.text.toString()

                when {
                    username.isEmpty() && email.isEmpty() && password.isEmpty() -> {
                        etLUsername("Please fill your username")
                        etLEmail("Please fill your email")
                        etLPassword("Please fill your password")
                        etSignupUsername.requestFocus()
                    }

                    username.isEmpty() -> {
                        etLUsername("Please fill your full name")
                        etSignupUsername.requestFocus()
                    }

                    email.isEmpty() -> {
                        etLEmail("Please fill your email")
                        etSignupEmail.requestFocus()
                    }

                    password.isEmpty() -> {
                        etLPassword("Please fill your password")
                        etSignupPw.requestFocus()
                    }

                    else -> {
                        // authentication by email and password using firebase authentication
                        fAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    auth()

                                    Snackbar.make(it, "Success", Snackbar.LENGTH_SHORT)
                                        .setBackgroundTint(resources.getColor(R.color.spaceCadet))
                                        .setTextColor(resources.getColor(R.color.brightGray))
                                        .show()
                                } else {
                                    Snackbar.make(it, "Failed", Snackbar.LENGTH_SHORT)
                                        .setBackgroundTint(resources.getColor(R.color.spaceCadet))
                                        .setTextColor(resources.getColor(R.color.brightGray))
                                        .show()
                                }
                            }
                    }
                }
            }
            btSignupLogin.setOnClickListener { moveToAcLogin() }
        }
        /** --- input --- **/
    }

    // input data and store to model then
    // insert data to firebase auth and realtime database
    private fun auth() {
        val username = binding.etSignupUsername.text.toString()
        val email = binding.etSignupEmail.text.toString()
        val password = binding.etSignupPw.text.toString()

        val uid = fAuth.uid.toString()

        // input data to database reference then go to realtime database
        dbRef.child("user").child(uid)
            .setValue(User(
                uid = uid,
                username = username,
                email = email,
                passowrd = password))

        logout()

        // clear edit text
        binding.etSignupUsername.text?.clear()
        binding.etSignupEmail.text?.clear()
        binding.etSignupPw.text?.clear()
    }

    // move to activity login
    fun moveToAcLogin() {
        onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    /** edit text layout function **/
    private fun etLUsername(note: String?) {
        binding.etLSignupUsername.error = note
    }

    private fun etLEmail(note: String?) {

        binding.etLSignupEmail.error = note
    }

    private fun etLPassword(note: String?) {
        binding.etLSignupPw.error = note
    }

    /** --- edit text layout function --- **/

    private fun actionbarAndStatusbar() {
        /** action bar **/
        val brightGrayCl = Color.parseColor("#E7E7F3")
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(brightGrayCl))
        supportActionBar!!.title = ""
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0f
        /** --- action bar --- **/

        /** window bar / status bar **/
        window.statusBarColor = ContextCompat.getColor(this, R.color.brightGray)
        // change icon color in status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        /** --- window bar / status bar --- **/
    }

    private fun logout() {
        if (true) {
            Firebase.auth.signOut()
        }
    }
}