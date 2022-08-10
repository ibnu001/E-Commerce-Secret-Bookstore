package com.ibnu.tugasakhiribnuadiv

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.ibnu.tugasakhiribnuadiv.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** action bar **/
        supportActionBar!!.hide()

        /** status bar **/
        // This is used to hide status bar and make
        // the splash screen as a full screen activity
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        /** delay this activity and will show then move to next activity **/
        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500) // 1500 is the delayed time in milliseconds.
    }
}