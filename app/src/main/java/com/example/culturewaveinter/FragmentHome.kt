package com.example.culturewaveinter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment

class FragmentHome : Fragment(R.layout.fragmenthome) {

    private lateinit var imageViewNewEvent: ImageView // Declara la variable como ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Usa view.findViewById para encontrar el ImageView
        imageViewNewEvent = view.findViewById(R.id.buttonNewEvents)

        // Configura el clic del ImageView
        imageViewNewEvent.setOnClickListener {
            val intent = Intent(activity, crearEventosActivity::class.java)
            startActivity(intent) // Inicia la actividad
        }
    }
}