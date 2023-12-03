package com.example.classwave.presentation.page.signUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.example.classwave.presentation.page.parent.ParentActivity
import com.example.classwave.presentation.page.signIn.SignInActivity
import com.example.classwave.presentation.page.student.StudentActivity
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.example.classwave.databinding.SignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var  binding: SignupBinding
    private lateinit var  auth: FirebaseAuth
    private val viewmodel :SignUpViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        val userType = intent.getStringExtra("user_type")
        super.onCreate(savedInstanceState)
        binding = SignupBinding.inflate(layoutInflater)

        setContentView(binding.root)
        auth= FirebaseAuth.getInstance()


        binding.editTextPassword.addTextChangedListener{

            if (viewmodel.isValidPassword(binding.editTextPassword.text.toString())){
                    binding.textPasswordValidation .text = "Your password must be at least 8 characters"

                }else{
                    binding.textPasswordValidation .text = ""
            }

        }
        binding.editTextEmail.addTextChangedListener{

            if (viewmodel.isValidEmail(binding.editTextPassword.text.toString())){
                binding.textPasswordValidation .text = "please enter a valid email"

            }else{
                binding.textPasswordValidation .text = ""
            }

        }

        binding.btnSignup.setOnClickListener {

            auth.createUserWithEmailAndPassword(binding.editTextEmail.text.toString(),binding.editTextPassword.text.toString()).addOnCompleteListener{
                if(it.isSuccessful){
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
            }.addOnFailureListener{
                Toast.makeText(applicationContext,it.localizedMessage, Toast.LENGTH_LONG).show()
            }


        }

        binding.txtAlreadyHaveAnAccount.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("user_type", userType)
            startActivity(intent)
        }
    }
}