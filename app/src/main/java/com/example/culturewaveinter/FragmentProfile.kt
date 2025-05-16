package com.example.culturewaveinter

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.culturewaveinter.Adapters.ReservesAdapter
import com.example.culturewaveinter.Api.ApiRepository
import com.example.culturewaveinter.Entities.User
import kotlinx.coroutines.launch
import java.util.*

class FragmentProfile : Fragment(R.layout.fragmentprofile) {

    private var currentUser: User? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private lateinit var reservesAdapter: ReservesAdapter
    private lateinit var recyclerViewReserves: RecyclerView

    companion object {
        fun newInstance(user: User): FragmentProfile {
            val fragment = FragmentProfile()
            val args = Bundle()
            args.putSerializable("user", user)
            fragment.arguments = args
            return fragment
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = arguments?.getSerializable("user") as? User

        recyclerViewReserves = view.findViewById(R.id.reservesUser)
        recyclerViewReserves.layoutManager = LinearLayoutManager(requireContext())
        // loadReserves()

        val editTextName = view.findViewById<EditText>(R.id.editTextNombreUsuario)
        val editTextEmail = view.findViewById<EditText>(R.id.editTextBoxMail)
        val editTextNewPassword = view.findViewById<EditText>(R.id.editTextBoxChangePassword)
        val btnChangePassword = view.findViewById<Button>(R.id.btnChangePassword)
        val btnLogOut = view.findViewById<Button>(R.id.btnLogOut)
        val imageViewProfile = view.findViewById<ImageView>(R.id.profilePicture)

        currentUser?.profilePicture?.let { urlOrUri ->
            Glide.with(this)
                .load(urlOrUri)
                .circleCrop()
                .into(imageViewProfile)
        }

        editTextEmail.setText(currentUser?.email)
        editTextName.setText(currentUser?.name)
        editTextNewPassword.setText(currentUser?.password)
        editTextEmail.isEnabled = false
        editTextName.isEnabled = false

        imageViewProfile.setOnClickListener {
            showImageSourceDialog()
        }

        view.findViewById<ImageView>(R.id.esFlagImg).setOnClickListener { setLanguage("es") }
        view.findViewById<ImageView>(R.id.catFlagImg).setOnClickListener { setLanguage("ca") }
        view.findViewById<ImageView>(R.id.enFlagImg).setOnClickListener { setLanguage("en") }

        btnChangePassword.setOnClickListener {
            val newPassword = editTextNewPassword.text.toString().trim()

            if (newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Ingrese una nueva contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentUser?.let { user ->
                if (newPassword == user.password) {
                    Toast.makeText(requireContext(), "La nueva contraseña no puede ser igual a la actual", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val updatedUser = user.copy(password = newPassword)

                lifecycleScope.launch {
                    val success = ApiRepository.updateUser(updatedUser)
                    if (success) {
                        Toast.makeText(requireContext(), "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                        currentUser = updatedUser
                        editTextNewPassword.text.clear()

                        (activity as? FragmentActivity)?.updateCurrentUser(updatedUser)
                    } else {
                        Toast.makeText(requireContext(), "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        btnLogOut.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    /*@RequiresApi(Build.VERSION_CODES.O)
    private fun loadReserves() {
        currentUser?.let { user ->
            lifecycleScope.launch {
                val reserves = ApiRepository.getUserReserves(user.id)
                if (reserves.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "No tienes reservas", Toast.LENGTH_SHORT).show()
                } else {
                    reservesAdapter = ReservesAdapter(reserves) { reserveId ->
                        cancelReserve(reserveId)
                    }
                    recyclerViewReserves.adapter = reservesAdapter
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cancelReserve(reserveId: Int) {
        lifecycleScope.launch {
            val success = ApiRepository.cancelReserve(reserveId)
            if (success) {
                Toast.makeText(requireContext(), "Reserva cancelada correctamente", Toast.LENGTH_SHORT).show()
                loadReserves()
            } else {
                Toast.makeText(requireContext(), "Error al cancelar la reserva", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

    private fun showImageSourceDialog() {
        val options = arrayOf("Cámara", "Galería")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Selecciona una opción")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val imageViewProfile = view?.findViewById<ImageView>(R.id.profilePicture)
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val bitmap = data.extras?.get("data") as? Bitmap ?: return
                    val uri = saveBitmapToTempUri(bitmap)
                    uri?.let {
                        updateProfilePicture(it, imageViewProfile)
                    }
                }

                REQUEST_IMAGE_GALLERY -> {
                    val uri = data.data ?: return
                    updateProfilePicture(uri, imageViewProfile)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateProfilePicture(uri: Uri, imageViewProfile: ImageView?) {
        currentUser?.let { user ->
            val updatedUser = user.copy(profilePicture = uri.toString())
            lifecycleScope.launch {
                val success = ApiRepository.updateUser(updatedUser)
                if (success) {
                    currentUser = updatedUser
                    imageViewProfile?.let {
                        Glide.with(this@FragmentProfile).load(uri).circleCrop().into(it)
                    }
                    Toast.makeText(requireContext(), "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()

                    // Actualiza también en la actividad
                    (activity as? FragmentActivity)?.updateCurrentUser(updatedUser)
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar la foto de perfil", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveBitmapToTempUri(bitmap: Bitmap): Uri? {
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            "temp_profile_image",
            null
                                                      )
        return Uri.parse(path)
    }

    private fun setLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        requireContext().resources.updateConfiguration(config, requireContext().resources.displayMetrics)
        activity?.recreate()
        Toast.makeText(requireContext(), "Idioma cambiado a $languageCode", Toast.LENGTH_SHORT).show()
    }
}
