package it.uninsubria.mybeer.activities

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isEmpty
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.datamodel.Report
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportBeerActivity : AppCompatActivity() {
    private val DATABASE_NAME = "https://mybeer-f68c5-default-rtdb.europe-west1.firebasedatabase.app"
    private lateinit var firebaseDb: FirebaseDatabase
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var sqliteHandler: DatabaseHandler

    private lateinit var photoFile: File
    private lateinit var editBeerName: EditText
    private lateinit var spinnerBeerStyle: Spinner
    private lateinit var editBeerBrewery: EditText
    private lateinit var editBeerNotes: EditText
    private lateinit var ivBeerPhoto: ImageView

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report_beer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)){v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        firebaseDb = FirebaseDatabase.getInstance(DATABASE_NAME)
        sqliteHandler = DatabaseHandler(baseContext, firebaseDb)
        photoFile = createImageFile()

        editBeerName = findViewById(R.id.edit_beer_name)
        spinnerBeerStyle = findViewById(R.id.spinner_beer_style)
        val beerStyles = sqliteHandler.getAllBeerCategories()
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, beerStyles.map{it.value})
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBeerStyle.adapter = spinnerAdapter

        editBeerBrewery = findViewById(R.id.edit_beer_brewery)
        editBeerNotes = findViewById(R.id.edit_beer_notes)
        floatingActionButton = findViewById(R.id.fam_report_beer)
        ivBeerPhoto = findViewById(R.id.iv_beer_picture)

        val popupMenu = PopupMenu(baseContext, floatingActionButton)
        popupMenu.menuInflater.inflate(R.menu.report_beer_menu, popupMenu.menu)
        val menuItemTakePhoto = popupMenu.menu.findItem(R.id.fam_take_photo)
        val menuItemSaveReport = popupMenu.menu.findItem(R.id.fam_save_report)
        val menuItemBack = popupMenu.menu.findItem(R.id.fam_back_main)

        val takePictureLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
            result ->
            if(result.resultCode == RESULT_OK){
                ivBeerPhoto.setImageBitmap(BitmapFactory.decodeFile(photoFile.absolutePath))
            }
        }

        floatingActionButton.setOnClickListener{
            popupMenu.setOnMenuItemClickListener{
                menuItem ->
                    if(menuItem.equals(menuItemTakePhoto)){
                        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
                        }else{
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            val photoUri = FileProvider.getUriForFile(this, "it.uninsubria.mybeer.fileprovider", photoFile)
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                            takePictureLauncher.launch(intent)
                        }
                    }else if(menuItem.equals(menuItemSaveReport)){
                        if(editBeerName.text.isNotEmpty() && editBeerName.text.isNotEmpty() && !spinnerBeerStyle.isEmpty()){

                            val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
                            val id = (1..32)
                                .map{ kotlin.random.Random.nextInt(0, charPool.size)
                                    .let{charPool[it]}}
                                .joinToString("")
                            val intent = Intent()
                            intent.putExtra("it.uninsubria.mybeer.report", Report(
                                id,
                                editBeerName.text.toString(),
                                spinnerBeerStyle.selectedItem.toString(),
                                editBeerBrewery.text.toString(),
                                editBeerNotes.text.toString(),
                                photoFile.absolutePath
                            ))
                            setResult(RESULT_OK, intent)
                            finish()
                        }else{
                            Toast.makeText(baseContext, "Campi non validi", Toast.LENGTH_LONG).show()
                        }
                    }
                    else if(menuItem.equals(menuItemBack)){
                        setResult(RESULT_OK)
                        finish()
                    }
                    true
            }
            popupMenu.show()
        }

    }

    private fun createImageFile(): File{
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss".format(Date()), Locale.getDefault())
        val imageFileName = "PNG_" + timeStamp + "_"
        return File.createTempFile(imageFileName, ".png", filesDir)
    }


}