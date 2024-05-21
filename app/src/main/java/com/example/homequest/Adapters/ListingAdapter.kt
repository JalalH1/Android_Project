package com.example.homequest.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.homequest.Models.Listing
import com.example.homequest.R
import com.squareup.picasso.Picasso

class ListingsAdapter(
    private val listings: List<Listing>,
    private val onItemClick: (Listing) -> Unit
) : RecyclerView.Adapter<ListingsAdapter.ListingViewHolder>() {

    class ListingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.listing_name)
        val address: TextView = view.findViewById(R.id.listing_address)
        val price: TextView = view.findViewById(R.id.listing_price)
        val image: ImageView = view.findViewById(R.id.listing_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_listing, parent, false)
        return ListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val listing = listings[position]
        holder.name.text = listing.name
        holder.address.text = listing.address
        holder.price.text = listing.price

        if (listing.imageUrls.isNotEmpty()) {
            Picasso.get().load(listing.imageUrls[0]).into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.image_icon)
        }

        holder.itemView.setOnClickListener {
            onItemClick(listing)
        }
    }

    override fun getItemCount() = listings.size
}
