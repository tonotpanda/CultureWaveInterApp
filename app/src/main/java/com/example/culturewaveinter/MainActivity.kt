package com.example.culturewaveinter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.culturewaveinter.Entities.User
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private val users = mutableListOf(
        User(1, "Admin", "admin@example.com", encryptSHA256("1234"), 2),
        User(2, "User", "user@example.com", encryptSHA256("1234"), 3)
   )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        buttonRegister.setOnClickListener{
            val intent = Intent(this, RegisUserActivity::class.java)
            startActivityForResult(intent, 1001)
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

            val encryptedPassword = encryptSHA256(password)

            val user = users.find { it.email == email && it.password == encryptedPassword }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val newUser = data?.getSerializableExtra("newUser") as? User
            if(newUser != null) {
                users.add(newUser)
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun encryptSHA256(password: String): String{
        val bytes = password.toByteArray()
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return hash.joinToString("") { "%02x".format(it) }
    }
}