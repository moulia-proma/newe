package com.example.classwave.presentation.page.signIn

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.CustomSnackbarLayoutBinding
import com.example.classwave.databinding.LoginBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.parent.ParentActivity
import com.example.classwave.presentation.page.student.StudentActivity
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    private val viewModel: SignInViewModel by viewModels()
    private lateinit var binding: LoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TAG", "onCreate: ")
        super.onCreate(savedInstanceState)
        val userType = intent.getStringExtra("user_type")
        binding = LoginBinding.inflate(layoutInflater)
        Log.d("Tag", "onCreate: ")

        setContentView(binding.root)
        auth = Firebase.auth
        if (userType != null) {
            registerFlow(userType)
        }
        val email =   binding.editTextSignInEmail.text
        val password =   binding.editTextSignInPassword.text
        binding.btnSignin.setOnClickListener {
            if(password.isNotEmpty() && email.isNotEmpty()){
                viewModel.signIn(
                    email.toString(),
                    password.toString(),
                    this
                )
            }else{
                Log.d("_tag", "onCreate: null login ${email}  ${password}")
                val snackRef = com.example.classwave.presentation.Snackbars.Snackbar()
                snackRef.ShowSnack(this,"Email or Password Can't be empty",binding.btnSignin)

            }

        }

    }


    fun registerFlow(userType: String) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.loginState.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {
                                binding.progressBarSignInLoading.visibility = View.INVISIBLE
                                binding.txtSignIn.visibility = View.VISIBLE

                                val snackView = View.inflate(
                                    this@SignInActivity,
                                    R.layout.custom_snackbar_layout,
                                    null
                                )
                                val snack_binding = CustomSnackbarLayoutBinding.bind(snackView)
                                val snackBar =
                                    Snackbar.make(binding.btnSignin, "", Snackbar.LENGTH_LONG)
                                snackBar.apply {
                                    (view as ViewGroup).addView(snack_binding.root)
                                    snack_binding.txnSnackMsg.text = it.message
                                    show()
                                }
                                snackBar.setBackgroundTint(Color.TRANSPARENT)


                            }

                            is Resource.Loading -> {

                                binding.txtSignIn.visibility = View.INVISIBLE
                                binding.progressBarSignInLoading.visibility = View.VISIBLE
                            }

                            is Resource.Success -> {
                                binding.txtSignIn.visibility = View.VISIBLE
                                binding.progressBarSignInLoading.visibility = View.INVISIBLE
                                if (userType == "teacher") {
                                    val intent =
                                        Intent(this@SignInActivity, TeacherActivity::class.java)
                                   clearPreviousActivites(intent)
                                    startActivity(intent)
                                } else if (userType == "student") {
                                    val intent =
                                        Intent(this@SignInActivity, StudentActivity::class.java)
                                    clearPreviousActivites(intent)
                                    startActivity(intent)
                                } else {
                                    val intent =
                                        Intent(this@SignInActivity, ParentActivity::class.java)
                                    clearPreviousActivites(intent)
                                    startActivity(intent)
                                }
                            }
                        }
                    }

                }
            }


        }


    }
    private fun clearPreviousActivites(intent: Intent){
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }


}