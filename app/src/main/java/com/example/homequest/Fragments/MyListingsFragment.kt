package com.example.homequest.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homequest.Adapters.MyListingsAdapter
import com.example.homequest.Models.Listing
import com.example.homequest.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyListingsFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var myListingsAdapter: MyListingsAdapter
    private val myListings = mutableListOf<Listing>()
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_listings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        myListingsAdapter = MyListingsAdapter(myListings, firestore)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_my_listings)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = myListingsAdapter

        val buttonAddListing: Button = view.findViewById(R.id.button_add_listing)
        buttonAddListing.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, AddListingFragment())
                .addToBackStack(null)
                .commit()
        }

        loadMyListings()
    }

    private fun loadMyListings() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("listings")
            .whereEqualTo("sellerId", userId)
            .get()
            .addOnSuccessListener { documents ->
                myListings.clear()
                for (document in documents) {
                    val listing = document.toObject(Listing::class.java)
                    myListings.add(listing)
                    Log.d(TAG, "Listing retrieved: ${listing.name}")
                }
                myListingsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }


    companion object {
        private const val TAG = "MyListingsFragment"
    }
}
