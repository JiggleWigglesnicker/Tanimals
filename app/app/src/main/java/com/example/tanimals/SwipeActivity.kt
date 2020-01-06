package com.example.tanimals

import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SwipeActivity : AppCompatActivity() {
    lateinit var profilePic : ImageView;
    lateinit var animalName: TextView;
    lateinit var animalGender: TextView;
    lateinit var animalLocation: TextView;
    lateinit var animalRace : TextView;
    lateinit var animalAge : TextView;
    lateinit var likeB : Button;
    lateinit var dislikeB : Button;
    private val db = FirebaseFirestore.getInstance();
    val user = FirebaseAuth.getInstance().currentUser;
    val documentMap : HashMap<String,String> =  HashMap<String,String>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)

        profilePic = findViewById(R.id.avatar_Animal);
        animalName = findViewById(R.id.name_Animal);
        animalGender = findViewById(R.id.gender_Animal);
        animalLocation = findViewById(R.id.location_Animal);
        animalRace = findViewById(R.id.race_Animal);
        animalAge = findViewById(R.id.age_Animal);
        likeB = findViewById(R.id.button_Like);
        dislikeB = findViewById(R.id.button_Dislike);

        val docRef = db.collection("user").document(user!!.uid)
        db.collection("user")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(null, "${document.id} => ${document.data}")
                    documentMap["${document.id}"] = "${document.data}";
                }
            }

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    animalName.setText(document.data?.get("name").toString())
                    animalAge.setText(document.data?.get("dob").toString())
                    animalLocation.setText(document.data?.get("place").toString())
                    animalGender.setText(document.data?.get("gender").toString())
                    animalRace.setText(document.data?.get("race").toString())
                }
            }

        dislikeB.setOnClickListener{
            // skip to next profile in firebase

        }

        likeB.setOnClickListener{
            // store like and match if both user like eachother
        }
    }
}
