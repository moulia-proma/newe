package com.example.classwave.presentation.page.signin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classwave.data.datasource.remote.model.loginResponse
import com.example.classwave.domain.model.Resource
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel() {

    private var dbRef = FirebaseDatabase.getInstance().getReference("Users")
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val passwordRegex = "\\w{8,}"
    private val emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"


    private val _loginState = MutableStateFlow<Resource<loginResponse>?>(null)
    val loginState = _loginState.asStateFlow()

    private val _resetPassword= MutableStateFlow<Resource<String>?>(null)
    val resetPassword = _loginState.asStateFlow()

    private var _userType =
        MutableStateFlow<Resource<ArrayList<String?>>>(Resource.Success(arrayListOf()))
    var userType = _userType.asStateFlow()



    fun signIn(email: String, password: String) {
        _loginState.value = Resource.Loading()
        _userType.value = Resource.Loading()

        if (!isValidEmail(email)) {
            _loginState.value = Resource.Error("Please enter a valid email")
            _userType.value = Resource.Error("Please enter a valid email")
            return
        }
        if (!isValidPassword(password)) {
            _loginState.value = Resource.Error("Password length must be more then 8 characters")
            _userType.value = Resource.Error("Password length must be more then 8 characters")
            return
        }

        Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) {
                    _loginState.value = Resource.Success(loginResponse(email, password))
                    findUserType(firebaseAuth.uid.toString())
                } else {
                    _loginState.value =
                        Resource.Error("please varify your email!check your email there is a varification link")
                    _userType.value =
                        Resource.Error("please varify your email!check your email there is a varification link")

                }

            } else {
                _userType.value = Resource.Error(it.exception.toString())

            }
        }
    }


    private fun isValidPassword(password: String): Boolean {
        return password.matches(passwordRegex.toRegex())
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(emailRegex.toRegex())
    }

    fun findUserType(uId: String) {
        _userType.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { user ->
                        if (user.child("uid").value.toString() == uId) {
                            val arr = arrayListOf<String?>()
                            arr.add(user.child("type").value.toString())
                            arr.add(user.child("name").value.toString())
                            arr.add(user.child("email").value.toString())
                            _userType.value = Resource.Success(arr)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _userType.value = Resource.Error("error")
                }

            })
        }

        Log.d("_pt", "findUserType: ${_userType.value} ")
    }

    fun resetPassword(email: String) {
        Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _resetPassword.value = Resource.Success("A password reset email has been sent to your email!please check!")
            }
        }.addOnFailureListener {
            _resetPassword.value = Resource.Error("Please Enter a correct email! or Try again!")
        }
        }
    }


