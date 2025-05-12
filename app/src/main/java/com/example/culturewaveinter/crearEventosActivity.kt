package com.example.culturewaveinter

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.culturewaveinter.Api.ApiRepository
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Space
import com.example.culturewaveinter.Entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

        // Referencias UI
        val spinnerSpaces = findViewById<Spinner>(R.id.spinnerSpaces)
        val editTextNombre = findViewById<EditText>(R.id.editTextNombreEvento)
        val editTextDescripcion = findViewById<EditText>(R.id.editTextDescripcionEvento)
        editTextCapacity = findViewById(R.id.editTextCapacity)

        tvFechaInicio = findViewById(R.id.lbFechaInicio)
        tvFechaFin = findViewById(R.id.lbFechaFin)
        btnSeleccionarFechaInicio = findViewById(R.id.btnSeleccionarFechaHoraInicio)
        btnSeleccionarFechaFin = findViewById(R.id.btnSeleccionarFechaHoraFin)
        btnGuardarEvento = findViewById(R.id.btnGuardarEvento)

        // Recuperamos datos pasados
        currentUser = intent.getSerializableExtra("user") as? User
        spaces = intent.getSerializableExtra("spaceList") as? ArrayList<Space>

        // Configuramos spinner de espacios
        spaces?.let {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                it.map { space -> space.name }
                                      )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerSpaces.adapter = adapter
            spinnerSpaces.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: android.view.View?,
                    position: Int, id: Long
                                           ) {
                    selectedSpace = it[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedSpace = null
                }
            }
        }

        btnSeleccionarFechaFin.isEnabled = false

        // Pickers de fecha/hora
        btnSeleccionarFechaInicio.setOnClickListener { mostrarDateTimePicker(true) }
        btnSeleccionarFechaFin.setOnClickListener { mostrarDateTimePicker(false) }

        // Guardar evento: devolvemos como resultado
        btnGuardarEvento.setOnClickListener {
            val nombre = editTextNombre.text.toString().trim()
            val descripcion = editTextDescripcion.text.toString().trim()
            val capacidadTexto = editTextCapacity.text.toString().trim()
            val capacidad = capacidadTexto.toIntOrNull()

            if (capacidad == null || capacidad <= 0) {
                Toast.makeText(this, "La capacidad debe ser un número válido y mayor que 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedSpace == null || nombre.isEmpty() || descripcion.isEmpty() || fechaInicio == null || fechaFin == null || capacidad == null || capacidad <= 0) {
                Toast.makeText(this, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Aquí no enviamos el idEvent porque la base de datos lo generará automáticamente
            val nuevoEvento = Event(
                idEvent = 0,  // Esto se elimina si la base de datos asigna el ID automáticamente
                name = nombre,
                description = descripcion,
                capacity = capacidad,
                startDate = fechaInicio!!,
                endDate = fechaFin!!,
                status = "Programado",
                idSpace = selectedSpace!!.id
                                   )

            // Log para depurar
            println("Event data: $nuevoEvento")

            // Hacer POST a la API para crear el evento
            CoroutineScope(Dispatchers.Main).launch {
                val createSuccessful = ApiRepository.createEvent(nuevoEvento)  // Llamada para crear el evento
                if (createSuccessful) {
                    // Si la creación fue exitosa, devolvemos el evento
                    val resultIntent = Intent().apply {
                        putExtra("nuevoEvento", nuevoEvento)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    // Si hubo un error, mostramos un mensaje
                    Toast.makeText(this@crearEventosActivity, "Error al guardar el evento. Intenta nuevamente.", Toast.LENGTH_SHORT).show()
                }
            }
        }



        // Back: cancelamos y cerramos
        findViewById<ImageView>(R.id.back).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun mostrarDateTimePicker(esInicio: Boolean) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            TimePickerDialog(this, { _, hourOfDay, minute ->
                val selectedDateTime = LocalDateTime.of(
                    year, month + 1, dayOfMonth, hourOfDay, minute
                                                       )

                if (esInicio) {
                    fechaInicio = selectedDateTime
                    tvFechaInicio.text =
                        "Fecha inicio evento: ${selectedDateTime.format(formatter)}"
                    btnSeleccionarFechaFin.isEnabled = true
                    fechaFin = null
                    tvFechaFin.text = "Fecha fin evento"
                } else {
                    if (fechaInicio == null || !selectedDateTime.isAfter(fechaInicio)) {
                        Toast.makeText(
                            this,
                            "La fecha de fin debe ser posterior a la de inicio",
                            Toast.LENGTH_SHORT
                                      ).show()
                        return@TimePickerDialog
                    }
                    fechaFin = selectedDateTime
                    tvFechaFin.text =
                        "Fecha fin evento: ${selectedDateTime.format(formatter)}"
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
}
