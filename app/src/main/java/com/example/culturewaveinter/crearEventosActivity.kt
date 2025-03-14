package com.example.culturewaveinter

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class crearEventosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_eventos_layout)

        // Encontrar el botón de "volver"
        val backButton = findViewById<ImageView>(R.id.back)

        backButton.setOnClickListener {
            // Crear un Intent para abrir FragmentActivity
            val intent = Intent(this, FragmentActivity::class.java)
            startActivity(intent) // Iniciar FragmentActivity
            finish() // Finaliza la actividad actual (crearEventosActivity) para evitar volver a ella
        }
    }
}
