@file:Suppress("DEPRECATION")

package com.brain.myinstagramworld

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.brain.myinstagramworld.Model.User
import com.brain.myinstagramworld.databinding.ActivitySignUpBinding
import com.brain.myinstagramworld.utils.USER_NODE
import com.brain.myinstagramworld.utils.USER_PROFILE_FOLDER
import com.brain.myinstagramworld.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var user: User

    private val luncher=registerForActivityResult(ActivityResultContracts.GetContent())
    {
        uri->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER){
                user.image=it
                binding.profileImage.setImageURI(uri)

            }

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val text = "<font color=#FF000000>Already Have An Account</font> <font color=#1E88E5>Login</font>"
        binding.login.text = Html.fromHtml(text)
        user=User()
        binding.login.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        binding.addImage.setOnClickListener {
            luncher.launch("image/*")
        }

        binding.register.setOnClickListener {
            // Get input from EditText fields
            val name = binding.name.editText?.text.toString().trim()
            val email = binding.email.editText?.text.toString().trim()
            val password = binding.password.editText?.text.toString().trim()

            // Check if fields are empty
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all the information", Toast.LENGTH_SHORT).show()

            }
            else
            {



            // Create user with Firebase
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                       user.name=name
                       user.password=password
                       user.email=email
                        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).set(user)
                            .addOnCompleteListener {
                                startActivity(Intent(this,HomeActivity::class.java))
                                finish()


                        }

                    } else {
                        Toast.makeText(this, result.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
