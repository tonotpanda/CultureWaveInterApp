package com.example.culturewaveinter.Adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.culturewaveinter.Entities.Message
import com.example.culturewaveinter.R

class ChatAdapter(
    private val messages: MutableList<Message>,
    private val currentUserId: Int
                 ) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.messageText)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val messageContainer: LinearLayout = itemView.findViewById(R.id.messageContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        val isSentByMe = message.from == currentUserId

        holder.messageText.text = message.content

        // Alinear burbuja y aplicar estilo
        val params = holder.messageContainer.layoutParams as FrameLayout.LayoutParams
        if (isSentByMe) {
            params.gravity = Gravity.END
            holder.messageContainer.setBackgroundResource(R.drawable.message_bubble_sent)
            holder.userName.visibility = View.GONE
            params.marginEnd = 16
            params.marginStart = 60
        } else {
            params.gravity = Gravity.START
            holder.messageContainer.setBackgroundResource(R.drawable.message_bubble_received)
            holder.userName.visibility = View.VISIBLE
            params.marginEnd = 60
            params.marginStart = 16
        }

        holder.messageContainer.layoutParams = params
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
