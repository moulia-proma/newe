package com.example.classwave.presentation.page.signout

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow

class SignoutViewModel : ViewModel(){
    fun signOut(){
    Firebase.auth.signOut()
   }
}