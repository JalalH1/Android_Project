package com.example.homequest.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homequest.Adapters.ChatMessageAdapter
import com.example.homequest.Models.ChatMessage
import com.example.homequest.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class ChatDetailFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatMessageAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "ChatDetailFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.chat_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        chatAdapter = ChatMessageAdapter(emptyList())
        recyclerView.adapter = chatAdapter

        messageInput = view.findViewById(R.id.message_input)
        sendButton = view.findViewById(R.id.send_button)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val chatId = arguments?.getString("chatId") ?: return
        loadMessages(chatId)

        sendButton.setOnClickListener {
            sendMessage(chatId)
        }
    }

    private fun loadMessages(chatId: String) {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val messages = snapshots?.map { document ->
                    ChatMessage(
                        senderId = document.getString("senderId") ?: "",
                        message = document.getString("message") ?: "",
                        timestamp = document.getTimestamp("timestamp")?.toDate() ?: Date()
                    )
                } ?: emptyList()

                chatAdapter.updateData(messages)
                recyclerView.scrollToPosition(messages.size - 1)
            }
    }

    private fun sendMessage(chatId: String) {
        val message = messageInput.text.toString()
        if (message.isNotEmpty()) {
            val chatMessage = hashMapOf(
                "senderId" to auth.currentUser?.uid,
                "message" to message,
                "timestamp" to FieldValue.serverTimestamp()
            )
            db.collection("chats").document(chatId).collection("messages")
                .add(chatMessage)
                .addOnSuccessListener {
                    messageInput.text.clear()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }
}
