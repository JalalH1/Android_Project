package com.example.homequest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

data class Apartment(
    var id: String = "",
    val name: String = "",
    val address: String = "",
    val price: String = "",
    val description: String = "",
    val imageURL: String = "",
    val country: String = "",
    val type: String = ""
)

class ApartmentAdapter(context: Context, resource: Int, objects: List<Apartment>) :
    ArrayAdapter<Apartment>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_apartment, parent, false)

        val apartment = getItem(position)
        val imageView = view.findViewById<ImageView>(R.id.apartment_image)
        val nameTextView = view.findViewById<TextView>(R.id.apartment_name)
        val priceTextView = view.findViewById<TextView>(R.id.apartment_price)

        if (apartment != null) {
            Picasso.get().load(apartment.imageURL).into(imageView)
            nameTextView.text = apartment.name
            priceTextView.text = apartment.price
        }

        return view
    }
}
