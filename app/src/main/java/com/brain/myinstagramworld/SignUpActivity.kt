@file:Suppress("DEPRECATION")

package com.brain.myinstagramworld

import android.annotation.SuppressLint
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var user: User

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER) { imageUrl ->
                user.image = imageUrl
                binding.profileImage.setImageURI(uri)
            }
        }
    }

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text = "<font color=#FF000000>Already Have An Account</font> <font color=#1E88E5>Login</font>"
        binding.login.text = Html.fromHtml(text)

        user = User()

        // Check if we are updating the profile
        if (intent.hasExtra("MODE") && intent.getIntExtra("MODE", -1) == 1) {
            binding.register.text = "Update Profile"

            Firebase.firestore.collection(USER_NODE).document(FirebaseAuth.getInstance().currentUser!!.uid)
                .get()
                .addOnSuccessListener { document ->
                    val fetchedUser = document.toObject<User>()
                    if (fetchedUser != null) {
                        user = fetchedUser // Use the fetched user object
                        if (!user.image.isNullOrEmpty()) {
                            Picasso.get().load(user.image).into(binding.profileImage)
                        }
                        binding.name.editText?.setText(user.name)
                        binding.password.editText?.setText(user.password)
                        binding.email.editText?.setText(user.email)
                    }
                }
        }

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.addImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.register.setOnClickListener {
            val name = binding.name.editText?.text.toString().trim()
            val email = binding.email.editText?.text.toString().trim()
            val password = binding.password.editText?.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all the information", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            user.name = name
            user.email = email
            user.password = password

            if (intent.hasExtra("MODE") && intent.getIntExtra("MODE", -1) == 1) {
                // Update existing user profile
                Firebase.firestore.collection(USER_NODE).document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .set(user)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Create new user
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            Firebase.firestore.collection(USER_NODE)
                                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                                .set(user)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        startActivity(Intent(this, HomeActivity::class.java))
                                        finish()
                                    } else {
                                        Toast.makeText(this, task.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, result.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
