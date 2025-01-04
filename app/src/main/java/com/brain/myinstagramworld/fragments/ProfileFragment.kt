package com.brain.myinstagramworld.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.brain.myinstagramworld.Model.User
import com.brain.myinstagramworld.SignUpActivity
import com.brain.myinstagramworld.databinding.FragmentProfileBinding
import com.brain.myinstagramworld.utils.USER_NODE
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding= FragmentProfileBinding.inflate(inflater,container,false)


      binding.editProfile.setOnClickListener {
          val intent=Intent(activity,SignUpActivity::class.java)
          intent.putExtra("MODE",1)
          activity?.startActivity(intent)
          activity?.finish()
      }


        return  binding.root



    }

    companion object;

    override fun onStart() {
        super.onStart()
        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).get()
            .addOnSuccessListener {
                val user :User=it.toObject<User>()!!
                binding.name.text = user.name
                binding.bio.text = user.password
                if(!user.image.isNullOrEmpty())
                {
                    Picasso.get().load(user.image).into(binding.profileImage)
                }






            }
    }
}