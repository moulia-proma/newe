package com.example.classwave.presentation.page.teacher

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classwave.domain.model.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TeacherViewModel @Inject constructor() : ViewModel() {
    private var dbRef = FirebaseDatabase.getInstance().getReference("ClassRoom")
    private var dbStdRef = FirebaseDatabase.getInstance().getReference("Students")
    private var dbSkillRef = FirebaseDatabase.getInstance().getReference("Skills")
    private var dbMarksRef = FirebaseDatabase.getInstance().getReference("Marks")
    private val uid = Firebase.auth.currentUser?.uid

    private val _classList = MutableStateFlow<Resource<List<Class>>?>(null)
    var classList = _classList.asStateFlow()

    private val _studentList = MutableStateFlow<Resource<List<Student>>?>(null)
    var studentList = _studentList.asStateFlow()

    private val _skillList = MutableStateFlow<Resource<List<Skill>>?>(null)
    var skillList = _skillList.asStateFlow()

    private val _createClass = MutableStateFlow<Resource<Class>?>(null)
    val createClass = _createClass.asStateFlow()

    private val _createStudent = MutableStateFlow<Resource<Student>?>(null)
    val createStudent = _createStudent.asStateFlow()

    private val _createSkill = MutableStateFlow<Resource<Skill>?>(null)
    val createSkill = _createSkill.asStateFlow()

    private val _selectedClass = MutableStateFlow<Class?>(null)
    val selectedClass = _selectedClass.asStateFlow()

    private val _addMarks = MutableStateFlow<Resource<Marks>?>(null)
    val addMarks = _addMarks.asStateFlow()

    init {
        fetchClassList()
    }

    fun updateClass(cls: Class) {
        _selectedClass.value = cls
        fetchStudentByClassId(classId = cls.classId)
        fetchSkillByClassId(classId = cls.classId)
    }

    private fun fetchClassList() {
        viewModelScope.launch(Dispatchers.IO) {
            dbRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val classList = arrayListOf<Class>()
                        dataSnapshot.children.forEach { classRoom ->
                            classList.add(
                                Class(
                                    classRoom.child("classId").value.toString(),
                                    classRoom.child("grade").value.toString(),
                                    classRoom.child("name").value.toString(),
                                    classRoom.child("teacherId").value.toString(),
                                )

                            )
                        }
                        _classList.value = Resource.Success(classList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _createClass.value = Resource.Error("Data Retrieve unsuccessful")
                    }
                })
        }
    }

    private fun fetchStudentByClassId(classId: String) {
        _studentList.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbStdRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.d("TAG", "onDataChange dataSnapshot: ${dataSnapshot}")
                        val studentList = arrayListOf<Student>()
                        dataSnapshot.children.forEach { student ->

                            if (student.child("classId").value.toString() == classId) {
                                Log.d(
                                    "TAG",
                                    "onDataChange name: ${student.child("StudentName").value.toString()}"
                                )
                                studentList.add(
                                    Student(
                                        student.child("classId").value.toString(),
                                        student.child("studentId").value.toString(),
                                        student.child("studentName").value.toString()
                                    )
                                )

                            }

                        }
                        _studentList.value = Resource.Success(studentList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _createClass.value = Resource.Error("Data Retrieve unsuccessful")
                    }
                })
        }


    }

    private fun fetchSkillByClassId(classId: String) {
        Log.d(
            "TAG",
            "onDataChange name:rrrrrr "
        )
        _skillList.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbSkillRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.d("TAG", "onDataChange dataSnapshot: ${dataSnapshot}")
                        val skillList = arrayListOf<Skill>()
                        dataSnapshot.children.forEach { skill ->

                            if (skill.child("classId").value.toString() == classId) {

                                skillList.add(
                                    Skill(
                                        skill.child("classId").value.toString(),
                                        skill.child("skillId").value.toString(),
                                        skill.child("name").value.toString(),
                                        skill.child("highestScore").value.toString(),
                                    )
                                )

                            }

                        }
                        _skillList.value = Resource.Success(skillList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _skillList.value = Resource.Error("Data Retrieve unsuccessful")
                    }
                })
        }


    }

    fun createClass(name: String, grade: String) {
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


    fun createStudent(clsId: String, name: String) {
        _createStudent.value = Resource.Loading()
        if (name.isEmpty()) {
            _createStudent.value = Resource.Error("Class name can't be empty")
            return
        }

        val stdId = dbStdRef.push().key
        val student = Student(clsId ?: "", stdId ?: "", name)

        dbStdRef.push().setValue(student)
            .addOnCompleteListener {
                _createStudent.value = Resource.Success(student)
            }
            .addOnFailureListener {
                _createStudent.value = Resource.Error("User creation Failed")
                Log.d("TAG", "createClass: cls created")
            }
        fetchStudentByClassId(clsId)
    }

    fun createSkill(clsId: String, name: String, hScore: String) {
        Log.d("TAG", "createSkill: Created")
        _createSkill.value = Resource.Loading()
        if (name.isEmpty()) {
            _createSkill.value = Resource.Error("add name of the skill")
            return
        }

        val skillId = dbSkillRef.push().key
        val skill = Skill(clsId ?: "", skillId ?: "", name, hScore)

        dbSkillRef.push().setValue(skill)
            .addOnCompleteListener {
                _createSkill.value = Resource.Success(skill)
            }
            .addOnFailureListener {
                _createSkill.value = Resource.Error("Skill creation Failed")
                Log.d("TAG", "createSkill: skill created")
            }
        fetchSkillByClassId(clsId)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addMarks(clsId: String, stdId: String, skillId: String, score: String) {

        dbSkillRef.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var hScore: String
                    dataSnapshot.children.forEach { skill ->
                        Log.d(
                            "TAGG",
                            "onDataChange: ${skill.child("clsId").value.toString()}  ${skill.child("skillId").value.toString()}"
                        )
                        if (skill.child("classId").value.toString() == clsId && skill.child("skillId").value.toString() == skillId) {
                            hScore = skill.child("highestScore").value.toString()
                            if (hScore.toInt() < score.toInt()) {
                                _addMarks.value =
                                    Resource.Error("Marks should be greater than ${hScore.toInt()}")
                                return
                            }

                            _addMarks.value = Resource.Loading()
                            val crndDate = LocalDate.now()
                            val mark = Marks(skillId, stdId, score, crndDate.toString())
                            dbMarksRef.push().setValue(mark)
                                .addOnCompleteListener {
                                    _addMarks.value = Resource.Success(mark)
                                }
                                .addOnFailureListener {
                                    _createSkill.value = Resource.Error("Skill creation Failed")
                                    Log.d("TAG", "createSkill: skill created")
                                }


                        }
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        /*  val hScore = getMaxMark(clsId, skillId)
          Log.d("TAB", "addMarks: ${hScore} ${score} ")
          if (score.toInt() < hScore.toInt()) {
              _addMarks.value = Resource.Error(
                  "score should be greated than $hScore"
              )
              return
          }*/


    }

    fun getMaxMark(clsId: String, skillId: String) {

        viewModelScope.launch(Dispatchers.IO) {
            dbSkillRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var hScore: String
                        dataSnapshot.children.forEach { skill ->
                            Log.d(
                                "TAGG",
                                "onDataChange: ${skill.child("clsId").value.toString()}  ${
                                    skill.child("skillId").value.toString()
                                }"
                            )
                            if (skill.child("classId").value.toString() == clsId && skill.child("skillId").value.toString() == skillId) {
                                hScore = skill.child("highestScore").value.toString()
                                Log.d("TAGG", "onDataChange: {$hScore}")

                            }
                        }


                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
        }

    }

}


data class Class(
    var classId: String,
    var teacherId: String,
    var name: String,
    var grade: String
)

data class Skill(
    var classId: String,
    var skillId: String,
    var name: String,
    var highestScore: String
)

data class Marks(
    var skillId: String,
    var stdId: String,
    var marks: String,
    var date: String
)
