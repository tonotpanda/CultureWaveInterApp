package com.example.culturewaveinter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturewaveinter.Adapters.EventAdapter
import com.example.culturewaveinter.Api.ApiRepository
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Space
import com.example.culturewaveinter.Entities.User
import com.google.gson.Gson
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FragmentHome : Fragment(R.layout.fragmenthome) {

    private lateinit var spaceList: List<Space>
    private val eventsList: MutableList<Event> = mutableListOf()
    private lateinit var eventAdapter: EventAdapter

    private lateinit var imageViewNewEvent: ImageView
    private var currentUser: User? = null


    private lateinit var newEventLauncher: ActivityResultLauncher<Intent>

    companion object {
        fun newInstance(user: User): FragmentHome {
            val fragment = FragmentHome()
            val args = Bundle()
            args.putSerializable("user", user)
            fragment.arguments = args
            return fragment
        }
    }

    private fun loadJsonFromAsset(context: Context, filename: String): String {
        val inputStream = context.assets.open(filename)
        val reader = InputStreamReader(inputStream)
        return reader.readText()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseEventsJson(context: Context): List<Event> {
        val json = loadJsonFromAsset(context, "Events.json")
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val gsonWithDate = Gson().newBuilder().registerTypeAdapter(
            LocalDateTime::class.java, JsonDeserializer<LocalDateTime> { json, _, _ ->
                LocalDateTime.parse(json.asString, formatter)
            }).create()
        val type: Type = object : TypeToken<List<Event>>() {}.type
        return gsonWithDate.fromJson(json, type)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Registramos el lanzador para StartActivityForResult
        newEventLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
                                                    ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val nuevoEvento = result.data?.getSerializableExtra("nuevoEvento") as? Event
                if (nuevoEvento != null) {
                    // Añadimos y notificamos al adapter
                    eventsList.add(nuevoEvento)
                    eventAdapter.notifyItemInserted(eventsList.size - 1)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = arguments?.getSerializable("user") as? User

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewEventos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        imageViewNewEvent = view.findViewById(R.id.buttonNewEvents)
        if (currentUser?.rol == 3) {
            imageViewNewEvent.visibility = View.GONE
        }

        lifecycleScope.launch {
            val spacesFromApi = ApiRepository.getSpaces()
            if (spacesFromApi != null) {
                spaceList = spacesFromApi

                val parsedEvents = parseEventsJson(requireContext())
                eventsList.addAll(parsedEvents)

                eventAdapter = EventAdapter(eventsList, spaceList)
                recyclerView.adapter = eventAdapter

                imageViewNewEvent.setOnClickListener {
                    val intent = Intent(requireContext(), crearEventosActivity::class.java)
                    intent.putExtra("spaceList", ArrayList(spaceList))
                    intent.putExtra("user", currentUser)
                    newEventLauncher.launch(intent)
                }
            } else {
                // Manejo de error si no se pudo cargar desde la API
                Toast.makeText(
                    requireContext(),
                    "Error al cargar espacios desde API",
                    Toast.LENGTH_SHORT
                              ).show()
            }
        }
    }
}