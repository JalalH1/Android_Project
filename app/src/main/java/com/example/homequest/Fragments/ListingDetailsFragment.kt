package com.example.homequest.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.homequest.Models.Listing
import com.example.homequest.R
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ListingDetailsFragment : Fragment() {

    private lateinit var listing: Listing
    private lateinit var contactSellerButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_listing_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listing = arguments?.getParcelable("listing")!!

        val listingImage: ImageView = view.findViewById(R.id.listing_image)
        val listingName: TextView = view.findViewById(R.id.listing_name)
        val listingAddress: TextView = view.findViewById(R.id.listing_address)
        val listingPrice: TextView = view.findViewById(R.id.listing_price)
        contactSellerButton = view.findViewById(R.id.contact_seller_button)

        listingName.text = listing.name
        listingAddress.text = listing.address
        listingPrice.text = listing.price

        if (listing.imageUrls.isNotEmpty()) {
            Picasso.get().load(listing.imageUrls[0]).into(listingImage)
        } else {
            listingImage.setImageResource(R.drawable.image_icon)
        }

        contactSellerButton.setOnClickListener {
            navigateToChatWithSeller()
        }
    }

    private fun navigateToChatWithSeller() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val chatId = "chat_${listing.sellerId}_${listing.id}_${currentUser?.uid}"

        val bundle = Bundle().apply {
            putString("chatId", chatId)
            putString("sellerId", listing.sellerId)
            putString("listingName", listing.name)
        }
        val fragment = ChatFragment().apply {
            arguments = bundle
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}
