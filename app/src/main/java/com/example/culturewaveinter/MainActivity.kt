package com.example.culturewaveinter

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.culturewaveinter.Api.ApiRepository.getUsers
import com.example.culturewaveinter.Entities.User
import com.example.culturewaveinter.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.io.Serializable
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var users = mutableListOf<User>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            try {
                val apiUsers = getUsers()
                users = apiUsers?.toMutableList() as MutableList<User>
            }catch (e: Exception)
            {
                println("API Connexion Error")
            }
        }

        binding.buttonRegister.setOnClickListener {
            startActivityForResult(
                Intent(this, RegisUserActivity::class.java),
                1001
                                  )
        }

        binding.buttonLogIn.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingresa correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val encryptedPassword = encryptSHA256(password)
            val user = verifyUser(email, encryptedPassword, users)

            if (user != null) {
                Intent(this, FragmentActivity::class.java).also {
                    it.putExtra("user", user as Serializable)
                    startActivity(it)
                    finish()
                }
            } else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            (data?.getSerializableExtra("newUser") as? User)?.let {
                users.add(it)
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyUser(userName : String, password : String, userList: MutableList<User>) : User?{

        for (user in userList) {
            if (userName == user.email && password == user.password) {
                return user
            }
        }

        return null
    }

    private fun encryptSHA256(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
}
