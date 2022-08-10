package com.ibnu.tugasakhiribnuadiv

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ibnu.tugasakhiribnuadiv.databinding.ActivitySettingProfilBinding
import java.util.*

class SettingProfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingProfilBinding

    private lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth

    private lateinit var imageUri: Uri

    // use for firebase storage
    private lateinit var uploadTask: UploadTask
    private lateinit var fStore: FirebaseStorage
    private lateinit var sRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference
        fAuth = FirebaseAuth.getInstance()

        // Firebase Storage
        fStore = FirebaseStorage.getInstance("gs://bookstore-9345c.appspot.com")
        sRef = fStore.reference

        binding.apply {
            btSpApply.setOnClickListener {
                addImage(imageUri)

                Snackbar.make(it, "Change Successfully", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(resources.getColor(R.color.spaceCadet))
                    .setTextColor(resources.getColor(R.color.brightGray))
                    .show()
            }

            ivSpFotoProfil.setOnClickListener { selectImage() }

            actionbarAndStatusbar()
        }
    }

    private fun selectImage() {
        val intent = Intent()

        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            binding.ivSpFotoProfil.setImageURI(imageUri)
        }
    }

    private fun addImage(imageUri: Uri) {
        binding.apply {
            val uidImage = UUID.randomUUID().toString()

            val imageRef = sRef.child("uploads/images/${uidImage}.jpg")

            uploadTask = imageRef.putFile(imageUri)

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uidUser = fAuth.currentUser?.uid.toString()

                    val downloadUri = task.result
                    val fotoProfil = downloadUri.toString()

                    ivSpFotoProfil.setImageResource(R.drawable.ic_account)

                    ivSpFotoProfil.setOnClickListener { selectImage() }

                    val pathFotoProfil = dbRef.child("user").child(uidUser).child("fotoProfil")

                    pathFotoProfil.setValue(fotoProfil)

                    showToast("Image uploaded successfully")
                } else {
                    showToast("Image failed to upload")
                }
            }
        }
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
        val abTitle = (Html.fromHtml("<font color=\"#1A374D\">" + "Setting Account" + "</font>"))
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