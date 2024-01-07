package com.example.classwave.presentation.page.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.databinding.ActivitySignUpBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.parent.ParentActivity
import com.example.classwave.presentation.page.signin.SignInActivity
import com.example.classwave.presentation.page.student.StudentActivity
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.example.classwave.presentation.util.SnackbarUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    private var userType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userType = intent.getStringExtra("user_type") ?: ""


        registerListener()
        initializeFlowCollectors()
    }

    private fun registerListener() {
        binding.btnSignup.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val name = binding.editTextName.text.toString()

            viewModel.createUser(
                email = email, password = password,
                name = name, type = userType
            )
        }

        binding.txtAlreadyHaveAnAccount.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("user_type", userType)
            startActivity(intent)
            finish()
        }
    }

    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.authUiState.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> showErrorMessage(message = it.message ?: "")
                            is Resource.Loading -> showLoadingView()
                            is Resource.Success -> showSuccessView()
                        }
                    }
                }
            }
        }
    }


    private fun showSuccessView() {
        binding.txtSignup.visibility = View.VISIBLE
        binding.progressBarSignUpLoading.visibility = View.INVISIBLE

        if (userType == "teacher") {
            val intent = Intent(this@SignUpActivity, TeacherActivity::class.java)
            startActivity(intent)
        } else if (userType == "student") {
            val intent = Intent(this@SignUpActivity, StudentActivity::class.java)
            startActivity(intent)
        }else if(userType == "parent"){
            val intent = Intent(this@SignUpActivity, ParentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showErrorMessage(message: String) {
        binding.progressBarSignUpLoading.visibility = View.INVISIBLE
        binding.txtSignup.visibility = View.VISIBLE

        SnackbarUtil.show(this@SignUpActivity, message, binding.btnSignup)
    }

    private fun showLoadingView() {
        binding.txtSignup.visibility = View.INVISIBLE
        binding.progressBarSignUpLoading.visibility = View.VISIBLE
    }
}