package com.example.homequest.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homequest.Adapters.ChatMessageAdapter
import com.example.homequest.Models.ChatMessage
import com.example.homequest.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class ChatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatMessageAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var chatId: String
    private lateinit var sellerId: String
    private lateinit var listingName: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    companion object {
        private const val TAG = "ChatFragment"
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

        chatId = arguments?.getString("chatId") ?: run {
            Log.e(TAG, "Chat ID is missing")
            return
        }
        sellerId = arguments?.getString("sellerId") ?: run {
            Log.e(TAG, "Seller ID is missing")
            return
        }
        listingName = arguments?.getString("listingName") ?: ""

        loadMessages()

        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun loadMessages() {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots == null) {
                    Log.d(TAG, "No chat messages found")
                    return@addSnapshotListener
                }

                val messages = snapshots.map { document ->
                    ChatMessage(
                        senderId = document.getString("senderId") ?: "",
                        message = document.getString("message") ?: "",
                        timestamp = document.getTimestamp("timestamp")?.toDate() ?: Date()
                    )
                }

                chatAdapter.updateData(messages)
                recyclerView.scrollToPosition(messages.size - 1)
            }
    }

    private fun sendMessage() {
        val userId = auth.currentUser?.uid ?: run {
            Log.e(TAG, "User not logged in")
            return
        }
        val message = messageInput.text.toString()
        if (message.isNotEmpty()) {
            val chatMessage = hashMapOf(
                "senderId" to userId,
                "message" to message,
                "timestamp" to FieldValue.serverTimestamp()
            )
            val chatRef = db.collection("chats").document(chatId)
            chatRef.collection("messages")
                .add(chatMessage)
                .addOnSuccessListener {
                    messageInput.text.clear()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

            // Update chat participants and details if it's the first message
            chatRef.get().addOnSuccessListener { document ->
                if (!document.exists()) {
                    val participants = listOf(userId, sellerId)
                    chatRef.set(mapOf(
                        "participants" to participants,
                        "lastMessage" to message,
                        "timestamp" to FieldValue.serverTimestamp(),
                        "listingName" to listingName
                    ))
                } else {
                    chatRef.update("lastMessage", message, "timestamp", FieldValue.serverTimestamp())
                }
            }
        }
    }

}
