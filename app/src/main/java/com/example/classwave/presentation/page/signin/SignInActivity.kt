package com.example.classwave.presentation.page.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.databinding.ActivitySignInBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.parent.ParentActivity
import com.example.classwave.presentation.page.signup.SignUpActivity
import com.example.classwave.presentation.page.student.StudentActivity
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.example.classwave.presentation.util.SnackbarUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private val viewModel: SignInViewModel by viewModels()
    private lateinit var binding: ActivitySignInBinding

    private var userType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userType = intent.getStringExtra("user_type") ?: ""


        registerListener()
        initializeFlowCollectors()
    }


    private fun registerListener() {
        binding.txtSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            intent.putExtra("user_type", userType)
            startActivity(intent)
        }
        binding.btnSignIn.setOnClickListener {
            val email = binding.editTextSignInEmail.text.toString()
            val password = binding.editTextSignInPassword.text.toString()
            viewModel.signIn(email, password)
        }
    }


    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loginState.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> showErrorMessage(it.message ?: "")
                            is Resource.Loading -> showLoadingView()
                            is Resource.Success -> showSuccessView()
                        }
                    }

                }
            }
        }

    }

    private fun showSuccessView() {
        binding.txtSignIn.visibility = View.VISIBLE
        binding.progressBarSignInLoading.visibility = View.INVISIBLE

        val intent = when (userType) {
            "teacher" -> {
                Intent(this@SignInActivity, TeacherActivity::class.java)
            }

            "student" -> {
                Intent(this@SignInActivity, StudentActivity::class.java)
            }

            else -> {
                Intent(this@SignInActivity, ParentActivity::class.java)
            }
        }
        startActivity(intent)
        finish()
    }

    private fun showErrorMessage(message: String) {
        binding.progressBarSignInLoading.visibility = View.INVISIBLE
        binding.txtSignIn.visibility = View.VISIBLE

        SnackbarUtil.show(this@SignInActivity, message, binding.btnSignIn)
    }

    private fun showLoadingView() {
        binding.txtSignIn.visibility = View.INVISIBLE
        binding.progressBarSignInLoading.visibility = View.VISIBLE
    }


}