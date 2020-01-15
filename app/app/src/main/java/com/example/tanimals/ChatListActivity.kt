package com.example.tanimals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.collections.ArrayList

class ChatListActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ChatListActivity"
    }

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()
    private var matchArray: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        //Retrieve user matches
        val docRef = db.collection("match").document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                //continue if there are matches
                if (document != null) {
                    //iterate over matches where k is match userID, v is liked boolean(True is liked, False is disliked)
                    document.data!!.forEach { (k, v) ->
                        //continue if user liked
                            getMatches(v,k)
                    }
                    println(matchArray)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


    private fun getMatches(v:Any, k:String){
        // Gets the matches of the person,to compare if the both have matched
        val docRefMatch = db.collection("match").document(k)
        docRefMatch.get()
            .addOnSuccessListener { document ->
                //continue if there are matches
                if (document != null) {
                    //checks if user is in the list
                    if (document.data?.get(user!!.uid) != null) {
                        //checks if user is liked
                        if (document.data?.get(user!!.uid) == true) {
                            //if both have matched add user to the "matchArray" array
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
