package com.example.classwave.presentation.page.teacher

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classwave.domain.model.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherViewModel @Inject constructor() : ViewModel() {
    private var dbRef = FirebaseDatabase.getInstance().getReference("ClassRoom")
    private val uid = Firebase.auth.currentUser?.uid

    private val _classList = MutableStateFlow<List<Class>>(arrayListOf())
    var classList = _classList.asStateFlow()

    fun fetchClassByTeacher(teacherId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _classList.value = arrayListOf(
                Class(
                    tcrId = "1",
                    name = "Demo Class",

                    grade = "Pre-School",
                    clsId = "5"
                ),

                Class(
                    tcrId = "2",
                    name = "General Knowledge",
                    grade = "Pre-School",
                    clsId = "4"
                ),
            )
        }
    }

    fun createClass(name: String, grade: String) {

        val _createClass = MutableStateFlow<Resource<Class>?>(null)
        val craeteflowClass = _createClass.asStateFlow()
        _createClass.value = Resource.Loading()
        if (name.isEmpty()) {
            _createClass.value = Resource.Error("Class name can't be empty")
            return
        }
        if (name.isEmpty()) {
            _createClass.value = Resource.Error("You have to select at least one grade")
            return
        }


        val clsId = dbRef.push().key
        val clsRoom = Class(clsId ?: "", uid ?: "", name, grade)

        dbRef.push().setValue(clsRoom)
            .addOnCompleteListener {
                _createClass.value = Resource.Success(clsRoom)
            }
            .addOnFailureListener {
                _createClass.value = Resource.Error("User creation Failed")
                Log.d("TAG", "createClass: cls created")
            }
    }

}

data class Class(
    var clsId: String,
    var tcrId: String,
    var name: String,
    var grade: String
)