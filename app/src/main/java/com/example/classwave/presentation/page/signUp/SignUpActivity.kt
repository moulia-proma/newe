package com.example.classwave.presentation.page.signUp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.CustomSnackbarLayoutBinding
import com.example.classwave.databinding.SignupBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.signIn.SignInActivity
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: SignupBinding

    private val viewModel: SignUpViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {

        val userType = intent.getStringExtra("user_type")
        super.onCreate(savedInstanceState)
        binding = SignupBinding.inflate(layoutInflater)

        setContentView(binding.root)


        binding.editTextPassword.addTextChangedListener {

            if (viewModel.isValidPassword(binding.editTextPassword.text.toString())) {
                binding.textPasswordValidation.text = "Your password must be at least 8 characters"
            } else {
                binding.textPasswordValidation.text = ""
            }

        }

        if (userType != null) {
            registerFlow(userType)
        }

        binding.btnSignup.setOnClickListener {
            viewModel.createUser(
                binding.editTextEmail.text.toString(),
                binding.editTextName.text.toString(),
                userType.toString(),
                binding.editTextPassword.toString()
            )
        }

        binding.txtAlreadyHaveAnAccount.setOnClickListener {
            Log.d("_TAG", "onCreate: ")
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("user_type", userType)
            startActivity(intent)
        }
    }

    private fun registerFlow(userType: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.authUiState.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {
                                binding.progressBarSignUpLoading.visibility = View.INVISIBLE
                                binding.txtSignup.visibility = View.VISIBLE

                                val snackView = View.inflate(
                                    this@SignUpActivity,
                                    R.layout.custom_snackbar_layout,
                                    null
                                )
                                val snack_binding = CustomSnackbarLayoutBinding.bind(snackView)
                                val snackBar =
                                    Snackbar.make(binding.btnSignup, "", Snackbar.LENGTH_LONG)
                                snackBar.apply {
                                    (view as ViewGroup).addView(snack_binding.root)
                                    snack_binding.txnSnackMsg.text = it.message
                                    show()
                                }
                                snackBar.setBackgroundTint(Color.TRANSPARENT)

                            }

                            is Resource.Loading -> {
                                binding.txtSignup.visibility = View.INVISIBLE
                                binding.progressBarSignUpLoading.visibility = View.VISIBLE
                            }

                            is Resource.Success -> {
                                binding.txtSignup.visibility = View.VISIBLE
                                binding.progressBarSignUpLoading.visibility = View.INVISIBLE
                                if (userType == "teacher") {
                                    val intent =
                                        Intent(this@SignUpActivity, TeacherActivity::class.java)
                                    startActivity(intent)
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}