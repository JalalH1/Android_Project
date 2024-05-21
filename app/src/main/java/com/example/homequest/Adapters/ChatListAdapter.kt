package com.example.homequest.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.homequest.Models.Chat
import com.example.homequest.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatListAdapter(private var chats: List<Chat>, private val onChatClick: (Chat) -> Unit) :
    RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view, onChatClick)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    fun updateData(newChats: List<Chat>) {
        chats = newChats
        notifyDataSetChanged()
    }

    class ChatViewHolder(itemView: View, private val onChatClick: (Chat) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.profile_image)
        private val chatNameTextView: TextView = itemView.findViewById(R.id.chat_name)
        private val chatLastMessageTextView: TextView = itemView.findViewById(R.id.chat_last_message)
        private val chatTimeTextView: TextView = itemView.findViewById(R.id.chat_time)

        fun bind(chat: Chat) {
            chatNameTextView.text = chat.listingName // Display listing name
            chatLastMessageTextView.text = chat.lastMessage
            chatTimeTextView.text = chat.timestamp?.let { formatDate(it) } ?: ""

            itemView.setOnClickListener { onChatClick(chat) }
        }

        private fun formatDate(date: Date): String {
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())
            return format.format(date)
        }
    }
}
