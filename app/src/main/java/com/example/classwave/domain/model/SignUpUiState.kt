package com.example.classwave.domain.model

sealed class SignUpUiState<R>(val data: R?, val message: String?) {
    class Error<T>(message: String) : SignUpUiState<T>(null, message) {}
    class Success<T>(data: T) : SignUpUiState<T>(data, null) {}

    class Loading <T>():SignUpUiState<T>(data=null,message = null){}
}