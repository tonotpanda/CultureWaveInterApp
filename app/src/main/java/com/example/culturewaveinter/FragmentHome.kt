package com.example.culturewaveinter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.Space
import com.example.culturewaveinter.Entities.User
import com.google.gson.Gson
import com.google.gson.JsonDeserializer
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FragmentHome : Fragment(R.layout.fragmenthome) {

    fun loadJsonFromAsset(context: Context, filename: String): String {
        val inputStream = context.assets.open(filename)
        val reader = InputStreamReader(inputStream)
        return reader.readText()
    }

    fun parseSpacesJson(context: Context): List<Space>{
        val json = loadJsonFromAsset(context, "Spaces.json")
        val gson = Gson()
        val type: Type = object : TypeToken<List<Space>>() {}.type
        return gson.fromJson(json, type)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseEventsJson(context: Context): List<Event>{
        val json = loadJsonFromAsset(context, "Events.json")
        val gson = Gson()
        val type: Type = object : TypeToken<List<Event>>() {}.type

        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val gsonWithDate = Gson().newBuilder().registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer<LocalDateTime> { json, _, _ ->
            LocalDateTime.parse(json.asString, formatter)
        }).create()

        return gsonWithDate.fromJson(json, type)
    }

    private lateinit var spaceList: List<Space>
    private lateinit var eventsList: List<Event>


    private lateinit var imageViewNewEvent: ImageView
    private var currentUser: User? = null

    companion object {
        fun newInstance(user: User): FragmentHome {
            val fragment = FragmentHome()
            val args = Bundle()
            args.putSerializable("user", user)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            spaceList = parseSpacesJson(it)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                eventsList = parseEventsJson(it)
            } else {
                eventsList = emptyList() // alternativa segura si versión < Android O
            }
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewEventos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = EventAdapter(eventsList, spaceList)

        currentUser = arguments?.getSerializable("user") as? User
        imageViewNewEvent = view.findViewById(R.id.buttonNewEvents)

        if(currentUser?.rol ==3) {
            imageViewNewEvent.visibility = View.GONE
        }

        // Configura el clic del ImageView
        imageViewNewEvent.setOnClickListener {
                val intent = Intent(activity, crearEventosActivity::class.java)
                startActivity(intent) // Inicia la actividad

        }
    }
}