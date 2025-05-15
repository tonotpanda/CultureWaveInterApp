package com.example.culturewaveinter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturewaveinter.Adapters.ReservesAdapter
import com.example.culturewaveinter.Api.ApiRepository
import com.example.culturewaveinter.Entities.ReserveWithEvent
import com.example.culturewaveinter.Entities.User
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
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

        // Configuración del RecyclerView
        recyclerViewReserves = view.findViewById(R.id.reservesUser)
        recyclerViewReserves.layoutManager = LinearLayoutManager(requireContext())

        // Cargar reservas del usuario
        loadReserves()

        val editTextName = view.findViewById<EditText>(R.id.editTextNombreUsuario)
        val editTextEmail = view.findViewById<EditText>(R.id.editTextBoxMail)
        val editTextNewPassword = view.findViewById<EditText>(R.id.editTextBoxChangePassword)
        val btnChangePassword = view.findViewById<Button>(R.id.btnChangePassword)
        val btnLogOut = view.findViewById<Button>(R.id.btnLogOut)
        val imageViewProfile = view.findViewById<ImageView>(R.id.profilePicture)

        loadImageFromPrefs()?.let {
            imageViewProfile.setImageBitmap(it)
        }

        editTextEmail.setText(currentUser?.email)
        editTextName.setText(currentUser?.name)
        editTextNewPassword.setText(currentUser?.password)
        editTextEmail.isEnabled = false
        editTextName.isEnabled = false

        imageViewProfile.setOnClickListener {
            showImageSourceDialog()
        }

        val esFlag = view.findViewById<ImageView>(R.id.esFlagImg)
        val catFlag = view.findViewById<ImageView>(R.id.catFlagImg)
        val enFlag = view.findViewById<ImageView>(R.id.enFlagImg)

        esFlag.setOnClickListener { setLanguage("es") }
        catFlag.setOnClickListener { setLanguage("ca") }
        enFlag.setOnClickListener { setLanguage("en") }

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

    // Cargar las reservas del usuario
    @RequiresApi(Build.VERSION_CODES.O)
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
            // Cancelar la reserva
            val success = ApiRepository.cancelReserve(reserveId)
            if (success) {
                Toast.makeText(requireContext(), "Reserva cancelada correctamente", Toast.LENGTH_SHORT).show()
                loadReserves() // Recargar las reservas después de la cancelación
            } else {
                Toast.makeText(requireContext(), "Error al cancelar la reserva", Toast.LENGTH_SHORT).show()
            }
        }
    }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            val imageViewProfile = view?.findViewById<ImageView>(R.id.profilePicture)
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    val squareBitmap = cropToSquare(imageBitmap)
                    imageViewProfile?.setImageBitmap(squareBitmap)
                    saveImageToPrefs(squareBitmap)
                }

                REQUEST_IMAGE_GALLERY -> {
                    val selectedImageUri: Uri = data.data ?: return
                    val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val squareBitmap = cropToSquare(bitmap)
                    imageViewProfile?.setImageBitmap(squareBitmap)
                    saveImageToPrefs(squareBitmap)
                }
            }
        }
    }

    private fun cropToSquare(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val xOffset = (bitmap.width - size) / 2
        val yOffset = (bitmap.height - size) / 2
        return Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun base64ToBitmap(base64Str: String): Bitmap {
        val bytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun saveImageToPrefs(bitmap: Bitmap) {
        val prefs = requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("profile_image", bitmapToBase64(bitmap)).apply()
    }

    private fun loadImageFromPrefs(): Bitmap? {
        val prefs = requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
        val base64 = prefs.getString("profile_image", null) ?: return null
        return base64ToBitmap(base64)
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
