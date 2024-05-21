package com.example.homequest.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homequest.Adapters.ListingsAdapter
import com.example.homequest.Models.Listing
import com.example.homequest.R
import com.google.firebase.firestore.FirebaseFirestore

class ListingsFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var listingsAdapter: ListingsAdapter
    private val listings = mutableListOf<Listing>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_listings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        listingsAdapter = ListingsAdapter(listings) { listing ->
            navigateToListingDetails(listing)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_listings)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = listingsAdapter

        loadListings()
    }

    private fun loadListings() {
        firestore.collection("listings")
            .get()
            .addOnSuccessListener { documents ->
                listings.clear()
                for (document in documents) {
                    val listing = document.toObject(Listing::class.java)
                    listings.add(listing)
                }
                listingsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun navigateToListingDetails(listing: Listing) {
        val bundle = Bundle().apply {
            putParcelable("listing", listing)
        }
        val fragment = ListingDetailsFragment().apply {
            arguments = bundle
        }
        fragmentManager?.beginTransaction()
            ?.replace(R.id.main_frame_layout, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    companion object {
        private const val TAG = "ListingsFragment"
    }
}

