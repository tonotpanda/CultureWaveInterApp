package com.example.culturewaveinter

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.culturewaveinter.Entities.User
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentActivity : AppCompatActivity() {

    private lateinit var currentUser : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navbar)

        currentUser = intent.getSerializableExtra("user") as? User ?: return

        if (currentUser != null) {
            Toast.makeText(this, "Hola, ${currentUser.name}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error al cargar usuario", Toast.LENGTH_SHORT).show()
        }


        // Obtén el BottomNavigationView desde el layout
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Configura el listener para manejar las sselecciones de íconos
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Cargar el fragmento de Home
                    loadFragment(FragmentHome.newInstance(currentUser))
                    true
                }
                R.id.navigation_events -> {
                    // Cargar el fragmento de Events (o el que desees)
                    loadFragment(FragmentCalendar())
                    true
                }
                R.id.navigation_chat -> {
                    // Cargar el fragmento de Chat
                    loadFragment(FragmentChat())
                    true
                }
                R.id.navigation_profile -> {
                    // Cargar el fragmento de Profile
                    loadFragment(FragmentProfile())
                    true
                }
                else -> false
            }
        }

        // Cargar el fragmento inicial (por ejemplo, Home) al abrir la actividad
        if (savedInstanceState == null) {
            loadFragment(FragmentHome.newInstance(currentUser)) // El fragmento que desees mostrar al inicio
        }
    }

    // Función para cargar el fragmento
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
