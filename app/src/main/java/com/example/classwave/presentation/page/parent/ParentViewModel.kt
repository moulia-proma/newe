package com.example.classwave.presentation.page.parent

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classwave.data.datasource.remote.model.UserItemResponse
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.teacher.Class
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
import java.time.LocalDateTime

class ParentViewModel : ViewModel() {
    private var dbChildRef = FirebaseDatabase.getInstance().getReference("children")
    private var dbRequestedStudentRef =
        FirebaseDatabase.getInstance().getReference("requestedStudent")
    private var dbUserRef = FirebaseDatabase.getInstance().getReference("Users")
    private var dbClassREf = FirebaseDatabase.getInstance().getReference("ClassRoom")
    private var dbStdREf = FirebaseDatabase.getInstance().getReference("Students")

    private var _createChild = MutableStateFlow<Resource<Child>?>(null)
    var createChild = _createChild.asStateFlow()

    private val _classList = MutableStateFlow<Resource<ArrayList<Class>>?>(null)
    var classList = _classList.asStateFlow()


    private var _userType =
        MutableStateFlow<Resource<ArrayList<String?>>>(Resource.Success(arrayListOf()))
    var userType = _userType.asStateFlow()

    private var _isClassExists =
        MutableStateFlow<Resource<Class>?>(null)
    var isClassExists = _isClassExists.asStateFlow()

    private var _isStudentExists = MutableStateFlow<Resource<UserItemResponse>?>(null)
    var isStudentExists = _isStudentExists.asStateFlow()

    private val _childList = MutableStateFlow<Resource<ArrayList<UserItemResponse>>?>(null)
    var childList = _childList.asStateFlow()

    private val _teacherList = MutableStateFlow<Resource<ArrayList<UserItemResponse>>?>(null)
    var teacherList = _teacherList.asStateFlow()

    fun setNull() {
        _isClassExists =
            MutableStateFlow(null)
        isClassExists = _isClassExists.asStateFlow()
        _createChild = MutableStateFlow(null)
        createChild = _createChild.asStateFlow()
    }

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
            dbUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var cnt = 0

                    snapshot.children.forEach { std ->
                        Log.d("_xyz", "onDataChange: $clsId ${std.child("uid").value.toString()}")
                        if (clsId == std.child("uid").value.toString()) {
                            Log.d(
                                "_xyz",
                                "onDataChange: okokok $clsId ${std.child("uid").value.toString()}"
                            )
                            cnt = 1

                            _isStudentExists.value = Resource.Success(
                                UserItemResponse(
                                    std.child("email").value.toString(),
                                    std.child("name").value.toString(),
                                    std.child("type").value.toString(),
                                    std.child("uid").value.toString(),
                                    std.child("uphoto").value.toString(),
                                    std.child("parent").value.toString(),
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

    fun addChild(
        stdName: String,
        stdImage: String,
        stdId: String,
        parentId: String,
        parentName: String,
        parentImage: String,
        status: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dbChildRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        Log.d(
                            "_xyz",
                            "onDataChange: ${parentId} ${stdId}    ${it.child("parentId").value.toString()} ${
                                it.child("stdId").value.toString()
                            }"
                        )
                        if (it.child("parentId").value.toString() == parentId && it.child("stdId").value.toString() == stdId) {
                            val k = it.key
                            dbChildRef.child(k.toString()).removeValue()
                        }
                    }
                    _createChild.value = Resource.Loading()
                    val child =
                        Child(parentId, parentImage, parentName, stdId, stdImage, stdName, status)



                    dbChildRef.push().setValue(child).addOnCompleteListener {

                        addParent(child)
                       // fatchChildList(Firebase.auth.uid.toString())
                    }.addOnFailureListener {
                        _createChild.value = Resource.Error("Failed")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _childList.value = Resource.Error("Failed")
                }

            })
        }

        //fatchChildList(Firebase.auth.uid.toString())
    }

    fun addParent(value: Child) {
        viewModelScope.launch(Dispatchers.IO) {
            dbUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { user ->
                        if (user.child("uid").value.toString() == value.stdId) {
                            val u = UserItemResponse(
                                user.child("email").value.toString(),
                                user.child("name").value.toString(),
                                user.child("type").value.toString(),
                                user.child("uid").value.toString(),
                                user.child("uphoto").value.toString(),
                                value.parentId
                            )

                            val k = user.key

                            dbUserRef.child(k.toString()).removeValue()

                            dbUserRef.push().setValue(
                                u
                            ).addOnCompleteListener {
                                _createChild.value = Resource.Success(value)
                               // fatchChildList(Firebase.auth.uid.toString())
                            }

                         // Log.d("_xyz", "onDataChange: okoko")
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    _isClassExists.value = Resource.Error("Error")
                }

            })


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

    }

    fun searchTeacher(teacherName: String) {
        _teacherList.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbUserRef.orderByChild("name").startAt(teacherName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var flag = 0
                        snapshot.children.forEach { user ->

                            val dbString = user.child("name").value.toString()

                            if (user.child("type").value.toString() == "teacher" && dbString.contains(
                                    teacherName
                                )
                            ) {
                                flag = 1
                                val arr = arrayListOf<UserItemResponse>()
                                arr.add(
                                    UserItemResponse(
                                        user.child("email").value.toString(),
                                        user.child("name").value.toString(),
                                        user.child("type").value.toString(),
                                        user.child("uid").value.toString(),
                                        user.child("uphoto").value.toString(),
                                        user.child("parent").value.toString(),

                                        )
                                )

                                _teacherList.value = Resource.Success(arr)
                            }
                        }
                        if (flag == 0) {
                            _teacherList.value = Resource.Success(arrayListOf())
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _teacherList.value = Resource.Error("error")
                    }

                })
        }

    }


    fun fetchClassList(tcrId: String) {
        _classList.value = Resource.Loading()
        val uid = Firebase.auth.currentUser?.uid
        viewModelScope.launch(Dispatchers.IO) {
            dbClassREf.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val classList = arrayListOf<Class>()
                    dataSnapshot.children.forEach { classRoom ->
                        if (classRoom.child("teacherId").value.toString() == tcrId) {
                            classList.add(
                                Class(
                                    classRoom.child("classId").value.toString(),
                                    classRoom.child("teacherId").value.toString(),
                                    classRoom.child("name").value.toString(),
                                    classRoom.child("grade").value.toString(),
                                    classRoom.child("img").value.toString(),


                                    )
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

    fun fatchChildList(parentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val arr = arrayListOf<UserItemResponse>()
                    snapshot.children.forEach {
                        //   Log.d("_xyz", "onDataChange:e $parentId ${it.child("parent").value.toString()}  ${it.child("parentId").value.toString() == parentId}")
                        if (it.child("parent").value.toString() == parentId) {
                            arr.add(
                                UserItemResponse(
                                    it.child("email").value.toString(),
                                    it.child("name").value.toString(),
                                    it.child("type").value.toString(),
                                    it.child("uid").value.toString(),
                                    it.child("uphoto").value.toString(),
                                    it.child("parent").value.toString(),
                                )

                            )
                        }
                    }
                    Log.d("_xyz", "onDataChange: $arr")
                    _childList.value = Resource.Success(arr)
                }

                override fun onCancelled(error: DatabaseError) {
                    _childList.value = Resource.Error("Failed")
                }

            })
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun requestTeacher(
        tcrId: String,
        stdId: String,
        parentId: String,
        selectedClass: ArrayList<String>,
        uPhoto: String,
        stdImage: String,
        parentPhoto: String,
        name: String,
        parentName: String,
        stdName: String
    ) {
        var i = 0
        while (i < selectedClass.size) {
            val request = Request(
                parentId,
                tcrId,
                selectedClass[i],
                stdId,
                "pending",
                uPhoto,
                stdImage,
                parentPhoto,
                name,
                parentName,
                stdName,
                "${LocalDateTime.now().month} ${LocalDateTime.now().hour}  "
            )
            dbRequestedStudentRef.push().setValue(request).addOnCompleteListener {
                val child = Child(
                    parentId, parentPhoto, parentName, stdId, stdImage, stdName, "pending"
                )
                addChild(
                    stdName, stdImage, stdId, parentId, parentName, parentPhoto, "pending"
                )
            }
            i++
        }
    }

}

data class Request(
    val parentId: String,
    val teacherId: String,
    val clsId: String,
    val stdId: String,
    val state: String,
    val uPhoto: String,
    val stdImage: String,
    val parentPhoto: String,
    val name: String,
    val parentName: String,
    val stdName: String,
    val time: String

)

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
