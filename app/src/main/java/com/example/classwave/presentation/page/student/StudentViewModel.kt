package com.example.classwave.presentation.page.student

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classwave.R
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.teacher.Class
import com.example.classwave.presentation.page.teacher.Marks
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
import java.time.LocalDate

class StudentViewModel : ViewModel() {

    private var dbClassREf = FirebaseDatabase.getInstance().getReference("ClassRoom")
    private var dbStdRef = FirebaseDatabase.getInstance().getReference("Students")
    private var dbMarksRef = FirebaseDatabase.getInstance().getReference("Marks")
    private var dbUserRef = FirebaseDatabase.getInstance().getReference("Users")

    private var _createStudent = MutableStateFlow<Resource<Student>?>(null)
    var createStudent = _createStudent.asStateFlow()

    private val _classList = MutableStateFlow<Resource<List<String>>?>(null)
    var classList = _classList.asStateFlow()

    private val _newClassList = MutableStateFlow<Resource<List<Class>>?>(null)
    var newClassList = _newClassList.asStateFlow()

    private val _selectedClass = MutableStateFlow<Class?>(null)
    val selectedClass = _selectedClass.asStateFlow()

    private var _userType = MutableStateFlow<ArrayList<String>>(arrayListOf())
    var userType = _userType.asStateFlow()

    private val stdImage = arrayListOf<Int>(
        R.drawable.st_boy,
        R.drawable.st_bussiness_man,
        R.drawable.st_profile,
        R.drawable.st_profile_boy,
        R.drawable.st_profile_man,
        R.drawable.st_user
    )

    fun setNull(){
        _createStudent = MutableStateFlow<Resource<Student>?>(null)
        createStudent = _createStudent.asStateFlow()
    }

    fun isClassExists(clsId: String, uName: String) {

        _createStudent.value = Resource.Loading()
        if (clsId.isEmpty()) {
            _createStudent.value = Resource.Error("Class code can't be empty")
            return
        }
        var isExist =0

        viewModelScope.launch(Dispatchers.IO) {
            dbClassREf.addListenerForSingleValueEvent(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { cls ->
                        if (clsId == cls.child("classId").value.toString()) {
                            isExist =1

                            createStudent(clsId, uName)
                        }
                    }
                    if(isExist == 0){
                        _createStudent.value = Resource.Error("No class found with this code,Try again!")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _createStudent.value = Resource.Error("Something is wrong,Try again!")
                }
            })

        }
    }

    fun updateClass(cls: Class?) {
        _selectedClass.value = cls

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createStudent(clsId: String, name: String) {

        val stdId = Firebase.auth.uid.toString()
        var profile = stdImage.random().toString()
        val student = Student(clsId ?: "", stdId ?: "", name, profile)

        val st = ("attendance123" + "_" + stdId)

        val stdAttendance = stdId?.let {
            Marks(
                clsId,
                "attendance123",
                stdId,
                "0",
                LocalDate.now().toString(),
                st,
                name,
                "",
                "1",
                name,
                profile

            )
        }
        dbStdRef.push().setValue(student).addOnCompleteListener {
            _createStudent.value = Resource.Success(student)
            dbMarksRef.push().setValue(stdAttendance)
        }.addOnFailureListener {
            _createStudent.value = Resource.Error("Something is wrong")

        }

        fetchClassList(stdId)
    }

    fun fetchClassList(stdId: String) {
        _classList.value = Resource.Loading()
        val uid = Firebase.auth.currentUser?.uid
        viewModelScope.launch(Dispatchers.IO) {
            dbStdRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val classList = arrayListOf<String>()
                    dataSnapshot.children.forEach { classRoom ->
                        if (classRoom.child("studentId").value.toString() == stdId) {
                            classList.add(
                                classRoom.child("classId").value.toString()
                            )
                        }

                    }

                    _classList.value = Resource.Success(classList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    _classList.value = Resource.Error("Data Retrieve unsuccessful")
                }
            })
        }
    }

    fun findUserType(uId: String) {

        viewModelScope.launch(Dispatchers.IO) {
            dbUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { user ->
                        if (user.child("uid").value.toString() == uId) {
                            val arr = arrayListOf<String>()
                            arr.add(user.child("type").value.toString())
                            arr.add(user.child("name").value.toString())
                            arr.add(user.child("email").value.toString())
                            _userType.value = arr
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }

    fun showClasses(data: List<String>?) {
        viewModelScope.launch(Dispatchers.IO) {
            dbClassREf.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val arr = mutableListOf<Class>()
                    snapshot.children.forEach { cls ->
                        data?.forEach { std ->

                            if (std == cls.child("classId").value.toString()) {
                                Log.d("_pro", "onDataChange: milce")
                                arr.add(
                                    Class(
                                        cls.child("classId").value.toString(),
                                        cls.child("teacherId").value.toString(),
                                        cls.child("name").value.toString(),
                                        cls.child("grade").value.toString(),
                                        cls.child("img").value.toString(),
                                        cls.child("teacherName").value.toString(),
                                    )
                                )
                            }

                        }
                    }
                    _newClassList.value = Resource.Success(arr)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        }


    }

    fun updateStudentName() {
        viewModelScope.launch(Dispatchers.IO) {
            dbUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
    }

    fun signOut() {
        Firebase.auth.signOut()


    }


}