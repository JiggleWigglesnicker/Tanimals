package com.example.tanimals

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException

class ChatListActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ClassName"
    }

    private lateinit var functions: FirebaseFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)
        functions = FirebaseFunctions.getInstance()

        addMessage("test")
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val e = task.exception
                    if (e is FirebaseFunctionsException) {
                        val code = e.code
                        val details = e.details
                    }

                    // [START_EXCLUDE]
                    Log.w(TAG, "addMessage:onFailure", e)
                    return@OnCompleteListener
                    // [END_EXCLUDE]
                }

                // [START_EXCLUDE]
                val result = task.result
                println("banana" + result)
                // [END_EXCLUDE]
            })
    }

    private fun addMessage(text: String): Task<String> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "text" to text,
            "push" to true
        )

        return functions
            .getHttpsCallable("helloWorld")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as String
                result
            }
    }
}
