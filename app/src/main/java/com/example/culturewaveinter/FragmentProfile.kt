package com.example.culturewaveinter

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment

class FragmentProfile : Fragment(R.layout.fragmentprofile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinnerGenero: Spinner = view.findViewById(R.id.spinnerGenero)

        // Crear un ArrayAdapter usando el array de strings y un layout por defecto para el Spinner
        val adapter = ArrayAdapter.createFromResource(
            requireContext(), // Usar requireContext() para obtener el contexto del fragmento
            R.array.genero_opciones, // El array de strings que creaste en res/values/strings.xml
            android.R.layout.simple_spinner_item // Layout por defecto para cada item del Spinner
                                                     )

        // Especificar el layout que se usará cuando se desplieguen las opciones
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Aplicar el adapter al Spinner
        spinnerGenero.adapter = adapter

        // Escuchar la selección del usuario
        spinnerGenero.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var isOptionSelected = false // Variable para controlar si se ha seleccionado una opción válida

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Si se selecciona "SELECCIONA UNA OPCIÓN" (posición 0) y no se ha seleccionado previamente,
                // no hacer nada y devolver la selección a "SELECCIONA UNA OPCIÓN"
                if (position == 0 && !isOptionSelected) {
                    println("Por favor selecciona un género válido")
                } else {
                    // Procesar la selección de una opción válida
                    val generoSeleccionado = parent?.getItemAtPosition(position).toString()
                    println("Género seleccionado: $generoSeleccionado")

                    // Marcar que se ha seleccionado una opción válida
                    isOptionSelected = true

                    // Eliminar la opción "SELECCIONA UNA OPCIÓN" de las opciones visibles
                    if (position != 0) {
                        val updatedAdapter = ArrayAdapter.createFromResource(
                            requireContext(),
                            R.array.genero_opciones, // Usar el array de opciones
                            android.R.layout.simple_spinner_item
                                                                            )
                        updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        // Eliminar la primera opción "SELECCIONA UNA OPCIÓN" del adaptador
                        val options = mutableListOf<String>()
                        options.add("Masculino") // Las opciones que quieres mostrar
                        options.add("Femenino")

                        // Crear un nuevo adaptador con las opciones actualizadas
                        val newAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
                        spinnerGenero.adapter = newAdapter
                        newAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Lógica cuando no se ha seleccionado ningún elemento
                println("No se ha seleccionado ningún género")
            }
        }
    }
}
