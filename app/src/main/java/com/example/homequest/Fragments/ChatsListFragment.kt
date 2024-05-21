package com.example.homequest.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homequest.Adapters.ChatListAdapter
import com.example.homequest.Models.Chat
import com.example.homequest.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date

class ChatListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatAdapter: ChatListAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chats_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.chat_list_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        chatAdapter = ChatListAdapter(emptyList()) { chat ->
            navigateToChat(chat)
        }
        recyclerView.adapter = chatAdapter

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        loadChats()
    }

    private fun loadChats() {
        val userId = auth.currentUser?.uid ?: return

        Log.d(TAG, "Attempting to load chats for user ID: $userId")

        db.collection("chats")
            .whereArrayContains("participants", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots == null || snapshots.isEmpty) {
                    Log.d(TAG, "No chat documents found")
                    return@addSnapshotListener
                }

                val chats = snapshots.map { document ->
                    Log.d(TAG, "Chat document found: ${document.id}")
                    Chat(
                        chatId = document.id,
                        participants = document.get("participants") as List<String>,
                        lastMessage = document.getString("lastMessage") ?: "",
                        timestamp = document.getTimestamp("timestamp")?.toDate() ?: Date(),
                        listingId = document.getString("listingId") ?: "",
                        listingName = document.getString("listingName") ?: ""
                    )
                }

                Log.d(TAG, "Loaded ${chats.size} chats")
                chatAdapter.updateData(chats)
            }
    }


    private fun navigateToChat(chat: Chat) {
        val bundle = Bundle().apply {
            putString("chatId", chat.chatId)
            putString("sellerId", chat.participants.firstOrNull { it != auth.currentUser?.uid })
            putString("listingName", chat.listingName)
        }
        val fragment = ChatFragment().apply {
            arguments = bundle
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val TAG = "ChatListFragment"
    }
}
