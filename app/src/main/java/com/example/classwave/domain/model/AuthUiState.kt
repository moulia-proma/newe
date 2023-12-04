package com.example.classwave.domain.model

sealed class Resource<R>(val data: R?, val message: String?) {
    class Error<T>(message: String) : Resource<T>(null, message)
    class Success<T>(data: T) : Resource<T>(data, null)
    class Loading<T>(data: T? = null) : Resource<T>(data, null)
}
