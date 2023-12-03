package com.example.classwave.presentation.page.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.example.classwave.presentation.page.signUp.SignUpActivity
import com.example.classwave.R

class MainActivity : AppCompatActivity() {

    private lateinit var cardTeacher: CardView
    private lateinit var cardParent: CardView
    private lateinit var cardStudent: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_type_option)
        cardTeacher = findViewById(R.id.card_tcr)
        cardParent = findViewById(R.id.card_parent)
        cardStudent = findViewById(R.id.card_stdnt)
        cardTeacher.setOnClickListener {
            val Intent = Intent(this, SignUpActivity::class.java)
            Intent.putExtra("user_type", "teacher")
            startActivity(Intent)
        }
        cardStudent.setOnClickListener {
            val Intent = Intent(this, SignUpActivity::class.java)
            Intent.putExtra("user_type", "student")
            startActivity(Intent)
        }
        cardParent.setOnClickListener {
            val Intent = Intent(this, SignUpActivity::class.java)
            Intent.putExtra("user_type", "parent")
            startActivity(Intent)
        }


    }
}