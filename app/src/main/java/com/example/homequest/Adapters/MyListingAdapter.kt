package com.example.homequest.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.homequest.Models.Listing
import com.example.homequest.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class MyListingsAdapter(private val listings: List<Listing>, private val firestore: FirebaseFirestore) :
    RecyclerView.Adapter<MyListingsAdapter.MyListingViewHolder>() {

    class MyListingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.listing_name)
        val address: TextView = view.findViewById(R.id.listing_address)
        val price: TextView = view.findViewById(R.id.listing_price)
        val image: ImageView = view.findViewById(R.id.listing_image)
        val buttonDelete: ImageButton = view.findViewById(R.id.button_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_listing_with_delete, parent, false)
        return MyListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyListingViewHolder, position: Int) {
        val listing = listings[position]
        holder.name.text = listing.name
        holder.address.text = listing.address
        holder.price.text = listing.price

        // Load the first image URL if available, otherwise load a placeholder
        if (listing.imageUrls.isNotEmpty()) {
            Picasso.get().load(listing.imageUrls[0]).into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.image_icon) // Make sure you have a placeholder image
        }

        holder.buttonDelete.setOnClickListener {
            firestore.collection("listings").document(listing.id)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "Listing deleted", Toast.LENGTH_SHORT).show()
                    notifyItemRemoved(position)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(holder.itemView.context, "Error deleting listing", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun getItemCount() = listings.size
}
