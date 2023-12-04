package com.example.classwave.presentation.page.signUp

import androidx.lifecycle.ViewModel
import com.example.classwave.data.datasource.remote.model.UserItemResponse
import com.example.classwave.domain.model.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignUpViewModel : ViewModel() {

    private var dbRef = FirebaseDatabase.getInstance().getReference("Users")
    private val passwordRegex = "\\w{8,}"
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"

    fun isValidPassword(password: String): Boolean {
        return !password.matches(passwordRegex.toRegex())
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(emailRegex.toRegex())
    }

    private val _authUiState = MutableStateFlow<Resource<UserItemResponse>?>(null)
    val authUiState = _authUiState.asStateFlow()


    fun createUser(email: String, name: String, type: String, password: String) {
        _authUiState.value = Resource.Loading()

        if (isValidEmail(email)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val uid = dbRef.push().key!!
                        val user = UserItemResponse(email, name, type, uid)

                        dbRef.push().setValue(user)
                            .addOnCompleteListener {
                                _authUiState.value = Resource.Success(user)
                            }
                            .addOnFailureListener {
                                _authUiState.value = Resource.Error("User creation Failed")
                            }
                    } //
                    else {
                        _authUiState.value = Resource.Error("Authentication Failed")
                    }
                }.addOnFailureListener { exception ->
                    _authUiState.value = Resource.Error(
                        exception.message ?: ""
                    )
                }
        } //
        else {
            _authUiState.value = Resource.Error(
                message = "Email is not valid."
            )
        }
    }


}