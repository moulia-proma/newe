package com.example.classwave.presentation.page.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
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


    private var _isAuthenticated = MutableStateFlow<Boolean?>(null)
    var isAuthenticated = _isAuthenticated.asStateFlow()


    fun checkIsAuthenticated() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(2000)
            _isAuthenticated.value = firebaseAuth.currentUser != null
        }
    }
}