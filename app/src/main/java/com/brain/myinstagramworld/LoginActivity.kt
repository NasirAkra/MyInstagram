package com.brain.myinstagramworld

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.brain.myinstagramworld.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Navigate to SignUpActivity
        binding.CreateAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Handle Login
        binding.Login.setOnClickListener {
            val email = binding.email.editText?.text.toString().trim()
            val password = binding.password.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all the information", Toast.LENGTH_SHORT).show()
            } else {
                // Login user with FirebaseAuth
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
