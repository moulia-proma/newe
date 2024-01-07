package com.example.classwave.presentation.page.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private var firebaseAuth = Firebase.auth
    private var dbUserRef = FirebaseDatabase.getInstance().getReference("Users")

    private var _isAuthenticated = MutableStateFlow<Boolean?>(null)
    var isAuthenticated = _isAuthenticated.asStateFlow()
    private var _userType = MutableStateFlow<String>("")
    var userType = _userType.asStateFlow()

    fun checkIsAuthenticated() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000)
            _isAuthenticated.value = firebaseAuth.currentUser != null
        }
    }

    fun findUserType(uId: String) {

        viewModelScope.launch(Dispatchers.IO) {
            dbUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { user ->
                        if (user.child("uid").value.toString() == uId) {
                            _userType.value = user.child("type").value.toString()

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }

        Log.d("_pt", "findUserType: ${_userType.value} ")
    }
}