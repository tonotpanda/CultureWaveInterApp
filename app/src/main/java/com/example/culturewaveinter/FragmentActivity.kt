package com.example.culturewaveinter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navbar)

        // Obtén el BottomNavigationView desde el layout
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Configura el listener para manejar las selecciones de íconos
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Cargar el fragmento de Home
                    loadFragment(FragmentHome())
                    true
                }
                R.id.navigation_events -> {
                    // Cargar el fragmento de Events (o el que desees)
                    loadFragment(FragmentEvents())
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
            loadFragment(FragmentHome()) // El fragmento que desees mostrar al inicio
        }
    }

    // Función para cargar el fragmento
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment) // Asegúrate de que R.id.fragmentContainer sea el contenedor de tu fragmento
        transaction.addToBackStack(null) // Esto permite que puedas regresar al fragmento anterior
        transaction.commit()
    }
}
