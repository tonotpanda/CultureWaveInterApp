package com.example.culturewaveinter

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.culturewaveinter.Entities.Event
import com.example.culturewaveinter.Entities.User
import com.google.android.material.bottomnavigation.BottomNavigationView

class FragmentActivity : AppCompatActivity() {

    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navbar)

        currentUser = intent.getSerializableExtra("user") as? User ?: run {
            Toast.makeText(this, "Error al cargar usuario", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Toast.makeText(this, "Hola, ${currentUser.name}", Toast.LENGTH_SHORT).show()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(FragmentHome.newInstance(currentUser))
                    true
                }
                R.id.navigation_events -> {
                    loadFragment(FragmentCalendar())
                    true
                }
                R.id.navigation_chat -> {
                    loadFragment(FragmentChat())
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(FragmentProfile.newInstance(currentUser))
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            val fragmentToLoad = intent.getStringExtra("fragmentToLoad") ?: "home"

            when (fragmentToLoad) {
                "home" -> {
                    val fragmentHome = FragmentHome.newInstance(currentUser)

                    // Comprobamos si se pasó un nuevo evento
                    val nuevoEvento = intent.getSerializableExtra("nuevoEvento") as? Event
                    if (nuevoEvento != null) {
                        val bundle = Bundle()
                        bundle.putSerializable("nuevoEvento", nuevoEvento)
                        fragmentHome.arguments?.putAll(bundle)
                    }

                    loadFragment(fragmentHome)
                    bottomNavigationView.selectedItemId = R.id.navigation_home
                }
                "calendar" -> {
                    loadFragment(FragmentCalendar())
                    bottomNavigationView.selectedItemId = R.id.navigation_events
                }
                "chat" -> {
                    loadFragment(FragmentChat())
                    bottomNavigationView.selectedItemId = R.id.navigation_chat
                }
                "profile" -> {
                    loadFragment(FragmentProfile.newInstance(currentUser))
                    bottomNavigationView.selectedItemId = R.id.navigation_profile
                }
            }
        }
    }

    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
