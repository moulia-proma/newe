package com.example.classwave.presentation.page.signUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.classwave.presentation.page.parent.ParentActivity
import com.example.classwave.presentation.page.signIn.SignInActivity
import com.example.classwave.presentation.page.student.StudentActivity
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.example.classwave.R
import com.google.android.material.button.MaterialButton

class SignUpActivity : AppCompatActivity() {
    private lateinit var txtAlreadyHaveAccount: TextView
    private lateinit var btnSignup: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        val userType = intent.getStringExtra("user_type")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)
        txtAlreadyHaveAccount = findViewById(R.id.txt_already_have_an_account)
        btnSignup = findViewById(R.id.btn_signup)
        btnSignup.setOnClickListener {

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
        txtAlreadyHaveAccount.setOnClickListener {
            val Intent = Intent(this, SignInActivity::class.java)
            Intent.putExtra("user_type", userType)
            startActivity(Intent)
        }
    }
}