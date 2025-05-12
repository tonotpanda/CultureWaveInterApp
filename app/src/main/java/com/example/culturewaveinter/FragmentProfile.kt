package com.example.culturewaveinter

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.culturewaveinter.Api.ApiRepository
import com.example.culturewaveinter.Entities.User
import kotlinx.coroutines.launch

class FragmentProfile : Fragment(R.layout.fragmentprofile) {

    private var currentUser: User? = null

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

        val editTextName = view.findViewById<EditText>(R.id.editTextNombreUsuario)
        val editTextEmail = view.findViewById<EditText>(R.id.editTextBoxMail)
        val editTextNewPassword = view.findViewById<EditText>(R.id.editTextBoxChangePassword)
        val btnChangePassword = view.findViewById<Button>(R.id.btnChangePassword)
        val btnLogOut = view.findViewById<Button>(R.id.btnLogOut)

        // Mostrar los datos pero sin permitir editar nombre ni email
        editTextEmail.setText(currentUser?.email)
        editTextName.setText(currentUser?.name)
        editTextNewPassword.setText(currentUser?.password) // Agregamos el de la contraseña, pero se puede cambiar
        editTextEmail.isEnabled = false
        editTextName.isEnabled = false

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
}
