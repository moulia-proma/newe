package com.example.classwave.presentation.page.parent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.teacher.Class
import com.example.classwave.presentation.page.teacher.Student
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParentViewModel : ViewModel() {
    private var dbParentRef = FirebaseDatabase.getInstance().getReference("Parents")
    private var dbClassREf = FirebaseDatabase.getInstance().getReference("ClassRoom")
    private var dbStdREf = FirebaseDatabase.getInstance().getReference("Students")

    /*    private val _selectedClass = MutableStateFlow<Class?>(null)
        val selectedClass = _selectedClass.asStateFlow()*/

    private var _createParent = MutableStateFlow<Resource<Parent>?>(null)
    var createParent = _createParent.asStateFlow()

    private var _isClassExists =
        MutableStateFlow<Resource<com.example.classwave.presentation.page.teacher.Class>?>(null)
    var isClassExists = _isClassExists.asStateFlow()

    private var _isStudentExists =
        MutableStateFlow<Resource<com.example.classwave.presentation.page.teacher.Student>?>(null)
    var isStudentExists = _isClassExists.asStateFlow()

    fun setNull() {
        _isClassExists =
            MutableStateFlow<Resource<com.example.classwave.presentation.page.teacher.Class>?>(null)
        isClassExists = _isClassExists.asStateFlow()
    }


    /* fun createParent(clsId: String, name: String) {
         _createParent.value = Resource.Loading()
         if (name.isEmpty()) {
             _createParent.value = Resource.Error("Class name can't be empty")
             return
         }

         val stdId = Firebase.auth.uid.toString()
         var profile = stdImage.random().toString()
         val student = Student(clsId ?: "", stdId ?: "", name, profile)

         val st = ("attendance123" + "_" + stdId)
         Log.d("tag", "addMarks: ${st}")



         dbParentRef.push().setValue(student).addOnCompleteListener {
             //   _createParent.value = Resource.Success(student)
             //  dbMarksRef.push().setValue(stdAttendance)
         }.addOnFailureListener {
             _createParent.value = Resource.Error("User creation Failed")
             Log.d("TAG", "createClass: cls created")
         }

         //fetchStudentByClassId(clsId)
     }*/


    /*   fun findClass(clsId: String) {
           viewModelScope.launch(Dispatchers.IO) {
               dbParentRef.addListenerForSingleValueEvent(object : ValueEventListener {
                   override fun onDataChange(snapshot: DataSnapshot) {

                   }

                   override fun onCancelled(error: DatabaseError) {

                   }

               })
           }
       }*/
    fun isClassExists(clsId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbClassREf.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var cnt = 0
                    snapshot.children.forEach { cls ->
                        if (clsId == cls.child("classId").value.toString()) {
                            cnt = 1
                            Log.d("_xyz", "onDataChange: okok")
                            _isClassExists.value = Resource.Success(
                                Class(

                                    cls.child("classId").value.toString(),
                                    cls.child("teacherId").value.toString(),
                                    cls.child("name").value.toString(),
                                    cls.child("grade").value.toString(),
                                    cls.child("img").value.toString(),
                                )
                            )
                        }

                    }
                    if (cnt == 0) {
                        _isClassExists.value = Resource.Error("No class Exists")
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    _isClassExists.value = Resource.Error("Error")
                }

            })


        }

    }

    fun isStudentExists(clsId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbStdREf.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var cnt = 0
                    snapshot.children.forEach { std ->
                        if (clsId == std.child("studentId").value.toString()) {
                            cnt = 1
                            Log.d("_xyz", "onDataChange: okok")
                            _isStudentExists.value = Resource.Success(
                                Student(
                                    std.child("classId").value.toString(),
                                    std.child("studentId").value.toString(),
                                    std.child("studentName").value.toString(),
                                    std.child("img").value.toString(),
                                )
                            )
                        }

                    }
                    if (cnt == 0) {
                        _isStudentExists.value = Resource.Error("No class Exists")
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    _isStudentExists.value = Resource.Error("Error")
                }

            })


        }

    }


    fun signOut() {
        Firebase.auth.signOut()


    }


}

data class Parent(
    val uid: String, val name: String, val image: String,

    val childId: String, val childName: String, val childPhoto: String,

    val clsId: String, val clsName: String, val classPhoto: String
)