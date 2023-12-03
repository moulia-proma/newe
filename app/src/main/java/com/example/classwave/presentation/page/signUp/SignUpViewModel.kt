package com.example.classwave.presentation.page.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    private val passwordRegex = "\\w{8,}"
    private val emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"
    fun isValidPassword(password: String): Boolean {
        return !password.matches(passwordRegex.toRegex())
    }
    fun isValidEmail(password: String): Boolean {
        return !password.matches(emailRegex.toRegex())
    }

    fun CreateTeacher() {
        viewModelScope.launch {

        }
    }


}