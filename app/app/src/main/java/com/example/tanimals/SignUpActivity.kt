package com.example.tanimals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_signup.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()

        signUpButton.setOnClickListener{
            signUpUser()
        }

        signintext.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    fun signUpUser(){
        if (userText.text.toString().isEmpty()){
            userText.error = "Please enter email"
            userText.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userText.text.toString()).matches()){
            userText.error = "Please enter valid email"
            userText.requestFocus()
            return
        }

        if(passText.text.toString().isEmpty()){
            passText.error = "Please enter password"
            passText.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(userText.text.toString(), passText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    startActivity(Intent(this, DashboardActivity::class.java))
                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}
