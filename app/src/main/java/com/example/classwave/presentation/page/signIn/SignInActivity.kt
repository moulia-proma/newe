package com.example.classwave.presentation.page.signIn

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.presentation.page.parent.ParentActivity
import com.example.classwave.presentation.page.student.StudentActivity
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.example.classwave.R
import com.example.classwave.databinding.CustomSnackbarLayoutBinding
import com.example.classwave.databinding.LoginBinding
import com.example.classwave.databinding.SignupBinding
import com.example.classwave.databinding.UserTypeOptionBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.signUp.SignUpViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    private val viewModel: SignInViewModel by viewModels()
    private lateinit var  binding:LoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TAG", "onCreate: ")
        super.onCreate(savedInstanceState)
        val userType = intent.getStringExtra("user_type")
        binding = LoginBinding.inflate(layoutInflater)
        Log.d("Tag", "onCreate: ")

        setContentView(binding.root)
        if (userType!=null){
            registerFlow(userType)
        }



          binding.btnSignin.setOnClickListener {
              Log.d("_tag", "onCreate: ")
            //  viewModel.signIn(binding.editTextSignInEmail.text.toString(),binding.editTextSignInPassword.text.toString(),this)
              Firebase.auth.signInWithEmailAndPassword(binding.editTextSignInEmail.text.toString(),binding.editTextSignInPassword.text.toString())
                  .addOnCompleteListener(this) { task ->
                      if (task.isSuccessful) {
                          // Sign in success, update UI with the signed-in user's information
                          Log.d("success", "signInWithEmail:success")
                        //  val user = auth.currentUser
                         // updateUI(user)
                          Toast.makeText(
                              baseContext,
                              "Authentication Success",
                              Toast.LENGTH_SHORT,
                          ).show()
                      } else {
                          // If sign in fails, display a message to the user.
                          Log.w("success", "signInWithEmail:failure", task.exception)
                          Toast.makeText(
                              baseContext,
                              "Authentication failed.",
                              Toast.LENGTH_SHORT,
                          ).show()
                         // updateUI(null)
                      }
                  }


         /*   if (userType == "teacher") {
                val intent = Intent(this, TeacherActivity::class.java)
                startActivity(intent)
            } else if (userType == "student") {
                val intent = Intent(this, StudentActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, ParentActivity::class.java)
                startActivity(intent)
            }*/

        }

    }

    fun registerFlow(userType:String){
        lifecycleScope.launch {
             repeatOnLifecycle(Lifecycle.State.CREATED){
                 viewModel.loginState.collectLatest {
                     it?.let{
                         when(it){
                             is Resource.Error ->{
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
                                     startActivity(intent)
                                 }else if(userType =="parent"){

                                 }else{

                                 }
                             }
                         }
                     }

                 }
             }


        }




    }


}