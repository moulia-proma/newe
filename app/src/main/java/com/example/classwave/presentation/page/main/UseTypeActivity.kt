package com.example.classwave.presentation.page.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.classwave.presentation.page.signUp.SignUpActivity
import com.example.classwave.databinding.UserTypeOptionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserTypeActivity : AppCompatActivity() {

    private lateinit var binding: UserTypeOptionBinding

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = UserTypeOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth


        binding.cardTcr.setOnClickListener {
            val Intent = Intent(this, SignUpActivity::class.java)
            Intent.putExtra("user_type", "teacher")
            startActivity(Intent)
        }

        binding.cardStdnt.setOnClickListener {
            val Intent = Intent(this, SignUpActivity::class.java)
            Intent.putExtra("user_type", "student")
            startActivity(Intent)
        }

        binding.cardParent.setOnClickListener {
            val Intent = Intent(this, SignUpActivity::class.java)
            Intent.putExtra("user_type", "parent")
            startActivity(Intent)
        }


    }
}