package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classwave.R
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

    private val _markList = MutableStateFlow<Resource<List<Marks>>?>(null)
    var markList = _markList.asStateFlow()

    private val _studentList = MutableStateFlow<Resource<List<Student>>?>(null)
    var studentList = _studentList.asStateFlow()

    private val _posSkillList = MutableStateFlow<Resource<List<Skill>>?>(null)
    var posSkillList = _posSkillList.asStateFlow()

    private val _negSkillList = MutableStateFlow<Resource<List<Skill>>?>(null)
    var negSkillList = _negSkillList.asStateFlow()

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

    private val clsImage = arrayListOf<Int>(
        R.drawable.cls_chemistry,
        R.drawable.cls_alculating,
        R.drawable.cls_blackboard,
        R.drawable.cls_calculator,
        R.drawable.cls_maths,
        R.drawable.cls_computer_science,
        R.drawable.cls_mathematics_symbol,
        R.drawable.cls_online_learning,
        R.drawable.cls_teaching
    )
    private val stdImage = arrayListOf<Int>(
        R.drawable.st_boy,
        R.drawable.st_bussiness_man,
        R.drawable.st_profile,
        R.drawable.st_profile_boy,
        R.drawable.st_profile_man,
        R.drawable.st_user
    )
    private val skillImage = arrayListOf<Int>(
        R.drawable.skill_competence,
        R.drawable.skill_competition,
        R.drawable.skill_ideas,
        R.drawable.skill_question,
        R.drawable.skill_competition1,
        R.drawable.skill_quiz,
        R.drawable.skill_quiz1,
        R.drawable.skill_quiz2,
        R.drawable.skill_test,
        R.drawable.skill_requirements
    )

    init {
        fetchClassList()
    }

    fun updateClass(cls: Class) {
        _selectedClass.value = cls
        fetchStudentByClassId(classId = cls.classId)
        fetchSkillByClassId(classId = cls.classId)
    }

    fun getMarksDropdownList(highestScore: String, type: String): ArrayList<Int> {
        if (type == "pos") {
            val grade = arrayListOf<Int>()
            var i = 1
            while (i <= highestScore.toInt()) {
                grade.add(i)
                i++
            }
            return grade
        } else {
            val grade = arrayListOf<Int>()
            var i = highestScore.toInt()
            while (i <= 0) {
                grade.add(i)
                i++
            }
            return grade
        }

    }

    @SuppressLint("SuspiciousIndentation")
    fun getTotalPosMarks(marks: List<Marks>):Int{

        var sum =0
        var i =0
        while(i<marks.size){
            if(marks[i].highestScore.toInt()>0)
            sum += marks[i].highestScore.toInt()
           i++
        }
        return sum

    }
    @SuppressLint("SuspiciousIndentation")
    fun getTotalNegMarks(marks: List<Marks>):Int{

        var sum =0
        var i =0
        while(i<marks.size){
            if(marks[i].highestScore.toInt()<=0){
                Log.d("sum", "getTotalNegMarks: ${marks[i].highestScore.toInt()}")
                sum += kotlin.math.abs(marks[i].highestScore.toInt())
            }

            i++
        }
        Log.d("TAG", "getTotalNegMarks: ${sum}")
        return sum

    }
    fun getTotalAchivedNegMarks(marks: List<Marks>):Int{

        var sum =0
        var i =0
        while(i<marks.size){
            if(marks[i].marks.toInt()<=0)
                sum += kotlin.math.abs(marks[i].marks.toInt())
            i++
        }
        return sum

    }
    fun getTotalAchivedPosMarks(marks: List<Marks>):Int{

        var sum =0
        var i =0
        while(i<marks.size){
            if(marks[i].marks.toInt()>0)
                sum += marks[i].marks.toInt()
            i++
        }
        return sum

    }
    fun getProgress(totalNeg: Int?, totalPos: Int?, negAchived: Int?, posAchived: Int?):Int {


        if (posAchived != null) {
            if(posAchived>0){
                return ((posAchived / (totalPos!!*1.0)*100)).toInt()
            }
            }
            return 0


    }

    private fun fetchClassList() {
        val uid = Firebase.auth.currentUser?.uid
        viewModelScope.launch(Dispatchers.IO) {
            dbRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val classList = arrayListOf<Class>()
                        dataSnapshot.children.forEach { classRoom ->
                            if (classRoom.child("teacherId").value.toString() == uid.toString()) {
                                classList.add(
                                    Class(
                                        classRoom.child("classId").value.toString(),
                                        classRoom.child("grade").value.toString(),
                                        classRoom.child("name").value.toString(),
                                        classRoom.child("teacherId").value.toString(),
                                        classRoom.child("img").value.toString()

                                    )
                                )
                            }

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
                                        student.child("studentName").value.toString(),
                                        student.child("img").value.toString(),
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
        _posSkillList.value = Resource.Loading()
        _negSkillList.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbSkillRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        Log.d("TAG", "onDataChange dataSnapshot: ${dataSnapshot}")
                        val posSkillList = arrayListOf<Skill>()
                        val negSkillList = arrayListOf<Skill>()
                        dataSnapshot.children.forEach { skill ->
                            if (skill.child("classId").value.toString() == classId) {
                                if (skill.child("type").value.toString() == "pos") {
                                    posSkillList.add(
                                        Skill(
                                            skill.child("classId").value.toString(),
                                            skill.child("skillId").value.toString(),
                                            skill.child("name").value.toString(),
                                            skill.child("highestScore").value.toString(),
                                            skill.child("img").value.toString(),
                                            skill.child("type").value.toString()
                                        )
                                    )

                                } else {
                                    negSkillList.add(
                                        Skill(
                                            skill.child("classId").value.toString(),
                                            skill.child("skillId").value.toString(),
                                            skill.child("name").value.toString(),
                                            skill.child("highestScore").value.toString(),
                                            skill.child("img").value.toString(),
                                            skill.child("type").value.toString()
                                        )
                                    )


                                }


                            }

                        }

                        _posSkillList.value = Resource.Success(posSkillList)

                        _negSkillList.value = Resource.Success(negSkillList)


                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _posSkillList.value = Resource.Error("Data Retrieve unsuccessful")
                        _negSkillList.value = Resource.Error("Data Retrieve unsuccessful")
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
        val clsRoom = Class(clsId ?: "", uid ?: "", name, grade, clsImage.random().toString())

        dbRef.push().setValue(clsRoom)
            .addOnCompleteListener {
                _createClass.value = Resource.Success(clsRoom)
            }
            .addOnFailureListener {
                _createClass.value = Resource.Error("User creation Failed")
                Log.d("TAG", "createClass: cls created")
            }

        fetchClassList()

    }


    fun createStudent(clsId: String, name: String) {
        _createStudent.value = Resource.Loading()
        if (name.isEmpty()) {
            _createStudent.value = Resource.Error("Class name can't be empty")
            return
        }

        val stdId = dbStdRef.push().key
        val student = Student(clsId ?: "", stdId ?: "", name, stdImage.random().toString())

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

    fun createSkill(clsId: String, name: String, hScore: String, typeofSkill: String) {
        Log.d("TAG", "createSkill: Created")
        _createSkill.value = Resource.Loading()
        if (name.isEmpty()) {
            _createSkill.value = Resource.Error("add name of the skill")
            return
        }

        val skillId = dbSkillRef.push().key
        val skill = Skill(
            clsId ?: "",
            skillId ?: "",
            name,
            hScore,
            skillImage.random().toString(),
            typeofSkill
        )

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
    fun addMarks(
        stdId: String,
        skillId: String,
        score: String,
        name: String,
        img: String,
        highestScore: String
    ) {
        val st = (skillId+"_"+stdId)
        Log.d("tag", "addMarks: ${st}")


        viewModelScope.launch(Dispatchers.IO) {
            dbMarksRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        dataSnapshot.children.forEach { mark ->
                            Log.d("xyz", "onDataChange: ${mark.child("stdId").value.toString()}  ${stdId}")
                            Log.d("man", "${mark.child("skillIdStdId").value.toString() } ${st}")
                            if (mark.child("skillIdStdId").value.toString() == st) {
                                val k = mark.key
                                 FirebaseDatabase.getInstance().getReference("Marks").child(k.toString()).removeValue()
                            }

                        }
                        _addMarks.value = Resource.Loading()

                        val currentDate = LocalDate.now()
                        val mark = Marks(skillId, stdId, score, currentDate.toString(), st,name,img,highestScore)

                        dbMarksRef.push().setValue(mark)
                            .addOnCompleteListener {
                                _addMarks.value = Resource.Success(mark)
                            }
                            .addOnFailureListener {
                                _createSkill.value = Resource.Error("Skill creation Failed")
                                Log.d("TAG", "createSkill: skill created")
                            }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _markList.value = Resource.Error("Data Retrieve unsuccessful")
                    }
                })
        }

    }
    fun fetchStudentReport(stdId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbMarksRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val markList = arrayListOf<Marks>()
                        dataSnapshot.children.forEach { mark ->
                            Log.d("xyz", "onDataChange: ${mark.child("stdId").value.toString()}  ${stdId}")
                            Log.d("TAG", "onDataChange:${mark.child("stdId").value.toString()}  ${stdId}")
                            if (mark.child("stdId").value.toString() == stdId) {
                                markList.add(
                                    Marks(
                                        mark.child("skillId").value.toString(),
                                        mark.child("stdId").value.toString(),
                                        mark.child("marks").value.toString(),
                                        mark.child("date").value.toString(),
                                        mark.child("skillIdStdId").value.toString(),
                                        mark.child("skillName").value.toString(),
                                        mark.child("skillPhoto").value.toString(),
                                        mark.child("highestScore").value.toString()
                                    )
                                )
                            }

                        }
                        _markList.value = Resource.Success(markList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _markList.value = Resource.Error("Data Retrieve unsuccessful")
                    }
                })
        }

    }

    fun fetchMarksByDay(stdId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dbMarksRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val markList = arrayListOf<Marks>()
                        dataSnapshot.children.forEach { mark ->
                            Log.d("xyz", "onDataChange: ${mark.child("stdId").value.toString()}  ${stdId}")
                            Log.d("TAG", "onDataChange:${mark.child("stdId").value.toString()}  ${stdId}")
                            if (mark.child("stdId").value.toString() == stdId) {
                                markList.add(
                                    Marks(
                                        mark.child("skillId").value.toString(),
                                        mark.child("stdId").value.toString(),
                                        mark.child("marks").value.toString(),
                                        mark.child("date").value.toString(),
                                        mark.child("skillIdStdId").value.toString(),
                                        mark.child("skillName").value.toString(),
                                        mark.child("skillPhoto").value.toString(),
                                        mark.child("highestScore").value.toString()
                                    )
                                )
                            }

                        }
                        _markList.value = Resource.Success(markList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _markList.value = Resource.Error("Data Retrieve unsuccessful")
                    }
                })
        }

    }



}


data class Class(
    var classId: String,
    var teacherId: String,
    var name: String,
    var grade: String,
    var img: String
)

data class Skill(
    var classId: String,
    var skillId: String,
    var name: String,
    var highestScore: String,
    var img: String,
    var type: String
)

data class Marks(
    var skillId: String,
    var stdId: String,
    var marks: String,
    var date: String,
    var skillIdStdId: String,
    var skillName:String,
    var skillPhoto:String,
    var highestScore: String
)


