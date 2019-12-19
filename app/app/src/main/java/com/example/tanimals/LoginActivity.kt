package com.example.tanimals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.passText
import kotlinx.android.synthetic.main.activity_login.userText
import kotlinx.android.synthetic.main.activity_signup.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        val signUpText = findViewById<TextView>(R.id.signuptext)

        signUpText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        signInButton.setOnClickListener{
            login()
        }
    }

        public override fun onStart() {
            super.onStart()
            // Check if user is signed in (non-null) and update UI accordingly.
            val currentUser = auth.currentUser
            updateUI(currentUser)
        }
        private fun updateUI(currentUser: FirebaseUser?){
            if(currentUser != null){
                startActivity(Intent(this,DashboardActivity::class.java))
                finish()
            }
        }

    fun login(){
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

        auth.signInWithEmailAndPassword(userText.text.toString(),passText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(baseContext, "Login failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


}
