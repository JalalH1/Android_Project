package com.example.homequest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.example.homequest.Fragments.ChatListFragment
import com.example.homequest.Fragments.ListingsFragment
import com.example.homequest.Fragments.MyListingsFragment
import com.example.homequest.Fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var auth: FirebaseAuth

    private val chatsListFragment = ChatListFragment()
    private val profileFragment = ProfileFragment()
    private val listingsFragment = ListingsFragment()
    private val myListingsFragment = MyListingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        authenticateUser()

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_chat -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, chatsListFragment).commit()
                    true
                }
                R.id.menu_profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit()
                    true
                }
                R.id.menu_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, listingsFragment).commit()
                    true
                }
                R.id.menu_my_listings -> {
                    supportFragmentManager.beginTransaction().replace(R.id.main_frame_layout, myListingsFragment).commit()
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.selectedItemId = R.id.menu_chat
    }

    private fun authenticateUser() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInAnonymously:success")
                    } else {
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Log.d(TAG, "User already signed in: ${currentUser.uid}")
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
