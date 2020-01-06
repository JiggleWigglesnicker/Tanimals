package com.example.tanimals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_swipe.view.*
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream


class SwipeActivity : AppCompatActivity() {
    lateinit var profilePic : ImageView;
    lateinit var animalName: TextView;
    lateinit var animalGender: TextView;
    lateinit var animalLocation: TextView;
    lateinit var animalRace : TextView;
    lateinit var animalAge : TextView;
    private val db = FirebaseFirestore.getInstance();
    val user = FirebaseAuth.getInstance().currentUser;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe)

        profilePic = findViewById(R.id.avatar_Animal);
        animalName = findViewById(R.id.name_Animal);
        animalGender = findViewById(R.id.gender_Animal);
        animalLocation = findViewById(R.id.location_Animal);
        animalRace = findViewById(R.id.race_Animal);
        animalAge = findViewById(R.id.age_Animal);


        val docRef = db.collection("user").document(user!!.uid)
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
    }
}
