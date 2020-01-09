package com.example.tanimals

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage


class SwipeActivity : AppCompatActivity() {
    private lateinit var profilePic: ImageView
    private lateinit var animalName: TextView
    private lateinit var animalGender: TextView
    private lateinit var animalLocation: TextView
    private lateinit var animalRace: TextView
    private lateinit var animalAge: TextView
    private lateinit var likeB: Button
    private lateinit var dislikeB: Button
    private val db = FirebaseFirestore.getInstance()
    private val userIdList: ArrayList<String> = ArrayList()
    private val user = FirebaseAuth.getInstance().currentUser
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private var animalImageref = storageRef.child("users/"+ user!!.uid +"/profilePicture.png")
    private var userIdCounter: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)

        profilePic = findViewById(R.id.avatar_Animal)
        animalName = findViewById(R.id.name_Animal)
        animalGender = findViewById(R.id.gender_Animal)
        animalLocation = findViewById(R.id.location_Animal)
        animalRace = findViewById(R.id.race_Animal)
        animalAge = findViewById(R.id.age_Animal)
        likeB = findViewById(R.id.button_Like)
        dislikeB = findViewById(R.id.button_Dislike)
        putUseridInArray()

        dislikeB.setOnClickListener {
            // TODO: should stop disliked profile from returning
            nextProfile()
        }

        likeB.setOnClickListener {
            // TODO: store like and match if both user like each other
            likeAndMatches()
            nextProfile()
        }

    }

    fun picDownload() {
        val TWO_MEGABYTE = 2048 * 2048.toLong()
        animalImageref.getBytes(TWO_MEGABYTE)
            .addOnSuccessListener { bytes ->
                val bitmap =
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    profilePic.setImageBitmap(bitmap)
            }.addOnFailureListener {
                // Handle any errors
            }
    }
    private fun setFirst(){
        try {
            if(userIdList.isNotEmpty()){
                if (user?.uid != userIdList[userIdCounter]) {
                    db.collection("user").document(userIdList[userIdCounter])
                        .get()
                        .addOnSuccessListener { document ->
                            picDownload();
                            animalName.text = document.data?.get("name").toString()
                            animalGender.text = document.data?.get("gender").toString()
                            animalLocation.text = document.data?.get("location").toString()
                            animalRace.text = document.data?.get("race").toString()
                            animalAge.text = document.data?.get("dob").toString()
                        }
                } else {
                    counterLimiter()
                }
            }
        } catch (e: NullPointerException) {
            Log.d(null, "array didn't store")
        }
    }

    private fun putUseridInArray() {
        try {
            db.collection("user")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (!userIdList.contains(document.id) && document.id != user?.uid )
                            userIdList.add(document.id)
                    }
                    setFirst()
                }

        } catch (e: NullPointerException) {
            Log.d(null, "array didn't store")
        }
    }

    private fun nextProfile() {
        counterLimiter()
        try {
            Log.d(null, userIdList.size.toString())
            Log.d(null, userIdCounter.toString())
            Log.d(null, userIdList.toString())
            if (user?.uid != userIdList[userIdCounter]) {
                db.collection("user").document(userIdList[userIdCounter])
                    .get()
                    .addOnSuccessListener { document ->
                        picDownload();
                        animalName.text = document.data?.get("name").toString()
                        animalGender.text = document.data?.get("gender").toString()
                        animalLocation.text = document.data?.get("location").toString()
                        animalRace.text = document.data?.get("race").toString()
                        animalAge.text = document.data?.get("dob").toString()
                    }

            }
        } catch (e: NullPointerException) {
            Log.d(null, "array didn't store")
        }

    }

    // TODO: likes not appearing in documents
    fun likeAndMatches() {
        try {
            val data = hashMapOf(userIdList[userIdCounter] to true)
            user?.uid?.let {
                db.collection("match").document(it)
                    .set(data, SetOptions.merge())
            }
        } catch (e: java.lang.NullPointerException) {

        }
    }

    private fun counterLimiter() {
        if (userIdCounter < userIdList.size - 1) {
            userIdCounter += 1
        } else {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

}
