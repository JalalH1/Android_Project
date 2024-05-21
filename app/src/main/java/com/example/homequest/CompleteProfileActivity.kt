package com.example.homequest

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CompleteProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private lateinit var ivProfileImage: ImageView
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var spCountry: Spinner
    private lateinit var btnCompleteProfile: Button

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        ivProfileImage = findViewById(R.id.ivProfileImage)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        spCountry = findViewById(R.id.spCountry)
        btnCompleteProfile = findViewById(R.id.btnCompleteProfile)

        // Set up the country spinner with an array adapter
        val countries = resources.getStringArray(R.array.country_array)
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, countries)
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        spCountry.adapter = adapter

        ivProfileImage.setOnClickListener {
            openImagePicker()
        }

        btnCompleteProfile.setOnClickListener {
            completeProfile()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imageUri?.let { uri ->
                Glide.with(this)
                    .load(uri)
                    .circleCrop()
                    .into(ivProfileImage)
            }
        }
    }

    private fun completeProfile() {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val country = spCountry.selectedItem.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid ?: return
        val profileRef = storage.reference.child("profile_images").child("$userId.jpg")

        profileRef.putFile(imageUri!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    profileRef.downloadUrl.addOnSuccessListener { uri ->
                        val profileData = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "country" to country,
                            "profileImageUrl" to uri.toString()
                        )

                        firestore.collection("users").document(userId)
                            .set(profileData)
                            .addOnCompleteListener(OnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Profile completed successfully", Toast.LENGTH_SHORT).show()
                                    navigateToHome()
                                } else {
                                    Toast.makeText(this, "Profile completion failed", Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                } else {
                    Toast.makeText(this, "Profile image upload failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val REQUEST_CODE_IMAGE_PICKER = 1001
    }
}
