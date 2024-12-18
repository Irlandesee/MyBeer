package it.uninsubria.mybeer

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import it.uninsubria.mybeer.activities.LoginActivity


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loginActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == RESULT_OK){
                Toast.makeText(baseContext, "Login ok", Toast.LENGTH_LONG).show()
            }
        }

        val loginIntent = Intent(baseContext, LoginActivity::class.java)
        loginActivityLauncher.launch(loginIntent)
    }


}