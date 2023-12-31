package com.example.classwave.presentation.page.Attandance

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.report.Report
import com.example.classwave.presentation.page.teacher.Marks
import com.example.classwave.presentation.page.teacher.Student
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class AttendanceViewModel @Inject constructor() : ViewModel() {
    private var dbMarksRef = FirebaseDatabase.getInstance().getReference("Marks")
    private val _reportList = MutableStateFlow<Resource<MutableMap<String, MutableList<Report>>>>(
        Resource.Success(mutableMapOf())
    )
    var reportList: StateFlow<Resource<MutableMap<String, MutableList<Report>>>> =
        _reportList.asStateFlow()

    private val _studentList = MutableStateFlow<Resource<List<Student>>?>(null)
    var studentList = _studentList.asStateFlow()
    private var dbStdRef = FirebaseDatabase.getInstance().getReference("Students")
    private val _addMarks = MutableStateFlow<Resource<Marks>?>(null)
    val addMarks = _addMarks.asStateFlow()

    private var _attendance = MutableStateFlow<MutableMap<String,Boolean>>(mutableMapOf())
    val attendance = _attendance.asStateFlow()
    fun fetchAttendance() {
        viewModelScope.launch(Dispatchers.IO) {
            dbMarksRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                     var attendance = mutableMapOf<String,Boolean>()

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onDataChange(dataSnapshot: DataSnapshot) {


                        dataSnapshot.children.filter {
                            it.child("date").value == LocalDate.now()
                                .toString() && it.child("skillId").value == "attendance123"
                        }.forEach { mark ->
                            attendance[mark.child("stdId").value.toString()] =
                                mark.child("marks").value.toString().toBoolean()


                        }
                        Log.d("pk", "onDataChange: $attendance")

                        _attendance.value = attendance
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _reportList.value = Resource.Error("Data Retrieve unsuccessful")
                    }
                })
        }
    }
    fun fetchStudentByClassId(classId: String) {
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
                                        student.child("studentName").value.toString(),
                                        student.child("img").value.toString(),
                                    )
                                )

                            }

                        }
                        _studentList.value = Resource.Success(studentList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
        }


    }
    fun addMarks(
        stdId: String,
        skillId: String,
        score: String,
        name: String,
        img: String,
        highestScore: String,
        clsId: String
    ) {
        val st = (skillId + "_" + stdId)
        Log.d("tag", "addMarks: ${st}")


        viewModelScope.launch(Dispatchers.IO) {
            dbMarksRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        dataSnapshot.children.forEach { mark ->
                            Log.d(
                                "xyz",
                                "onDataChange: ${mark.child("stdId").value.toString()}  ${stdId}"
                            )
                            Log.d("man", "${mark.child("skillIdStdId").value.toString()} ${st}")
                            if (mark.child("skillIdStdId").value.toString() == st) {
                                val k = mark.key
                                FirebaseDatabase.getInstance().getReference("Marks")
                                    .child(k.toString()).removeValue()
                            }

                        }
                        _addMarks.value = Resource.Loading()

                        val currentDate = LocalDate.now()
                        val mark = Marks(
                            clsId,
                            skillId,
                            stdId,
                            score,
                            currentDate.toString(),
                            st,
                            name,
                            img,
                            highestScore
                        )

                        dbMarksRef.push().setValue(mark)
                            .addOnCompleteListener {
                                _addMarks.value = Resource.Success(mark)
                            }
                            .addOnFailureListener {
                               _addMarks.value = Resource.Error("Skill creation Failed")
                                Log.d("TAG", "createSkill: skill created")
                            }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _reportList.value = Resource.Error("Data Retrieve unsuccessful")
                    }
                })
        }

    }

    /*fun defaultAttendance(classId: String) {
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
                                        student.child("studentName").value.toString(),
                                        student.child("img").value.toString(),
                                    )
                                )

                            }

                        }
                        _studentList.value = Resource.Success(studentList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _studentList.value = Resource.Error("Data Retrieve unsuccessful")
                    }
                })
        }


    }*/

    private fun createRegex(year: Int?, month: Int?, day: Int?): Regex {
        val yearRegex = year?.toString() ?: "\\d{4}"
        val monthRegex = if (month != null) {
            String.format("%02d", month)
        } else {
            "\\d{2}"
        }
        val dayRegex = if (day != null) {
            String.format("%02d", day)
        } else {
            "\\d{2}"
        }
        val regexPattern = "^$yearRegex-$monthRegex-$dayRegex.*"
        return Regex(regexPattern)
    }

}