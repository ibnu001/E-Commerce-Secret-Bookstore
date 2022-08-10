package com.ibnu.tugasakhiribnuadiv

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.ibnu.tugasakhiribnuadiv.databinding.ActivityAddBookSalesBinding
import com.ibnu.tugasakhiribnuadiv.model.BookSell
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.max


class AddBookSalesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBookSalesBinding

    // for image url
    private lateinit var imageUri: Uri

    private lateinit var executor: Executor
    private lateinit var handler: Handler

    // use for firebase storage
    private lateinit var uploadTask: UploadTask
    private lateinit var fStore: FirebaseStorage
    private lateinit var sRef: StorageReference

    // firebase realtime and authentication
    private lateinit var dbRef: DatabaseReference
    private lateinit var fAuth: FirebaseAuth

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddBookSalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase realtime and authentication
        dbRef = FirebaseDatabase.getInstance().reference
        fAuth = FirebaseAuth.getInstance()

        // Firebase Storage
        fStore = FirebaseStorage.getInstance("gs://bookstore-9345c.appspot.com")
        sRef = fStore.reference

        actionbarAndStatusbar()

        /** input **/

        binding.apply {
            etAbsTitle.doOnTextChanged { text, start, before, count -> etlTitle(null) }
            etAbsAuthor.doOnTextChanged { text, start, before, count -> etlAuthor(null) }
            etAbsPublishing.doOnTextChanged { text, start, before, count -> etlPublishing(null) }
            etAbsPages.doOnTextChanged { text, start, before, count -> etlPages(null) }
            etAbsCategory.doOnTextChanged { text, start, before, count -> etlCategory(null) }
            etAbsPrice.doOnTextChanged { text, start, before, count -> etlPrice(null) }
            etAbsDescription.doOnTextChanged { text, start, before, count -> etlStock(null) }
            etAbsStock.doOnTextChanged { text, start, before, count -> etlDescription(null) }

            btAbsSelectImage.setOnClickListener { selectImage() }

            etAbsDescription.setOnTouchListener(OnTouchListener { view, event ->
                if (etAbsDescription.hasFocus()) {
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            view.parent.requestDisallowInterceptTouchEvent(false)
                            return@OnTouchListener true
                        }
                    }
                }
                false
            })

            btAbsSubmit.setOnClickListener {
                val title = etAbsTitle.text.toString()
                val author = etAbsAuthor.text.toString()
                val publishing = etAbsPublishing.text.toString()
                val pages = etAbsPages.text.toString()
                val category = etAbsCategory.text.toString()
                val price = etAbsPrice.text.toString()
                val stock = etAbsStock.text.toString()
                val description = etAbsDescription.text.toString()

                when {
                    title.isEmpty() -> {
                        etlTitle("Please enter the title of the book")
                        etAbsTitle.requestFocus()
                    }
                    author.isEmpty() -> {
                        etlAuthor("Please enter the author of the book")
                        etAbsAuthor.requestFocus()
                    }
                    publishing.isEmpty() -> {
                        etlPublishing("Please enter the publishing of the book")
                        etAbsPublishing.requestFocus()
                    }
                    pages.isEmpty() -> {
                        etlPages("Please enter the pages of the book")
                        etAbsPages.requestFocus()
                    }
                    category.isEmpty() -> {
                        etlCategory("Please enter the category of the book")
                        etAbsCategory.requestFocus()
                    }
                    price.isEmpty() -> {
                        etlPrice("Please enter the price of the book")
                        etAbsPrice.requestFocus()
                    }
                    stock.isEmpty() -> {
                        etlStock("Please enter the stock of the book")
                        etAbsStock.requestFocus()
                    }
                    description.isEmpty() -> {
                        etlDescription("Please enter the description of the book")
                        etAbsDescription.requestFocus()
                    }

                    else -> {
                        addBookSale(imageUri)
                        Snackbar.make(it, "Uploaded Successfully", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(resources.getColor(R.color.spaceCadet))
                            .setTextColor(resources.getColor(R.color.brightGray))
                            .show()

                        etAbsTitle.text?.clear()
                        etAbsAuthor.text?.clear()
                        etAbsPublishing.text?.clear()
                        etAbsPages.text?.clear()
                        etAbsCategory.text?.clear()
                        etAbsPrice.text?.clear()
                        etAbsStock.text?.clear()
                        etAbsDescription.text?.clear()
                    }
                }
            }
            executor = Executors.newSingleThreadExecutor()
            handler = Handler(Looper.getMainLooper())
        }
        /** --- input --- **/
    }

    private fun addBookSale(imageUri: Uri) {
        binding.apply {
            val title = etAbsTitle.text.toString()
            val author = etAbsAuthor.text.toString()
            val publishing = etAbsPublishing.text.toString()
            val pages = etAbsPages.text.toString()
            val category = etAbsCategory.text.toString()
            val price = etAbsPrice.text.toString()
            val stock = etAbsStock.text.toString()
            val description = etAbsDescription.text.toString()

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
                    val downloadUri = task.result
                    val coverBook = downloadUri.toString()

                    ivAbsCover.setImageResource(R.drawable.ic_cover)

                    btAbsSelectImage.setOnClickListener { selectImage() }

                    addBookSaleToDatabase(
                        title,
                        author,
                        publishing,
                        pages,
                        category,
                        price,
                        stock,
                        description,
                        coverBook
                    )

                    showToast("Image uploaded successfully")
                } else {
                    showToast("Image failed to upload")
                }
            }
        }
    }

    private fun addBookSaleToDatabase(
        title: String,
        author: String,
        publishing: String,
        pages: String,
        category: String,
        price: String,
        stock: String,
        description: String,
        coverBook: String,
    ) {
        val uidUser = fAuth.currentUser?.uid.toString()
        val uidSale = UUID.randomUUID().toString()

        dbRef.child("book_sale").child(uidSale)
            .setValue(
                BookSell(
                    uidSale,
                    uidUser,
                    title,
                    author,
                    publishing,
                    pages,
                    category,
                    price,
                    stock,
                    description,
                    coverBook
                )
            )
    }

    /** select image **/
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
            binding.ivAbsCover.setImageURI(imageUri)
        }
    }
    /** --- select image --- **/

    /** Edit Text Layout error message **/
    private fun etlTitle(note: String?) {
        binding.etLAbsTitle.error = note
    }

    private fun etlAuthor(note: String?) {
        binding.etLAbsAuthor.error = note
    }

    private fun etlPublishing(note: String?) {
        binding.etLAbsPublishing.error = note
    }

    private fun etlPages(note: String?) {
        binding.etLAbsPages.error = note
    }

    private fun etlCategory(note: String?) {
        binding.etLAbsCategory.error = note
    }

    private fun etlPrice(note: String?) {
        binding.etLAbsPrice.error = note
    }

    private fun etlStock(note: String?) {
        binding.etLAbsStock.error = note
    }

    private fun etlDescription(note: String?) {
        binding.etLAbsDescription.error = note
    }

    /** --- Edit Text Layout error message --- **/

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
        val abTitle = (Html.fromHtml("<font color=\"#1A374D\">" + "Add Book Sale" + "</font>"))
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(brightGrayCl))
        supportActionBar!!.title = abTitle
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
        supportActionBar!!.elevation = 0f
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        /** window bar / status bar **/
        window.statusBarColor = ContextCompat.getColor(this, R.color.brightGray)
        // change icon color in status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

}