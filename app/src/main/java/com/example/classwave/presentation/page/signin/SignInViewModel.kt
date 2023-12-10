package com.example.classwave.presentation.page.signin

import androidx.lifecycle.ViewModel
import com.example.classwave.data.datasource.remote.model.loginResponse
import com.example.classwave.domain.model.Resource
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel() {

    private var dbRef = FirebaseDatabase.getInstance().getReference("Users")
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val passwordRegex = "\\w{8,}"
    private val emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"


    private val _loginState = MutableStateFlow<Resource<loginResponse>?>(null)
    val loginState = _loginState.asStateFlow()


    fun signIn(email: String, password: String) {
        _loginState.value = Resource.Loading()

        if(!isValidEmail(email)) {
            _loginState.value = Resource.Error("Please enter a valid email")
            return
        }
        if(!isValidPassword(password)) {
            _loginState.value = Resource.Error("Password length must be more then 8 characters")
            return
        }

        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                _loginState.value = Resource.Success(loginResponse(email, password))
            } else {
                _loginState.value = Resource.Error(it.exception.toString())
            }
        }
    }


    private fun isValidPassword(password: String): Boolean {
        return password.matches(passwordRegex.toRegex())
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(emailRegex.toRegex())
    }

}