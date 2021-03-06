package com.example.tanimals

import android.annotation.SuppressLint
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
import java.util.*
import kotlin.collections.ArrayList


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
    private var userIdList: ArrayList<String> = ArrayList()
    private var userMatchList: ArrayList<String> = ArrayList()
    private val user = FirebaseAuth.getInstance().currentUser
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private var animalImageref = storageRef.child("users/" + user!!.uid + "/profilePicture.png")
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
            dislikeAndMatches()
            nextProfile()
        }

        likeB.setOnClickListener {
            likeAndMatches()
            nextProfile()
        }

    }

    private fun picDownload() {
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

    @SuppressLint("SetTextI18n")
    private fun setFirst() {
        try {
            if (userIdList.isNotEmpty()) {
                if (user?.uid != userIdList[userIdCounter]) {
                    db.collection("user").document(userIdList[userIdCounter])
                        .get()
                        .addOnSuccessListener { document ->
                            val dob = document.data?.get("dob").toString()


                            animalImageref =
                                storageRef.child("users/" + userIdList[userIdCounter] + "/profilePicture.png")
                            picDownload();
                            animalName.text = "Name: " + document.data?.get("name").toString()
                            animalGender.text = "Gender: " + document.data?.get("gender").toString()
                            animalLocation.text = "Location: " + document.data?.get("place").toString()
                            animalRace.text = "Breed: " + document.data?.get("race").toString()
                            animalAge.text = "Age: " + getAge(dob.substring(4).toInt(),dob.substring(2,3).toInt(),dob.substring(0,1).toInt())
                        }
                } else {
                    counterLimiter()
                }
            }
        } catch (e: NullPointerException) {
            Log.d(null, "array didn't store")
        }
    }

    private fun getAge(year: Int, month: Int, day: Int): String? {
        val dob: Calendar = Calendar.getInstance()
        val today: Calendar = Calendar.getInstance()
        dob.set(year, month, day)
        var age: Int = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        val ageInt = age
        return ageInt.toString()
    }

    private fun putUseridInArray() {
        try {
            db.collection("user")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (!userIdList.contains(document.id) && document.id != user?.uid)
                            userIdList.add(document.id)
                    }
                    visibleProfiles()
                    setFirst()

                }

        } catch (e: NullPointerException) {
            Log.d(null, "array didn't store")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun nextProfile() {
        counterLimiter()
        try {
            if (user?.uid != userIdList[userIdCounter]) {
                db.collection("user").document(userIdList[userIdCounter])
                    .get()
                    .addOnSuccessListener { document ->
                        val dob = document.data?.get("dob").toString()

                        animalImageref =
                            storageRef.child("users/" + userIdList[userIdCounter] + "/profilePicture.png")
                        picDownload();
                        animalName.text = document.data?.get("name").toString()
                        animalGender.text = document.data?.get("gender").toString()
                        animalLocation.text = document.data?.get("place").toString()
                        animalRace.text = document.data?.get("race").toString()
                        animalAge.text = "Age: " + getAge(dob.substring(4).toInt(),dob.substring(2,3).toInt(),dob.substring(0,1).toInt())
                    }

            }
        } catch (e: NullPointerException) {
            Log.d(null, "array didn't store")
        }

    }


    private fun likeAndMatches() {
        try {
            val data = hashMapOf(userIdList[userIdCounter] to true)
            user?.uid?.let {
                db.collection("match").document(it)
                    .set(data, SetOptions.merge())
            }
        } catch (e: java.lang.NullPointerException) {

        }
    }

    private fun dislikeAndMatches() {
        try {
            val data = hashMapOf(userIdList[userIdCounter] to false)
            user?.uid?.let {
                db.collection("match").document(it)
                    .set(data, SetOptions.merge())
            }
        } catch (e: java.lang.NullPointerException) {

        }
    }

    // TODO: doesnt EXIT Swipe Activity when userIdList is empty
    private fun getMatchProfilesInArray() {
        var matchRef = db.collection("match");
            matchRef.document(user!!.uid).get()
                .addOnSuccessListener { document ->
                    document.data?.keys?.forEach { key ->
                        userMatchList.add(key);
                    }
//                    Log.d("", userMatchList.toString())
                }.addOnFailureListener { exception ->
                    Log.d(null, "get failed with ", exception)
                }

    }

    private fun visibleProfiles(){
        getMatchProfilesInArray()
        userMatchList.forEach { id ->
            if(userIdList.contains(id)){
                userIdList.remove(id);
            }
//            Log.d("willu", userIdList.toString())
        }

    }

    private fun counterLimiter() {
        when {
            userIdCounter < userIdList.size - 1 -> userIdCounter += 1
            userIdList.size <= 0 -> startActivity(Intent(this, DashboardActivity::class.java))
            else -> startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

}
