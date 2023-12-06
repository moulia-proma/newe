package com.example.classwave.presentation.page.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.classwave.R
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        // Initialize Firebase Auth
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
             val intent = Intent(this,TeacherActivity::class.java)
            startActivity(intent)
        }else{
            val intent = Intent(this,UserTypeActivity::class.java)
            startActivity(intent)
        }
    }
}