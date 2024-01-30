package com.example.classwave.presentation.page.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        binding.btnSignUp.setOnClickListener {
           val email = binding.editTextSignUpEmail.text.toString()
            val password = binding.editTextSignUpPassword.text.toString()
            val name = binding.editTextSignUpFullName.text.toString()

            viewModel.createUser(
                email = email, password = password,
                name = name, type = userType
            )

          /*  viewModel.emailAuthentcationCode(email)*/


        }

        binding.textViewAlreadyHaveAccount.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("user_type", userType)
            startActivity(intent)
            finish()
        }
    }

    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userType.collectLatest {
                    Log.d("_abd", "initializeFlowCollectors: ${it.data}")
                    when (it) {
                        is Resource.Error -> {
                            Log.d("_e", "initializeFlowCollectors: error")
                            showError(it.message)
                        }

                        is Resource.Loading -> {
                            Log.d("_e", "initializeFlowCollectors: loading")
                            binding.btnSignUp.text=""
                            binding.progressBarSignInLoading.visibility=View.VISIBLE

                        }
                        is Resource.Success -> {
                         /*   if (it.data?.isNotEmpty() == true) {
                                if (it.data[0] == "teacher") {
                                    val intent =
                                        Intent(this@SignUpActivity, TeacherActivity::class.java)
                                    startActivity(intent)
                                } else if (it.data[0] == "parent") {
                                    val intent =
                                        Intent(this@SignUpActivity, ParentActivity::class.java)
                                    startActivity(intent)

                                } else if (it.data[0] == "student") {

                                    val intent =
                                        Intent(this@SignUpActivity, StudentActivity::class.java)
                                    intent.putExtra("name", it.data[1])
                                    intent.putExtra("email", it.data[2])
                                    startActivity(intent)

                                }
                            }
*/

                        }
                    }

                }
            }
        }
    }

    private fun showError(message: String?) {
        Log.d("_e", "showError: abc")
        binding.btnSignUp.text = "Sign Up"
        binding.progressBarSignInLoading.visibility = View.INVISIBLE
        message?.let { it1 ->
            SnackbarUtil.show(
                this,
                it1, binding.btnSignUp
            )
        }
    }

/*    private fun showSuccess(data: ArrayList<String?>?) {
        binding.progressBarSignInLoading.visibility = View.INVISIBLE
        if (data?.isNotEmpty() == true) {
            if (data[0] == "teacher") {
                val intent =
                    Intent(this, TeacherActivity::class.java)
                startActivity(intent)
            } else if (data[0] == "parent") {
                val intent =
                    Intent(this, ParentActivity::class.java)
                startActivity(intent)

            } else if (data[0] == "student") {

                val intent =
                    Intent(this, StudentActivity::class.java)
                intent.putExtra("name", data[1])
                intent.putExtra("email", data[2])
                startActivity(intent)
            }
        }

    }*/


    /*    private fun showSuccessView() {
            binding.txtSignup.visibility = View.VISIBLE
            binding.progressBarSignUpLoading.visibility = View.INVISIBLE

            if (userType == "teacher") {
                val intent = Intent(this@SignUpActivity, TeacherActivity::class.java)
                startActivity(intent)
            } else if (userType == "student") {


                val intent = Intent(this@SignUpActivity, StudentActivity::class.java)
                startActivity(intent)
            } else if (userType == "parent") {
                val intent = Intent(this@SignUpActivity, ParentActivity::class.java)
                startActivity(intent)
            }
        }*/

    private fun showErrorMessage(message: String) {
        binding.progressBarSignInLoading.visibility = View.INVISIBLE
        binding.btnSignUp.text = "Sign Up"

        SnackbarUtil.show(this@SignUpActivity, message, binding.btnSignUp)
    }

    private fun showLoadingView() {
        binding.btnSignUp.text = ""
        binding.progressBarSignInLoading.visibility = View.VISIBLE
    }
}