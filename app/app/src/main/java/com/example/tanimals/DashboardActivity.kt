package com.example.tanimals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {
        val TAG = "MyMessage"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        setContentView(R.layout.activity_dashboard)

        val docRef = db.collection("user").document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if(document.data == null) {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
            }

        logoutBut.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        profilebut.setOnClickListener{
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}
