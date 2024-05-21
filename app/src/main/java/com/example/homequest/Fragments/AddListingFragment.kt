package com.example.homequest.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.homequest.Models.Listing
import com.example.homequest.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class AddListingFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var imageUriList: MutableList<Uri>
    private lateinit var linearLayoutImages: LinearLayout
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        imageUriList = mutableListOf()
        linearLayoutImages = view.findViewById(R.id.linear_layout_images)

        val nameEditText: EditText = view.findViewById(R.id.edit_text_name)
        val addressEditText: EditText = view.findViewById(R.id.edit_text_address)
        val priceEditText: EditText = view.findViewById(R.id.edit_text_price)
        val descriptionEditText: EditText = view.findViewById(R.id.edit_text_description)
        val uploadImageButton: Button = view.findViewById(R.id.button_upload_image)
        val addButton: Button = view.findViewById(R.id.button_add_listing)

        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                result.data?.clipData?.let { clipData ->
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        imageUriList.add(uri)
                        addImageView(uri)
                    }
                } ?: result.data?.data?.let { uri ->
                    imageUriList.add(uri)
                    addImageView(uri)
                }
            }
        }

        uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            imagePickerLauncher.launch(intent)
        }

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val address = addressEditText.text.toString()
            val price = priceEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val sellerId = auth.currentUser?.uid

            if (name.isNotEmpty() && address.isNotEmpty() && price.isNotEmpty() && description.isNotEmpty() && imageUriList.isNotEmpty() && sellerId != null) {
                uploadImagesAndAddListing(name, address, price, description, sellerId)
            } else {
                Toast.makeText(context, "Please fill out all fields and upload at least one image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addImageView(uri: Uri) {
        val imageView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setImageURI(uri)
        }
        linearLayoutImages.addView(imageView)
    }

    private fun uploadImagesAndAddListing(name: String, address: String, price: String, description: String, sellerId: String) {
        val imageUrls = mutableListOf<String>()
        val storageReference = storage.reference

        for (uri in imageUriList) {
            val fileName = UUID.randomUUID().toString()
            val imageRef: StorageReference = storageReference.child("listings/$fileName")

            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        imageUrls.add(downloadUrl.toString())
                        if (imageUrls.size == imageUriList.size) {
                            addListingToFirestore(name, address, price, description, sellerId, imageUrls)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error uploading image: $exception")
                }
        }
    }

    private fun addListingToFirestore(name: String, address: String, price: String, description: String, sellerId: String, imageUrls: List<String>) {
        val listingId = firestore.collection("listings").document().id
        val listing = Listing(
            id = listingId,
            name = name,
            address = address,
            price = price,
            description = description,
            imageUrls = imageUrls,
            sellerId = sellerId
        )

        firestore.collection("listings").document(listing.id)
            .set(listing)
            .addOnSuccessListener {
                Toast.makeText(context, "Listing added", Toast.LENGTH_SHORT).show()
                activity?.onBackPressed() // Go back to the previous fragment
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error adding listing", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        private const val TAG = "AddListingFragment"
    }
}
