package it.uninsubria.mybeer.activities

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.datamodel.Beer
import it.uninsubria.mybeer.datamodel.Rating
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import java.io.File
import java.security.MessageDigest
import java.util.Calendar
import kotlin.text.Charsets.UTF_8

class StarRatingActivity() : AppCompatActivity(){

    private val DATABASE_NAME = "https://mybeer-f68c5-default-rtdb.europe-west1.firebasedatabase.app"
    private lateinit var firebaseDb: FirebaseDatabase
    private lateinit var dbHandler: DatabaseHandler

    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var starViews: List<ImageView>
    private var selectedRating: Int = 0
    private lateinit var btnDrunkDate: Button
    private lateinit var tvSelectedDate: TextView
    private var selectedDate: String  = ""
    private lateinit var etDrinkingLocation: EditText
    private var beer: Beer? = null
    private lateinit var ivPicture: ImageView
    private lateinit var photoFile: File
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var btnTakePhoto: Button

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_beer_rating_layout)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.beer_add_rating)){v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseDb = FirebaseDatabase.getInstance(DATABASE_NAME)
        dbHandler = DatabaseHandler(baseContext, firebaseDb)

        initViews()
        initStars()
        setupDatePicker()
        val extras: Bundle? = intent.extras
        if(extras != null){
            beer = extras.getSerializable("selected_beer") as Beer
        }

        photoFile = createImageFile()
        takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ result ->
            if(result.resultCode  == RESULT_OK){
                ivPicture.setImageBitmap(BitmapFactory.decodeFile(photoFile.absolutePath))

            }
        }
        val fab: FloatingActionButton = findViewById(R.id.floating_button)
        fab.setOnClickListener{
            setResult(RESULT_OK)
            finish()
        }

        loadBeerDetails(beer)
    }

    private fun updateStarRating(rating: Int){
        selectedRating = rating
        starViews.forEachIndexed{index, imageView ->
            imageView.setImageResource(
                if (index < rating) R.drawable.ic_star // Filled star
                else R.drawable.ic_star_border
            )
        }
        //Toast.makeText(this, "Beer rated $selectedRating", Toast.LENGTH_LONG).show()
    }

    fun getRating(): Int{
        return selectedRating
    }

    private fun initViews() {
        btnDrunkDate = findViewById(R.id.btn_drunk_date)
        etDrinkingLocation = findViewById(R.id.et_drinking_location)
        tvSelectedDate = findViewById(R.id.tv_drunk_date)
        ivPicture = findViewById(R.id.iv_picture)

        // Initialize submit button
        val btnSubmit = findViewById<Button>(R.id.btn_submit_rating)
        btnSubmit.setOnClickListener { submitRating() }

        btnTakePhoto = findViewById<Button>(R.id.btn_take_photo)
        btnTakePhoto.setOnClickListener{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
            }else{
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val photoUri = FileProvider.getUriForFile(this, "it.uninsubria.mybeer.fileprovider", photoFile)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                try{
                    takePictureLauncher.launch(intent)
                }catch(e: Exception){
                    Toast.makeText(this, "Errore nell'apertura della fotocamera: ${e.message}", Toast.LENGTH_LONG).show()
                }

            }

        }
    }


    private fun createImageFile(): File{
        val imageFileName = "JPEG_${System.currentTimeMillis()}_"
        return File.createTempFile(imageFileName, ".jpg", getExternalFilesDir(null))
    }


    private fun initStars() {
        starViews = listOf(
            findViewById(R.id.star_1),
            findViewById(R.id.star_2),
            findViewById(R.id.star_3),
            findViewById(R.id.star_4),
            findViewById(R.id.star_5)
        )

        starViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                updateStarRating(index + 1)
            }
        }
    }

    private fun setupDatePicker() {
        btnDrunkDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Format the selected date as "YYYY-MM-DD"
                selectedDate = "$selectedYear-${String.format("%02d", selectedMonth + 1)}-${String.format("%02d", selectedDay)}"
                tvSelectedDate.text = "Data selezionata: $selectedDate"
            }, year, month, day)

            datePicker.show()
        }
    }

    fun setBeerDetails(beer: Beer){
        this.beer = beer
    }

    private fun loadBeerDetails(beer: Beer?) {
        if (beer != null) {
            findViewById<TextView>(R.id.tvBeerName)?.text = beer.beer_name
            findViewById<TextView>(R.id.tvBeerStyle)?.text = beer.beer_style
            findViewById<TextView>(R.id.tvBeerBrewery)?.text = beer.beer_brewery
            findViewById<TextView>(R.id.tvBeerAbv)?.text = beer.beer_abv
            findViewById<TextView>(R.id.tvBeerDesc)?.text = beer.beer_desc
            val ivPicture = findViewById<ImageView>(R.id.iv_picture)
            val requestOption = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
            Glide.with(baseContext)
                .applyDefaultRequestOptions(requestOption)
                .load(beer.beer_picture_link)
                .into(ivPicture)
        }
    }

    private fun submitRating(){
        if(selectedRating == 0){
            Toast.makeText(this, "Dai un voto", Toast.LENGTH_LONG).show()
            return;
        }
        val drunkDate = selectedDate
        val drinkingLocation = etDrinkingLocation.text.toString()
        val rating: Rating = Rating(beer,
            beer?.beer_name?.let { hashString(it).toHex() }, selectedRating, drunkDate, drinkingLocation, photoFile.absolutePath)

        beer?.let { dbHandler.addRating(it, rating) }

        Toast.makeText(this, "Rating submitted: $selectedRating", Toast.LENGTH_LONG).show()
        btnDrunkDate.text = "Seleziona data"
        etDrinkingLocation.text.clear()
        selectedRating = 0
        updateStarRating(selectedRating)
    }

    private fun ByteArray.toHex() = joinToString(separator = ""){byte -> "%02x".format(byte)}
    private fun hashString(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

}