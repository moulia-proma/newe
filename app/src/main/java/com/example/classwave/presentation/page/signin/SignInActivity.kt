package com.example.classwave.presentation.page.signin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
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
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SignInActivity : AppCompatActivity() {

    private val viewModel: SignInViewModel by viewModels()
    private lateinit var binding: ActivitySignInBinding
    private var userType: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        userType = intent.getStringExtra("user_type").toString()


        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
     // binding.blurView.setupWith(binding.root,RenderScriptBlur(this)).setBlurRadius(2F)
        binding.btnSignIn.text = "SignIn"



        registerListener()
        initializeFlowCollectors()
    }



    private fun registerListener() {

        binding.textViewCreateAnAccount.setOnClickListener {
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
                viewModel.userType.collectLatest {
                    when (it) {
                        is Resource.Error -> {
                            showError(it.message)
                        }

                        is Resource.Loading -> {
                            binding.btnSignIn.text = ""
                            binding.progressBarSignInLoading.visibility = View.VISIBLE
                        }

                        is Resource.Success -> {
                            showSuccess(it.data)
                        }
                    }

                }
            }
        }

    }

    private fun showError(message: String?) {
        binding.btnSignIn.text = "Sign In"
        binding.progressBarSignInLoading.visibility = View.INVISIBLE
        message?.let { it1 ->
            SnackbarUtil.show(
                this@SignInActivity,
                it1, binding.btnSignIn
            )
        }
    }

    private fun showSuccess(data: ArrayList<String?>?) {
        binding.progressBarSignInLoading.visibility = View.INVISIBLE
        if (data?.isNotEmpty() == true) {
            if (data[0] == "teacher") {
                val intent =
                    Intent(this@SignInActivity, TeacherActivity::class.java)
                startActivity(intent)
            } else if (data[0] == "parent") {
                val intent =
                    Intent(this@SignInActivity, ParentActivity::class.java)
                startActivity(intent)

            } else if (data[0] == "student") {

                val intent =
                    Intent(this@SignInActivity, StudentActivity::class.java)
                intent.putExtra("name", data[1])
                intent.putExtra("email", data[2])
                startActivity(intent)
            }
        }

    }
}