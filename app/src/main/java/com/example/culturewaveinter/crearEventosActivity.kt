package com.example.culturewaveinter

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class crearEventosActivity : AppCompatActivity() {

    private lateinit var tvFechaInicio: TextView
    private lateinit var btnSeleccionarFechaHora: Button
    private lateinit var calendar: Calendar
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var hour: Int = 0
    private var minute: Int = 0

    @SuppressLint("MissingInflatedId")
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

        // Coger la info de la descripción del evento
        val descripcionEvento =
            findViewById<EditText>(R.id.editTextDescripcionEvento).text.toString()

        // Inicializar vistas
        tvFechaInicio = findViewById(R.id.lbFechaInicio)
        btnSeleccionarFechaHora = findViewById(R.id.btnSeleccionarFechaHoraInicio)


        // Inicializar calendario
        calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
        hour = calendar.get(Calendar.HOUR_OF_DAY)
        minute = calendar.get(Calendar.MINUTE)

        // Configurar el botón para abrir el DatePickerDialog
        btnSeleccionarFechaHora.setOnClickListener {
            mostrarDatePicker()
        }
    }

    private fun mostrarDatePicker() {
        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // Guardar la fecha seleccionada
                year = selectedYear
                month = selectedMonth
                day = selectedDay

                // Mostrar el TimePicker después de seleccionar la fecha
                mostrarTimePicker()
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun mostrarTimePicker() {
        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            // Guardar la hora seleccionada
            hour = selectedHour
            minute = selectedMinute

            // Formatear la hora para que siempre tenga dos dígitos (ejemplo: 09:05 en lugar de 9:5)
            val horaFormateada = String.format("%02d:%02d", hour, minute)

            // Formatear la fecha para que siempre tenga dos dígitos (ejemplo: 01/05/2023 en lugar de 1/5/2023)
            val diaFormateado = String.format("%02d", day)
            val mesFormateado =
                String.format("%02d", month + 1) // Sumar 1 porque los meses comienzan en 0

            // Crear el texto final con el enunciado y la fecha/hora seleccionada
            val textoFinal =
                "Fecha inicio evento: $diaFormateado/$mesFormateado/$year $horaFormateada"

            // Actualizar el TextView con el texto final
            tvFechaInicio.text = textoFinal
        }, hour, minute, true)
        timePickerDialog.show()
    }
}