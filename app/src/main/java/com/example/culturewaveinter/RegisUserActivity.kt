package com.example.culturewaveinter

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.culturewaveinter.Entities.User
import java.security.MessageDigest

class RegisUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.regist_user)

        val backButton = findViewById<ImageView>(R.id.back)
        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val confPasswordEditText = findViewById<EditText>(R.id.editTextConfirmPassword)
        val confButton = findViewById<Button>(R.id.buttonConfirm)

        backButton.setOnClickListener{
            finish()
        }

        confButton.setOnClickListener{
            val email = emailEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confPassword = confPasswordEditText.text.toString().trim()

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confPassword.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Ingrese un email válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confPassword){
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val encryptedPassword = encryptSHA256(password)

            val newUser = User(
                id = 1,
                name = username,
                email = email,
                password = encryptedPassword,
                rol = 3
            )

            Toast.makeText(this, "Usuario creado", Toast.LENGTH_SHORT).show()
            val resultIntent = Intent()
            resultIntent.putExtra("newUser", newUser)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun encryptSHA256(password: String): String {
        val bytes = password.toByteArray()
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return hash.joinToString("") {"%02x".format(it)}
    }
}