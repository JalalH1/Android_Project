package com.example.homequest

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import android.widget.Toast

class ApartmentDetailsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apartment_details)

        val imageView = findViewById<ImageView>(R.id.apartment_detail_image)
        val nameTextView = findViewById<TextView>(R.id.apartment_detail_name)
        val addressTextView = findViewById<TextView>(R.id.apartment_detail_address)
        val priceTextView = findViewById<TextView>(R.id.apartment_detail_price)
        val descriptionTextView = findViewById<TextView>(R.id.apartment_detail_description)

        db = FirebaseFirestore.getInstance()

        val apartmentId = intent.getStringExtra("apartmentId")
        Log.d("ApartmentDetailsActivity", "Received apartmentId: $apartmentId")

        if (apartmentId != null && apartmentId.isNotEmpty()) {
            db.collection("apartments").document(apartmentId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val apartment = document.toObject(Apartment::class.java)
                        if (apartment != null) {
                            Picasso.get().load(apartment.imageURL).into(imageView)
                            nameTextView.text = apartment.name
                            addressTextView.text = apartment.address
                            priceTextView.text = apartment.price
                            descriptionTextView.text = apartment.description
                        }
                    } else {
                        Toast.makeText(this, "No such document!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error getting document: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Apartment ID is null or empty", Toast.LENGTH_SHORT).show()
        }
    }
}
