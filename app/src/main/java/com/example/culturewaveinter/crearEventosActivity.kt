package com.example.culturewaveinter

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.culturewaveinter.Adapters.LocalDateTimeAdapter
import com.example.culturewaveinter.Api.ApiRepository
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Space
import com.example.culturewaveinter.Entities.User
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class crearEventosActivity : AppCompatActivity() {

    private lateinit var tvFechaInicio: TextView
    private lateinit var tvFechaFin: TextView
    private lateinit var btnSeleccionarFechaInicio: Button
    private lateinit var btnSeleccionarFechaFin: Button
    private lateinit var btnGuardarEvento: Button
    private lateinit var editTextCapacity: EditText

    private var fechaInicio: LocalDateTime? = null
    private var fechaFin: LocalDateTime? = null

    private var selectedSpace: Space? = null
    private var spaces: ArrayList<Space>? = null
    private var currentUser: User? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crear_eventos_layout)

        val spinnerSpaces = findViewById<Spinner>(R.id.spinnerSpaces)
        val editTextNombre = findViewById<EditText>(R.id.editTextNombreEvento)
        val editTextDescripcion = findViewById<EditText>(R.id.editTextDescripcionEvento)
        editTextCapacity = findViewById(R.id.editTextCapacity)

        tvFechaInicio = findViewById(R.id.lbFechaInicio)
        tvFechaFin = findViewById(R.id.lbFechaFin)
        btnSeleccionarFechaInicio = findViewById(R.id.btnSeleccionarFechaHoraInicio)
        btnSeleccionarFechaFin = findViewById(R.id.btnSeleccionarFechaHoraFin)
        btnGuardarEvento = findViewById(R.id.btnGuardarEvento)

        currentUser = intent.getSerializableExtra("user") as? User
        spaces = intent.getSerializableExtra("spaceList") as? ArrayList<Space>

        spaces?.let {
            val adapter = ArrayAdapter(
                this, android.R.layout.simple_spinner_item, it.map { space -> space.name }
                                      )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSpaces.adapter = adapter
            spinnerSpaces.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long
                                           ) {
                    selectedSpace = it[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedSpace = null
                }
            }
        }

        btnSeleccionarFechaFin.isEnabled = false

        btnSeleccionarFechaInicio.setOnClickListener { mostrarDateTimePicker(true) }
        btnSeleccionarFechaFin.setOnClickListener { mostrarDateTimePicker(false) }

        btnGuardarEvento.setOnClickListener {
            val nombre = editTextNombre.text.toString().trim()
            val descripcion = editTextDescripcion.text.toString().trim()
            val capacidadTexto = editTextCapacity.text.toString().trim()
            val capacidad = capacidadTexto.toIntOrNull()

            if (capacidad == null || capacidad <= 0) {
                Toast.makeText(this, "La capacidad debe ser un número válido y mayor que 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedSpace == null || nombre.isEmpty() || descripcion.isEmpty() || fechaInicio == null || fechaFin == null) {
                Toast.makeText(this, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoEvento = Event(
                idEvent = 0,
                name = nombre,
                description = descripcion,
                capacity = capacidad,
                startDate = fechaInicio!!,
                endDate = fechaFin!!,
                status = "Programado",
                idSpace = selectedSpace!!.id
                                   )

            // Log para verificar el JSON enviado
            val gson = GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
                .create()

            Log.d("crearEvento", "JSON enviado: ${gson.toJson(nuevoEvento)}")

            lifecycleScope.launch {
                try {
                    val createdEvent = ApiRepository.createEvent(nuevoEvento)

                    if (createdEvent != null) {
                        Log.d("crearEvento", "Evento creado correctamente: ${createdEvent.name}")
                        val resultIntent = Intent().apply {
                            putExtra("nuevoEvento", createdEvent)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Log.e("crearEvento", "Error al crear el evento: respuesta nula")
                        Toast.makeText(this@crearEventosActivity, "Error al guardar el evento. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("crearEvento", "Error en la creación del evento", e)
                    Toast.makeText(this@crearEventosActivity, "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mostrarDateTimePicker(esInicio: Boolean) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            TimePickerDialog(this, { _, hourOfDay, minute ->
                val selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)

                if (esInicio) {
                    fechaInicio = selectedDateTime
                    tvFechaInicio.text = "Fecha inicio evento: ${selectedDateTime.format(formatter)}"
                    btnSeleccionarFechaFin.isEnabled = true
                    fechaFin = null
                    tvFechaFin.text = "Fecha fin evento"
                } else {
                    if (fechaInicio == null || !selectedDateTime.isAfter(fechaInicio)) {
                        Toast.makeText(this, "La fecha de fin debe ser posterior a la de inicio", Toast.LENGTH_SHORT).show()
                        return@TimePickerDialog
                    }
                    fechaFin = selectedDateTime
                    tvFechaFin.text = "Fecha fin evento: ${selectedDateTime.format(formatter)}"
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
}

