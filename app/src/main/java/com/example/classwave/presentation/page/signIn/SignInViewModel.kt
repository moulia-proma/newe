package com.example.classwave.presentation.page.signIn

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.classwave.data.datasource.remote.model.loginResponse
import com.example.classwave.domain.model.Resource
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignInViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<loginResponse>?>(null)
    val loginState = _loginState.asStateFlow()

    fun signIn(email: String, password: String,context: Context) {
        Log.d("_xyz", "signIn: ${email} ${password}")
        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{

       /*     Log.d("mik", "signIn: ")
            if (it.isSuccessful) {
                _loginState.value = Resource.Success(loginResponse(email, password))
            } else {
                Log.d("_xyz", "signIn: ${it.exception}")
                _loginState.value = Resource.Error("login failed")
            }*/
        }
    }


}