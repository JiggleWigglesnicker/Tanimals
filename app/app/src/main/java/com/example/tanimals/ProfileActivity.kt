package com.example.tanimals

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt


class ProfileActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ClassName"
    }
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var imageView: ImageView
    private lateinit var spinner: Spinner
    private var breedArray = arrayOf<String>()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var nameField: EditText
    private lateinit var dateField: EditText
    private lateinit var placeField: EditText
    private lateinit var genderGroup: RadioGroup

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val animalImageref = storageRef.child("users/"+ user!!.uid +"/profilePicture.png")


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imageView = findViewById(R.id.avatar)
        spinner = findViewById(R.id.positionSpinner)
        nameField = findViewById(R.id.name)
        dateField = findViewById(R.id.dateOfBirth)
        placeField = findViewById(R.id.address)
        genderGroup = findViewById(R.id.radioGroup)
        val gender1 : RadioButton = findViewById(R.id.gender1)
        val gender2 : RadioButton = findViewById(R.id.gender2)
        val gender3 : RadioButton = findViewById(R.id.gender3)


        val docRef = db.collection("user").document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    val TWO_MEGABYTE = 2048 * 2048.toLong()
                    animalImageref.getBytes(TWO_MEGABYTE)
                        .addOnSuccessListener { bytes ->
                            val bitmap =
                                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            imageView.setImageBitmap(bitmap)
                            labelBitmapImage(false)
                        }.addOnFailureListener {
                            // Handle any errors
                        }

                    nameField.setText(document.data?.get("name").toString())
                    dateField.setText(document.data?.get("dob").toString().replace(Regex("(..)(..)(....)"), "\$1-\$2-\$3"))
                    placeField.setText(document.data?.get("place").toString())

                    when {
                        gender1.text == document.data?.get("gender").toString() -> {
                            gender1.isChecked = true
                        }
                        gender2.text == document.data?.get("gender").toString() -> {
                            gender2.isChecked = true
                        }
                        else -> {
                            gender3.isChecked = true
                        }
                    }
                }
            }
        dateField.addTextChangedListener(object : TextWatcher {
            var prevL = 0
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                prevL = dateField.text.toString().length
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                val length = editable.length
                if (prevL < length && (length == 2 || length == 5)) {
                    editable.append("-")
                }
            }
        })
    }

    fun selectImage(v: View) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
    }

    fun saveProfile(v: View) {
        val checkedRadio: RadioButton = findViewById(genderGroup.checkedRadioButtonId)


        if(nameField.text.toString() == "" || dateField.text.toString() == "" || spinner.selectedItem == null || placeField.text.toString() == ""){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        val profile = hashMapOf(
            "name" to nameField.text.toString().filter { it.isLetter() || it.isWhitespace() },
            "dob" to dateField.text.toString().filter { it.isDigit() },
            "race" to spinner.selectedItem.toString().filter { it.isLetter() },
            "place" to placeField.text.toString().filter { it.isLetter() },
            "gender" to checkedRadio.text
        )

        if (user != null) {
            db.collection("user").document(user.uid)
                .set(profile)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
        }
    }

    // Extension method to resize bitmap to maximum width and height
    private fun Bitmap.scale(maxWidthAndHeight:Int):Bitmap{
        val newWidth: Int
        val newHeight: Int

        if (this.width >= this.height){
            val ratio:Float = this.width.toFloat() / this.height.toFloat()

            newWidth = maxWidthAndHeight
            // Calculate the new height for the scaled bitmap
            newHeight = (maxWidthAndHeight / ratio).roundToInt()
        }else{
            val ratio:Float = this.height.toFloat() / this.width.toFloat()

            // Calculate the new width for the scaled bitmap
            newWidth = (maxWidthAndHeight / ratio).roundToInt()
            newHeight = maxWidthAndHeight
        }

        return Bitmap.createScaledBitmap(
            this,
            newWidth,
            newHeight,
            false
        )
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(data!!.data)
            labelBitmapImage(true)
        }
    }

    private fun labelBitmapImage(upload: Boolean) {
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val image = FirebaseVisionImage.fromBitmap(bitmap)

        val labeler = FirebaseVision.getInstance().cloudImageLabeler

        labeler.processImage(image)
            .addOnSuccessListener { labels ->
                breedArray = arrayOf()
                for (label in labels) {
                    val text = label.text
//                    val entityId = label.entityId
//                    val confidence = label.confidence
                    breedArray += text
                }
                val aa = ArrayAdapter(this,android.R.layout.simple_spinner_item,breedArray)
                spinner.adapter = aa

                if(upload){
                    uploadImage(bitmap)
                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                // ...
            }
    }

    private fun uploadImage(bitmap: Bitmap){
        var uploadBitmap = bitmap
        val baos = ByteArrayOutputStream()
        uploadBitmap = uploadBitmap.scale(1024)
        uploadBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = animalImageref.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }

}