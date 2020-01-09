package com.example.tanimals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList

class ChatListActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ChatListActivity"
    }

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private val matchArray: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)


        val docRef = db.collection("match").document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    document.data!!.forEach { (k, v) ->
                        if (v == true) {
                            // Get matches for matched
                            val docRef = db.collection("match").document(k)
                            docRef.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        if (document.data?.get(user.uid) != null) {
                                            if (document.data?.get(user.uid) == true) {
                                                matchArray.add(k)
                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "No such document")
                                    }

                                }
                                .addOnFailureListener { exception ->
                                    Log.d(TAG, "get failed with ", exception)
                                }
                        }
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}
