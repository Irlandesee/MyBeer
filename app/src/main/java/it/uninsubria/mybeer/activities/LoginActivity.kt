package it.uninsubria.mybeer.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import it.uninsubria.mybeer.R
import it.uninsubria.mybeer.dbHandler.DatabaseHandler


class LoginActivity() : AppCompatActivity() {

    //private lateinit var etEmail: EditText
    private val DATABASE_NAME = "https://mybeer-f68c5-default-rtdb.europe-west1.firebasedatabase.app"
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegisterRedirect: TextView
    private val dbHandler: DatabaseHandler = DatabaseHandler(this, FirebaseDatabase.getInstance(DATABASE_NAME))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //etEmail = findViewById(R.id.et_email)
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        tvRegisterRedirect = findViewById(R.id.tv_register_redirect)

        btnLogin.setOnClickListener {
            validateLogin()
        }

        tvRegisterRedirect.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validate that fields are not empty
        when {
            username.isEmpty() -> {
                etUsername.error = "Username cannot be empty"
                return
            }
            password.isEmpty() -> {
                etPassword.error = "Password cannot be empty"
                return
            }
        }

        // Check credentials in the database
        if (dbHandler.checkCredentials(username, password)) {
            // If credentials are valid, proceed to the next screen
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            // Navigate to the home screen or another part of your app
            // val intent = Intent(this, HomeActivity::class.java)
            // startActivity(intent)
        } else {
            // Show error message
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
        }
    }
}