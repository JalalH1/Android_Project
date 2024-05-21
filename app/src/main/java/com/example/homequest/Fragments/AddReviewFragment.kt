package com.example.homequest.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import com.example.homequest.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AddReviewFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var listingId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        listingId = arguments?.getString("listingId") ?: return

        val ratingBar: RatingBar = view.findViewById(R.id.rating_bar)
        val reviewText: EditText = view.findViewById(R.id.review_text)
        val buttonSubmitReview: Button = view.findViewById(R.id.button_submit_review)

        buttonSubmitReview.setOnClickListener {
            val rating = ratingBar.rating
            val review = reviewText.text.toString()
            submitReview(rating, review)
        }
    }

    private fun submitReview(rating: Float, review: String) {
        val reviewData = hashMapOf(
            "rating" to rating,
            "review" to review,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("listings").document(listingId).collection("reviews")
            .add(reviewData)
            .addOnSuccessListener {
                Toast.makeText(context, "Review added", Toast.LENGTH_SHORT).show()
                activity?.onBackPressed()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error adding review", Toast.LENGTH_SHORT).show()
            }
    }
}
