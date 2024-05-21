package com.example.homequest.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.homequest.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        profileImageView = view.findViewById(R.id.profileImageView)
        nameTextView = view.findViewById(R.id.nameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        logoutButton = view.findViewById(R.id.logoutButton)

        loadUserProfile()

        logoutButton.setOnClickListener {
            auth.signOut()
            activity?.finish()
        }

        return view
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            emailTextView.text = user.email
            Log.d(TAG, "User ID: ${user.uid}")

            val userRef = firestore.collection("users").document(user.uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        val firstName = document.getString("firstName")
                        val lastName = document.getString("lastName")
                        val profilePictureUrl = document.getString("profileImageUrl")

                        nameTextView.text = "$firstName $lastName"

                        if (!profilePictureUrl.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(profilePictureUrl)
                                .transform(CircleCrop())
                                .into(profileImageView)
                        }
                    } else {
                        Log.d(TAG, "No such document")
                        nameTextView.text = "Name not found"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                    nameTextView.text = "Error fetching profile"
                }
        } else {
            nameTextView.text = "User not signed in"
        }
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}
