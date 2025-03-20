package com.example.culturewaveinter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.culturewaveinter.Entities.User

class FragmentHome : Fragment(R.layout.fragmenthome) {


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