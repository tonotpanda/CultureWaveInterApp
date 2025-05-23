package com.example.culturewaveinter

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
import kotlinx.coroutines.launch

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newEventLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
            ) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val nuevoEvento = result.data?.getSerializableExtra("nuevoEvento") as? Event
                if (nuevoEvento != null) {
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
            val eventsFromApi = ApiRepository.getEvents()

            if (spacesFromApi != null && eventsFromApi != null) {
                spaceList = spacesFromApi
                eventsList.addAll(eventsFromApi)

                eventAdapter = EventAdapter(eventsList, spaceList, currentUser!!)
                recyclerView.adapter = eventAdapter

                imageViewNewEvent.setOnClickListener {
                    val intent = Intent(requireContext(), crearEventosActivity::class.java)
                    intent.putExtra("spaceList", ArrayList(spaceList))
                    intent.putExtra("user", currentUser)
                    newEventLauncher.launch(intent)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Error al cargar espacios desde API",
                    Toast.LENGTH_SHORT
                              ).show()
            }
        }
    }
}