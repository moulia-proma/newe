package com.example.classwave.presentation.page.signIn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.classwave.presentation.page.parent.ParentActivity
import com.example.classwave.presentation.page.student.StudentActivity
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.example.classwave.R
import com.example.classwave.databinding.LoginBinding
import com.example.classwave.databinding.SignupBinding
import com.example.classwave.databinding.UserTypeOptionBinding
import com.google.android.material.button.MaterialButton

class SignInActivity : AppCompatActivity() {


    private lateinit var  binding:LoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userType = intent.getStringExtra("user_type")
        binding = LoginBinding.inflate(layoutInflater)

        setContentView(R.layout.login)

          binding.btnSignin.setOnClickListener {

            if (userType == "teacher") {
                val intent = Intent(this, TeacherActivity::class.java)
                startActivity(intent)
            } else if (userType == "student") {
                val intent = Intent(this, StudentActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, ParentActivity::class.java)
                startActivity(intent)
            }

        }

    }
}