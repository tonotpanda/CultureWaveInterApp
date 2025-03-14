package com.example.culturewaveinter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class crearEventosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_eventos_layout)

        // Obtén el BottomNavigationView desde el layout
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Configura el listener para manejar las selecciones de íconos
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, FragmentActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_events -> {
                    val intent = Intent(this, FragmentEvents::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_chat -> {
                    val intent = Intent(this, FragmentChat::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, FragmentProfile::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}