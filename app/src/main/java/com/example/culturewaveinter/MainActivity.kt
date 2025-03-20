package com.example.culturewaveinter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.culturewaveinter.Entities.User
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private val users = arrayOf(
        User(1, "Admin", "admin@example.com", "1234", 1),
        User(2, "User", "user@example.com", "1234", 2)
   )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        buttonRegister.setOnClickListener{
            val intent = Intent(this, RegisUserActivity::class.java)
            startActivity(intent)
        }

        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogIn)

        buttonLogin.setOnClickListener{
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = users.find { it.email == email && it.password == password }

            if (user != null) {
                val intent = Intent(this, FragmentActivity::class.java)
                intent.putExtra("user", user)
                startActivity(intent)
                finish()
            }else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}