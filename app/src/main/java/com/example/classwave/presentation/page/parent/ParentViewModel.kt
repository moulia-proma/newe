package com.example.classwave.presentation.page.parent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classwave.data.datasource.remote.model.UserItemResponse
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
    private var dbChildRef = FirebaseDatabase.getInstance().getReference("children")
    private var dbUserRef = FirebaseDatabase.getInstance().getReference("Users")
    private var dbClassREf = FirebaseDatabase.getInstance().getReference("ClassRoom")
    private var dbStdREf = FirebaseDatabase.getInstance().getReference("Students")

    /*    private val _selectedClass = MutableStateFlow<Class?>(null)
        val selectedClass = _selectedClass.asStateFlow()*/

    private var _createChild = MutableStateFlow<Resource<Child>?>(null)
    var createChild = _createChild.asStateFlow()


    private var _userType =
        MutableStateFlow<Resource<ArrayList<String?>>>(Resource.Success(arrayListOf()))
    var userType = _userType.asStateFlow()

    private var _isClassExists =
        MutableStateFlow<Resource<com.example.classwave.presentation.page.teacher.Class>?>(null)
    var isClassExists = _isClassExists.asStateFlow()

    private var _isStudentExists =
        MutableStateFlow<Resource<com.example.classwave.presentation.page.teacher.Student>?>(null)
    var isStudentExists = _isStudentExists.asStateFlow()

    private val _childList = MutableStateFlow<Resource<ArrayList<Child>>?>(null)
    var childList = _childList.asStateFlow()

    private val _teacherList = MutableStateFlow<Resource<ArrayList<UserItemResponse>>?>(null)
    var teacherList = _teacherList.asStateFlow()

    fun setNull() {
        _isClassExists =
            MutableStateFlow<Resource<com.example.classwave.presentation.page.teacher.Class>?>(null)
        isClassExists = _isClassExists.asStateFlow()
        _createChild = MutableStateFlow<Resource<Child>?>(null)
        createChild = _createChild.asStateFlow()
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
                        //  Log.d("_xyz", "onDataChange: before match $clsId ${std.child("studentId").value.toString()}")
                        if (clsId == std.child("studentId").value.toString()) {
                            //  Log.d("_xyz", "onDataChange: after match $clsId ${std.child("studentId").value.toString()}")
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
                    Log.d("_xyz", "onDataChange: ${_isStudentExists.value?.data.toString()}")


                }

                override fun onCancelled(error: DatabaseError) {
                    _isStudentExists.value = Resource.Error("Error")
                }

            })


        }

    }

    fun addChild(
        stdName: String,
        stdImage: String,
        stdId: String,
        parentId: String,
        parentName: String,
        parentImage: String,
        status: String
    ) {
        _createChild.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            val child = Child(
                parentId, parentImage, parentName, stdId, stdImage, stdName, status
            )

            dbChildRef.push().setValue(child).addOnCompleteListener {
                _createChild.value = Resource.Success(child)
            }.addOnFailureListener {
                _createChild.value = Resource.Error("Failed")
            }

        }


    }

    fun signOut() {
        Firebase.auth.signOut()


    }

    fun findUserType(uId: String) {
        _userType.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { user ->
                        if (user.child("uid").value.toString() == uId) {
                            val arr = arrayListOf<String?>()
                            arr.add(user.child("type").value.toString())
                            arr.add(user.child("name").value.toString())
                            arr.add(user.child("uphoto").value.toString())
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

    fun searchTeacher(teacherName: String) {
        Log.d("_e", "searchTeacher: $teacherName")
        _teacherList.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbUserRef.orderByChild("name").startAt(teacherName).addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { user ->
                       // Log.d("_xyz", "onDataChange: ${user.child("name").value.toString() }")
                        if (user.child("type").value.toString() == "teacher") {
                            val arr = arrayListOf<UserItemResponse>()
                            arr.add(
                                UserItemResponse(
                                    user.child("email").value.toString(),
                                    user.child("name").value.toString(),
                                    user.child("type").value.toString(),
                                    user.child("uid").value.toString(),
                                    user.child("uphoto").value.toString()
                                )
                            )
                            Log.d("_e", "onDataChange:ok  $arr")

                            _teacherList.value = Resource.Success(arr)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _teacherList.value = Resource.Error("error")
                }

            })
        }

    }

    fun fetchChild(parentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbChildRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val arr = arrayListOf<Child>()
                    snapshot.children.forEach {
                        if (it.child("parentId").value.toString() == parentId && it.child("status").value.toString() == "notAssigned") {
                            arr.add(
                                Child(
                                    it.child("parentId").value.toString(),
                                    it.child("parentPhoto").value.toString(),
                                    it.child("parentName").value.toString(),
                                    it.child("stdId").value.toString(),
                                    it.child("stdImage").value.toString(),
                                    it.child("stdName").value.toString(),
                                    it.child("status").value.toString(),

                                    )

                            )
                        }
                    }
                    _childList.value = Resource.Success(arr)
                }

                override fun onCancelled(error: DatabaseError) {
                    _childList.value = Resource.Error("Failed")
                }

            })

        }
    }


}

data class Parent(
    val uid: String, val name: String, val image: String,

    val childId: String, val childName: String, val childPhoto: String,

    val clsId: String, val clsName: String, val classPhoto: String
)

data class Child(
    val parentId: String,
    val parentPhoto: String,
    val parentName: String,
    val stdId: String,
    val stdImage: String,
    val stdName: String,
    val status: String
)
