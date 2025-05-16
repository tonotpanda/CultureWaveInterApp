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
import java.io.File
import java.io.FileOutputStream
import java.util.*

class FragmentProfile : Fragment(R.layout.fragmentprofile) {

    private var currentUser: User? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private val REQUEST_VIDEO_CAPTURE = 3
    private val REQUEST_VIDEO_GALLERY = 4
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
        val videoViewProfile = view.findViewById<VideoView>(R.id.profileVideo)

        currentUser?.profilePicture?.let { urlOrUri ->
            if (urlOrUri.endsWith(".mp4") || urlOrUri.contains("video")) {
                imageViewProfile.visibility = View.GONE
                videoViewProfile.visibility = View.VISIBLE
                videoViewProfile.setVideoURI(Uri.parse(urlOrUri))
                videoViewProfile.setOnPreparedListener {
                    it.isLooping = true
                    videoViewProfile.start()
                }
            } else {
                videoViewProfile.visibility = View.GONE
                imageViewProfile.visibility = View.VISIBLE
                Glide.with(this)
                    .load(urlOrUri)
                    .circleCrop()
                    .into(imageViewProfile)
            }
        }

        editTextEmail.setText(currentUser?.email)
        editTextName.setText(currentUser?.name)
        editTextNewPassword.setText(currentUser?.password)
        editTextEmail.isEnabled = false
        editTextName.isEnabled = false

        imageViewProfile.setOnClickListener {
            showMediaTypeDialog()
        }

        videoViewProfile.setOnClickListener {
            showMediaTypeDialog()
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

    private fun showMediaTypeDialog() {
        val options = arrayOf("Foto", "Video")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("¿Qué quieres usar como perfil?")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> showImageSourceDialog()
                1 -> showVideoSourceDialog()
            }
        }
        builder.show()
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Cámara", "Galería")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Selecciona una opción para la foto")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun showVideoSourceDialog() {
        val options = arrayOf("Cámara", "Galería")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Selecciona una opción para el video")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openVideoCamera()
                1 -> openVideoGallery()
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

    private fun openVideoCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE)
        }
    }

    private fun openVideoGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*"
        startActivityForResult(intent, REQUEST_VIDEO_GALLERY)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val imageViewProfile = view?.findViewById<ImageView>(R.id.profilePicture)
            val videoViewProfile = view?.findViewById<VideoView>(R.id.profileVideo)

            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val bitmap = data.extras?.get("data") as? Bitmap ?: return
                    val uri = saveBitmapToTempUri(bitmap)
                    uri?.let {
                        videoViewProfile?.visibility = View.GONE
                        imageViewProfile?.visibility = View.VISIBLE
                        updateProfileMedia(it, isVideo = false, imageViewProfile, videoViewProfile)
                    }
                }

                REQUEST_IMAGE_GALLERY -> {
                    val uri = data.data ?: return
                    videoViewProfile?.visibility = View.GONE
                    imageViewProfile?.visibility = View.VISIBLE
                    updateProfileMedia(uri, isVideo = false, imageViewProfile, videoViewProfile)
                }

                REQUEST_VIDEO_CAPTURE, REQUEST_VIDEO_GALLERY -> {
                    val uri = data.data ?: return
                    imageViewProfile?.visibility = View.GONE
                    videoViewProfile?.visibility = View.VISIBLE
                    updateProfileMedia(uri, isVideo = true, imageViewProfile, videoViewProfile)
                }
            }
        }
    }

    private fun copyUriToInternalStorage(uri: Uri, filename: String): Uri? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val file = File(requireContext().filesDir, filename)
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateProfileMedia(uri: Uri, isVideo: Boolean, imageViewProfile: ImageView?, videoViewProfile: VideoView?) {
        val fileName = if (isVideo) "profile_video_${System.currentTimeMillis()}.mp4"
        else "profile_image_${System.currentTimeMillis()}.jpg"
        val savedUri = copyUriToInternalStorage(uri, fileName)

        if (savedUri == null) {
            Toast.makeText(requireContext(), "Error al guardar el archivo local", Toast.LENGTH_SHORT).show()
            return
        }

        currentUser?.let { user ->
            val updatedUser = user.copy(profilePicture = savedUri.toString())

            lifecycleScope.launch {
                val success = ApiRepository.updateUser(updatedUser)
                if (success) {
                    currentUser = updatedUser

                    if (isVideo) {
                        videoViewProfile?.apply {
                            setVideoURI(savedUri)
                            setOnPreparedListener {
                                it.isLooping = true
                                start()
                            }
                            visibility = View.VISIBLE
                        }
                    } else {
                        imageViewProfile?.let {
                            Glide.with(this@FragmentProfile).load(savedUri).circleCrop().into(it)
                            it.visibility = View.VISIBLE
                        }
                    }

                    Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    (activity as? FragmentActivity)?.updateCurrentUser(updatedUser)
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
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

    /*
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
            val success = ApiRepository.cancelReserve(reserveId)
            if (success) {
                Toast.makeText(requireContext(), "Reserva cancelada correctamente", Toast.LENGTH_SHORT).show()
                loadReserves()
            } else {
                Toast.makeText(requireContext(), "Error al cancelar la reserva", Toast.LENGTH_SHORT).show()
            }
        }
    }
    */
}
