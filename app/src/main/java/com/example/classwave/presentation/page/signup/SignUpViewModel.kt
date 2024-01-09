package com.example.classwave.presentation.page.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classwave.data.datasource.remote.model.UserItemResponse
import com.example.classwave.domain.model.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(): ViewModel() {

    private var dbRef = FirebaseDatabase.getInstance().getReference("Users")
    private val passwordRegex = "\\w{8,}"
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    private var _userType = MutableStateFlow<Resource<ArrayList<String?>>>(Resource.Success(arrayListOf()))
    var userType = _userType.asStateFlow()

    private fun isValidPassword(password: String): Boolean {
        return password.matches(passwordRegex.toRegex())
    }

    private fun isValidEmail(email: String): Boolean {
        return email.matches(emailRegex.toRegex())
    }

    private val _authUiState = MutableStateFlow<Resource<UserItemResponse>?>(null)
    val authUiState = _authUiState.asStateFlow()


    fun createUser(email: String, name: String, type: String, password: String) {
        _authUiState.value = Resource.Loading()

        if(!isValidEmail(email)) {
            _authUiState.value = Resource.Error("Please enter a valid email")
            return
        }
        if(!isValidPassword(password)) {
            _authUiState.value = Resource.Error("Password length must be more then 8 characters")
            return
        }

        if(name.isEmpty()) {
            _authUiState.value = Resource.Error("Please enter a valid name")
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                findUserType(firebaseAuth.uid.toString())
                if (it.isSuccessful) {
                    auth = Firebase.auth
                    currentUser = auth.currentUser!!
                    val uid =currentUser.uid
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

}