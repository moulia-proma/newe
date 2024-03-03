package com.example.classwave.presentation.page.parent

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import java.time.LocalDateTime
import kotlin.math.log

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

    private val _stdClassList = MutableStateFlow<Resource<ArrayList<Request>>?>(null)
    var stdClassList = _stdClassList.asStateFlow()


    private var _userType =
        MutableStateFlow<Resource<ArrayList<String?>>>(Resource.Success(arrayListOf()))
    var userType = _userType.asStateFlow()

    private var _isClassExists =
        MutableStateFlow<Resource<Class>?>(null)
    var isClassExists = _isClassExists.asStateFlow()

    private var _isStudentExists = MutableStateFlow<Resource<Student>?>(null)
    var isStudentExists = _isStudentExists.asStateFlow()
    private var _isStudentExists1 = MutableStateFlow<Resource<UserItemResponse>?>(null)
    var isStudentExists1 = _isStudentExists1.asStateFlow()

    private val _childList = MutableStateFlow<Resource<ArrayList<UserItemResponse>>?>(null)
    var childList = _childList.asStateFlow()

    private val _teacherList = MutableStateFlow<Resource<ArrayList<UserItemResponse>>?>(null)
    var teacherList = _teacherList.asStateFlow()

    fun setNull() {
        _isClassExists = MutableStateFlow(null)
        isClassExists = _isClassExists.asStateFlow()
        _createChild = MutableStateFlow(null)
        createChild = _createChild.asStateFlow()
        _isStudentExists = MutableStateFlow(null)
        isStudentExists = _isStudentExists.asStateFlow()
        _isStudentExists1 = MutableStateFlow(null)
        isStudentExists1 = _isStudentExists1.asStateFlow()
    }
    fun setUtypeNull(){
        _userType = MutableStateFlow(Resource.Success(arrayListOf()))
        userType = _userType.asStateFlow()
    }

    fun isClassExists(clsId: String) {
        _isClassExists.value = Resource.Loading()
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
                                    cls.child("teacherName").value.toString()
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

   /* fun isAlreadyNotified(clsId: String,teacherId: String,stdId: String){
        viewModelScope.launch(Dispatchers.IO) {

            dbRequestedStudentRef.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var cnt =0
                    snapshot.children.forEach {req->
                        if(clsId==req.child("clsId").value.toString() && stdId == req.child("teacherId").value.toString() ){
                            cnt=1
                        }
                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        }




    }*/
   fun isStudentExists1(stdId: String) {

     /*  if(stdId=="") {
          _isStudentExists1.value = Resource.Error("Please enter a proper stdId , it can't be empty")

           return
       }*/
       Log.d("_x", "isStudentExists1: $stdId")
       //_isStudentExists1.value = Resource.Loading()
       viewModelScope.launch(Dispatchers.IO) {
           dbUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                   var cnt = 0

                   snapshot.children.forEach { user ->
                       if (stdId == user.child("uid").value.toString()) {
                           cnt = 1
                           _isStudentExists1.value = Resource.Success(
                               UserItemResponse(
                                   user.child("email").value.toString(),
                                   user.child("name").value.toString(),
                                   user.child("type").value.toString(),
                                   user.child("uid").value.toString(),
                                   user.child("uphoto").value.toString(),
                                   user.child("parentId").value.toString(),
                               )
                               )

                       }

                   }
                   if (cnt == 0) {
                       _isStudentExists1.value = Resource.Error("Please add child in your profile first.")
                   }


               }

               override fun onCancelled(error: DatabaseError) {
                   _isStudentExists1.value = Resource.Error("Something went wrong")
               }

           })


       }

   }
    fun isStudentExists(stdId: String, classId: String) {
        _isStudentExists.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbStdREf.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var cnt = 0

                    snapshot.children.forEach { std ->
                        if (stdId == std.child("studentId").value.toString() && (classId == "" || classId == std.child(
                                "classId"
                            ).value.toString())
                        ) {
                            cnt = 1
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

                        _isStudentExists.value = Resource.Error("Please add child in your profile first.")
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
        Log.d("_j", "addChild: fff")
        _createChild.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbChildRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        Log.d("_lm", "onDataChange: ${parentId} yy   ${stdName} ${stdImage}")
                        if (it.child("parentId").value.toString() == parentId && it.child("stdId").value.toString() == stdId) {
                            val k = it.key
                            dbChildRef.child(k.toString()).removeValue()
                        }
                    }

                    val child =
                        Child(parentId, parentImage, parentName, stdId, stdImage, stdName, status)



                    dbChildRef.push().setValue(child).addOnCompleteListener {
                        Log.d("_lm", "onDataChange: i am not saved")
                        addParent(child)
                        _createChild.value = Resource.Success(child)

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


    }

    fun addParent(value: Child) {
        _createChild.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbUserRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { user ->
                        Log.d("_lk", "onDataChange: ${value.stdId} ${value.parentId}")
                        if (user.child("uid").value.toString() == value.stdId && user.child("parent").value.toString() == "") {

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
                                Log.d("_lm", "onDataChange: i am saved")

                                _createChild.value = Resource.Success(value)

                                 fatchChildList(Firebase.auth.uid.toString())
                            }

                            // Log.d("_xyz", "onDataChange: okoko")
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    _createChild.value = Resource.Error("Error")
                }

            })


        }
        //fatchChildList(Firebase.auth.uid.toString())

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
                                    classRoom.child("teacherName").value.toString(),
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
                        Log.d("_hm", "onDataChange: ${it.child("parent").value.toString()} x ${parentId}")
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
                    Log.d("_hm", "onDataChange: $arr")
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
        selectedClass: ArrayList<String>,
        uPhoto: String,
        stdImage: String,
        name: String,
        stdName: String,
        selectedClassName: ArrayList<String>,
        selectedClassImage: ArrayList<String>,
    ) {
        var i = 0
        while (i < selectedClass.size) {
            val request = _userType.value.data?.get(2)?.let {
               // Log.d("_oo", "requestTeacher: ${stdImage} ${    _userType.value.data!![1].toString()} ")
                Request(
                    Firebase.auth.uid.toString(),
                    tcrId,
                    selectedClass[i],
                    stdId,
                    "pending",
                    uPhoto,
                    stdImage,
                    it.toString(),
                    name,
                    _userType.value.data!![1].toString(),
                    stdName,
                    "${LocalDateTime.now().month} ${LocalDateTime.now().hour}  ",
                    selectedClassName[i],
                    selectedClassImage[i]
                )
            }
            val clsId = selectedClass[i]

            viewModelScope.launch (Dispatchers.IO){
                dbRequestedStudentRef.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var cnt = 0
                        snapshot.children.forEach {req->
                            Log.d("_ma", "onDataChange: ${req.child("parentId").value.toString()}  x ${req.child("stdId").value.toString()}  v ${stdId} x ${req.child("clsId").value.toString()} v  ")
                            if(req.child("parentId").value.toString() == Firebase.auth.uid.toString() && req.child("stdId").value.toString() == stdId && req.child("clsId").value.toString()== clsId){
                                cnt =1
                            }
                        }
                        Log.d("_ma", "onDataChange: ${cnt}")
                        if(cnt == 0) {
                            dbRequestedStudentRef.push().setValue(request).addOnCompleteListener {
                                val child = _userType.value.data?.get(1)?.let { it1 ->
                                    Child(
                                        Firebase.auth.uid.toString(),
                                        it1.toString(),
                                        _userType.value.data!![2].toString(),
                                        stdId,
                                        stdImage,
                                        stdName,
                                        "pending"
                                    )
                                }
                                _userType.value.data?.get(1)?.let { it1 ->
                                    addChild(
                                        stdName,
                                        stdImage,
                                        stdId,
                                        Firebase.auth.uid.toString(),
                                        it1.toString(),
                                        _userType.value.data!![2].toString(),
                                        "pending"
                                    )
                                }
                            }
                        } else{

                                _createChild.value= Resource.Error("Sorry,You've already requested for this class")
                            }
                        }








                    override fun onCancelled(error: DatabaseError) {

                    }

                })


            }





            i++
        }
    }


     fun fetchStdClassList(stdId: String,parentId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbRequestedStudentRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val arr = arrayListOf<Request>()
                    snapshot.children.forEach { request ->
                        if (request.child("stdId").value.toString() == stdId && request.child("parentId").value.toString() == parentId ) {
                            arr.add(
                                Request(
                                    request.child("parentId").value.toString(),
                                    request.child("teacherId").value.toString(),
                                    request.child("clsId").value.toString(),
                                    request.child("stdId").value.toString(),
                                    request.child("state").value.toString(),
                                    request.child("uPhoto").value.toString(),
                                    request.child("stdImage").value.toString(),
                                    request.child("parentPhoto").value.toString(),
                                    request.child("name").value.toString(),
                                    request.child("parentName").value.toString(),
                                    request.child("stdName").value.toString(),
                                    request.child("time").value.toString(),
                                    request.child("clsName").value.toString(),
                                    request.child("clsImage").value.toString(),
                                )
                            )
                        }
                    }
                    _stdClassList.value = Resource.Success(arr)
                }

                override fun onCancelled(error: DatabaseError) {
                    _stdClassList.value = null
                }
            })
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
    val time: String,
    val clsName:String,
    val clsImage:String

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
