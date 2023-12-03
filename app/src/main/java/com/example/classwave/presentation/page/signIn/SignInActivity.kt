package com.example.classwave.presentation.page.signIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.classwave.presentation.page.parent.ParentActivity
import com.example.classwave.presentation.page.student.StudentActivity
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.example.classwave.R
import com.google.android.material.button.MaterialButton

class SignInActivity : AppCompatActivity() {
    private lateinit var btnSignIn: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userType = intent.getStringExtra("user_type")
        setContentView(R.layout.login)
        btnSignIn = findViewById(R.id.btn_signin)
        btnSignIn.setOnClickListener {
            if (userType == "teacher") {
                val Intent = Intent(this, TeacherActivity::class.java)
                startActivity(Intent)
            } else if (userType == "student") {
                val Intent = Intent(this, StudentActivity::class.java)
                startActivity(Intent)
            } else {
                val Intent = Intent(this, ParentActivity::class.java)
                startActivity(Intent)

            }
        }

    }
}