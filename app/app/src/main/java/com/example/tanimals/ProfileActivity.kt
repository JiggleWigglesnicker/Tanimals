package com.example.tanimals

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ClassName"
    }
    val user = FirebaseAuth.getInstance().currentUser
    lateinit var imageView: ImageView
    lateinit var spinner: Spinner
    var breedArray = arrayOf<String>()
    private val db = FirebaseFirestore.getInstance()
    lateinit var nameField: EditText
    lateinit var dateField: EditText
    lateinit var placeField: EditText
    lateinit var genderGroup: RadioGroup
    lateinit var storage: FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        storage = FirebaseStorage.getInstance()

        imageView = findViewById(R.id.avatar)
        imageView.setImageResource(R.drawable.avatar)
        spinner = findViewById(R.id.positionSpinner)
        nameField = findViewById(R.id.name)
        dateField = findViewById(R.id.dateOfBirth)
        placeField = findViewById(R.id.address)
        genderGroup = findViewById(R.id.radioGroup)

        val docRef = db.collection("user").document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    nameField.setText(document.data?.get("name").toString())
                    dateField.setText(document.data?.get("dob").toString())
                    placeField.setText(document.data?.get("place").toString())
                    genderGroup.check(document.data?.get("gender").toString().toInt())

                }
            }
    }

    fun selectImage(v: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
    }

    fun saveProfile(v: View) {
        val checkedRadio = genderGroup.checkedRadioButtonId

        val profile = hashMapOf(
            "name" to nameField.text.toString(),
            "dob" to dateField.text.toString(),
            "race" to spinner.selectedItem.toString(),
            "place" to placeField.text.toString(),
            "gender" to checkedRadio
        )

        if (user != null) {
            db.collection("user").document(user.uid)
                .set(profile)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(data!!.data)
            labelBitmapImage()
        }
    }

    private fun labelBitmapImage() {
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val image = FirebaseVisionImage.fromBitmap(bitmap)

        val labeler = FirebaseVision.getInstance().cloudImageLabeler

        labeler.processImage(image)
            .addOnSuccessListener { labels ->
                breedArray = arrayOf()
                for (label in labels) {
                    val text = label.text
                    val entityId = label.entityId
                    val confidence = label.confidence
                    breedArray += text
                }
                val aa = ArrayAdapter(this,android.R.layout.simple_spinner_item,breedArray)
                spinner.adapter = aa

                var storageRef = storage.reference
                val animalImageref = storageRef.child("avatars/"+ user!!.uid +".png")

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val data = baos.toByteArray()

                var uploadTask = animalImageref.putBytes(data)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener {
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                }

            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }

    }

}