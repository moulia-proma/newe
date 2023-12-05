package com.example.classwave.data.datasource.remote.model

import android.provider.ContactsContract.CommonDataKinds.Email

data class loginResponse (
    val email: String,
    val password:String
)