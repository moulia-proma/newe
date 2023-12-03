package com.example.classwave.presentation.page.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.example.classwave.presentation.page.signUp.SignUpActivity
import com.example.classwave.R
import com.example.classwave.databinding.ActivityMaiBinding
import com.example.classwave.databinding.UserTypeOptionBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: UserTypeOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = UserTypeOptionBinding.inflate(layoutInflater)
        setContentView(binding.root)


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