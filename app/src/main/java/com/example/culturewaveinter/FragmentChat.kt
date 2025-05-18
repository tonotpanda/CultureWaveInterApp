package com.example.culturewaveinter

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.culturewaveinter.Adapters.ChatAdapter
import com.example.culturewaveinter.Api.ApiRepository
import com.example.culturewaveinter.Entities.AESUtil
import com.example.culturewaveinter.Entities.Message
import com.example.culturewaveinter.Entities.MessageData
import com.example.culturewaveinter.Entities.User
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.*
import java.net.Socket

class FragmentChat : Fragment(R.layout.fragmentchat) {

    private lateinit var editTextMessage: EditText
    private lateinit var btnSendMessage: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter

    private var userId: Int = -1
    private var socket: Socket? = null
    private var writer: BufferedWriter? = null
    private var reader: BufferedReader? = null
    private val gson = Gson()
    private val messages = mutableListOf<Message>()
    private val userCache = mutableMapOf<Int, String>()

    private val serverIP = "10.0.3.8" // Cambia esto si el servidor está en otro lugar
    private val serverPort = 5050

    companion object {
        private const val ARG_USER_ID = "user_id"
        fun newInstance(userId: Int): FragmentChat {
            val fragment = FragmentChat()
            val args = Bundle()
            args.putInt(ARG_USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = arguments?.getInt(ARG_USER_ID) ?: -1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextMessage = view.findViewById(R.id.editTextMessage)
        btnSendMessage = view.findViewById(R.id.btnSendMessage)
        recyclerView = view.findViewById(R.id.recyclerViewMessages)

        chatAdapter = ChatAdapter(messages, userId)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = chatAdapter

        btnSendMessage.setOnClickListener {
            val content = editTextMessage.text.toString()
            if (content.isNotEmpty()) {
                sendMessage(content)
                editTextMessage.text.clear()
            }
        }

        // Cargar usuarios antes de conectar al servidor
        loadUsers()
        connectToServer()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Obtener todos los usuarios
                val users = ApiRepository.getAllUsers()
                users?.forEach {
                    // Poner en un mapa con ID como clave y nombre como valor
                    userCache[it.id] = it.name
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al cargar usuarios: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun connectToServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                socket = Socket(serverIP, serverPort)
                writer = BufferedWriter(OutputStreamWriter(socket!!.getOutputStream()))
                reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))

                // Enviar datos de conexión
                val initData = MessageData(userId, "")
                writer?.write(gson.toJson(initData) + "\n")
                writer?.flush()

                while (true) {
                    val line = reader?.readLine() ?: break
                    val jsonObject = gson.fromJson(line, Map::class.java)

                    if (jsonObject["type"] == "message") {
                        val encryptedContent = jsonObject["content"] as String
                        val decryptedContent = try {
                            AESUtil.decrypt(encryptedContent)
                        } catch (e: Exception) {
                            "[Mensaje ilegible]"
                        }

                        val message = Message(
                            id = (jsonObject["message_id"] as Double).toInt(),
                            from = (jsonObject["from"] as Double).toInt(),
                            content = decryptedContent,
                            timestamp = jsonObject["timestamp"]?.toString() ?: ""
                        )

                        val userName = userCache[message.from] ?: "Usuario desconocido"
                        message.senderName = userName

                        withContext(Dispatchers.Main) {
                            chatAdapter.addMessage(message)
                            recyclerView.smoothScrollToPosition(chatAdapter.itemCount - 1)
                        }
                    }

                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun sendMessage(content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val encryptedContent = AESUtil.encrypt(content)
                val messageData = MessageData(userId, encryptedContent)
                writer?.write(gson.toJson(messageData) + "\n")
                writer?.flush()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al enviar mensaje", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        try {
            writer?.close()
            reader?.close()
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
