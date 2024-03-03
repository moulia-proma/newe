package com.example.classwave.presentation.page.signin

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.ActivitySignInBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.parent.ParentActivity
import com.example.classwave.presentation.page.signup.SignUpActivity
import com.example.classwave.presentation.page.student.StudentActivity
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.example.classwave.presentation.util.SnackbarUtil
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
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
        binding.textViewForgetPassword.setOnClickListener {

            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val dialogView = layoutInflater.inflate(R.layout.custom_dialog_forget_password, null)
            dialog.setContentView(dialogView)
            val btnCancel = dialogView.findViewById<ImageView>(R.id.image_view_cancel)

            val btnSend = dialogView.findViewById<View>(R.id.btn_send) as MaterialButton

            btnSend.setOnClickListener {
                val email =
                    (dialogView.findViewById<EditText>(R.id.edit_text_email)).text.toString()
                viewModel.resetPassword(email)
            }
            dialog.show()
            btnCancel.setOnClickListener {
                dialog.dismiss()
                Log.d("_ok", "registerListener: cancel pressed")
            }

        }

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
                            Log.d("_e", "initializeFlowCollectors: error")
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

        lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resetPassword.collectLatest {
                    it?.let {
                        Log.d("_xyz", "initializeFlowCollectors: ${it.data}  x ${it.message}  ")
                        when (it) {

                            is Resource.Error -> {
                                it.message?.let { it1 ->
                                    val dialogView = layoutInflater.inflate(
                                        R.layout.custom_dialog_forget_password,
                                        null
                                    )
                                    val btnSend =
                                        dialogView.findViewById<View>(R.id.btn_send) as MaterialButton
                                    Log.d("_e", "initializeFlowCollectors: i m error")
                                    SnackbarUtil.show(
                                        this@SignInActivity,
                                        it1, btnSend
                                    )
                                }
                            }

                            is Resource.Loading -> {


                            }

                            is Resource.Success -> {

                            }
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