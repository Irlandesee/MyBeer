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
import it.uninsubria.mybeer.datamodel.User
import it.uninsubria.mybeer.dbHandler.DatabaseHandler
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {

    private val DATABASE_NAME = "https://mybeer-f68c5-default-rtdb.europe-west1.firebasedatabase.app"
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginRedirect: TextView
    private lateinit var etName: TextView
    private lateinit var etSurname: TextView
    private val dbHandler: DatabaseHandler = DatabaseHandler(this, FirebaseDatabase.getInstance(DATABASE_NAME))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        etUsername = findViewById(R.id.et_username)
        etEmail = findViewById(R.id.et_email)
        etName = findViewById(R.id.et_name)
        etSurname = findViewById(R.id.et_surname)
        etPassword = findViewById(R.id.et_password)
        btnRegister = findViewById(R.id.btn_register)
        tvLoginRedirect = findViewById(R.id.tv_login_redirect)

        btnRegister.setOnClickListener {
            if(validateInputs()){
                val user = User(
                    etName.text.toString().trim(),
                    etSurname.text.toString().trim(),
                    etUsername.text.toString().trim(),
                    etPassword.text.toString().trim()
                )
                dbHandler.addUser(user)
                Toast.makeText(baseContext, "User creation successful, please login!", Toast.LENGTH_LONG).show()
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            }
        }

        tvLoginRedirect.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(): Boolean {
        val username = etUsername.text.toString().trim()
        val name = etName.text.toString().trim()
        val surname = etSurname.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Initialize validation status
        var isValid = true

        // Validate username (not empty and minimum length)
        if (username.isEmpty() || username.length < 3) {
            etUsername.error = "Username must be at least 3 characters"
            isValid = false
        }

        // Validate name (not empty)
        if (name.isEmpty()) {
            etName.error = "Name cannot be empty"
            isValid = false
        }

        // Validate surname (not empty)
        if (surname.isEmpty()) {
            etSurname.error = "Surname cannot be empty"
            isValid = false
        }
        // Validate email (email format)
        if (!isValidEmail(email)) {
            etEmail.error = "Invalid email format"
            isValid = false
        }

        // Validate password (minimum length and complexity)
        if (password.isEmpty() || password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else if (!isValidPassword(password)) {
            etPassword.error = "Password must contain at least one digit and one uppercase letter"
            isValid = false
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        // Basic email pattern for validation
        val emailPattern = Pattern.compile(
            "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        )
        return emailPattern.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        // Password should have at least one digit and one uppercase letter
        val hasUppercase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        return hasUppercase && hasDigit
    }
}