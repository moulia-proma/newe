package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classwave.R
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.parent.Request
import com.example.classwave.presentation.page.report.Report
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private var dbRequestedStudentRef =
        FirebaseDatabase.getInstance().getReference("requestedStudent")

    private val uid = Firebase.auth.currentUser?.uid


    private val _skillReportList = MutableStateFlow<Resource<MutableList<Report>>?>(null)
    var skillReportList = _skillReportList.asStateFlow()

    private val _classList = MutableStateFlow<Resource<List<Class>>>(Resource.Loading())
    var classList: StateFlow<Resource<List<Class>>> = _classList.asStateFlow()

    private var _signOut = MutableStateFlow<Boolean?>(null)
    var signOut = _signOut.asStateFlow()

    private val _markList = MutableStateFlow<Resource<List<Marks>>?>(null)
    var markList = _markList.asStateFlow()

    private val _studentList = MutableStateFlow<Resource<List<Student>>?>(null)
    var studentList = _studentList.asStateFlow()

    private val _posSkillList = MutableStateFlow<Resource<List<Skill>>?>(null)
    var posSkillList = _posSkillList.asStateFlow()

    private val _negSkillList = MutableStateFlow<Resource<List<Skill>>?>(null)
    var negSkillList = _negSkillList.asStateFlow()

    private var _createClass = MutableStateFlow<Resource<Class>?>(null)
    var createClass = _createClass.asStateFlow()

    private var _createStudent = MutableStateFlow<Resource<Student>?>(null)
    var createStudent = _createStudent.asStateFlow()

    private var _createSkill = MutableStateFlow<Resource<Skill>?>(null)
    var createSkill = _createSkill.asStateFlow()

    private val _selectedClass = MutableStateFlow<Class?>(null)
    val selectedClass = _selectedClass.asStateFlow()

    private val _addMarks = MutableStateFlow<Resource<Marks>?>(null)
    val addMarks = _addMarks.asStateFlow()

    private var _deleteClass = MutableStateFlow<Resource<String>?>(null)
    var deleteClass = _deleteClass.asStateFlow()

    private val _uniqueDays = MutableStateFlow<String>("")
    val marksByDay = _uniqueDays.asStateFlow()

    private val _newNotificationList = MutableStateFlow<Resource<ArrayList<Request>>?>(null)
    var newNotificationList = _newNotificationList.asStateFlow()

    private val _oldNotificationList = MutableStateFlow<Resource<ArrayList<Request>>?>(null)
    var oldNotificationList = _oldNotificationList.asStateFlow()


    private val _notificationList = MutableStateFlow<Resource<ArrayList<Request>>?>(null)
    var notificationList = _notificationList.asStateFlow()


    fun setNull() {
        _createClass = MutableStateFlow<Resource<Class>?>(null)
        createClass = _createClass.asStateFlow()
        _deleteClass = MutableStateFlow<Resource<String>?>(null)
        deleteClass = _deleteClass.asStateFlow()
        _createStudent = MutableStateFlow<Resource<Student>?>(null)
        createStudent = _createStudent.asStateFlow()
        _createSkill = MutableStateFlow<Resource<Skill>?>(null)
        createSkill = _createSkill.asStateFlow()
    }

    private val clsImage = arrayListOf<Int>(
      /*  R.drawable.cls_chemistry,
        R.drawable.cls_alculating,
        R.drawable.cls_blackboard,
        R.drawable.cls_calculator,
        R.drawable.cls_maths,
        R.drawable.cls_computer_science,
        R.drawable.cls_mathematics_symbol,
        R.drawable.cls_online_learning,
        R.drawable.cls_teaching*/
        R.drawable.class_1,
        R.drawable.class_2,
        R.drawable.class_3,
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

    fun selectedClass(cls: Class?) {

        Log.d("_xyz", "selectedClass: ${cls?.classId} | ${cls?.name}")
        viewModelScope.launch {
            _selectedClass.value = cls
        }
        if (cls == null) return
        fetchNotification()

//          comment from prvz
//        notification()
//        fetchStudentByClassId(classId = cls.classId)
//        fetchSkillByClassId(classId = cls.classId)
//        fetchSkillByClassId(classId = cls.classId)
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
    fun getTotalPosMarks(marks: List<Marks>): Int {

        var sum = 0
        var i = 0
        while (i < marks.size) {
            if (marks[i].highestScore.toInt() > 0) sum += marks[i].highestScore.toInt()
            i++
        }
        return sum

    }

    @SuppressLint("SuspiciousIndentation")
    fun getTotalNegMarks(marks: List<Marks>): Int {

        var sum = 0
        var i = 0
        while (i < marks.size) {
            if (marks[i].highestScore.toInt() <= 0) {
                sum += kotlin.math.abs(marks[i].highestScore.toInt())
            }

            i++
        }
        return sum

    }

    fun getTotalAchivedNegMarks(marks: List<Marks>): Int {

        var sum = 0
        var i = 0
        while (i < marks.size) {
            if (marks[i].marks.toInt() <= 0) sum += kotlin.math.abs(marks[i].marks.toInt())
            i++
        }
        return sum

    }

    fun getTotalAchivedPosMarks(marks: List<Marks>): Int {

        var sum = 0
        var i = 0
        while (i < marks.size) {
            if (marks[i].marks.toInt() > 0) sum += marks[i].marks.toInt()
            i++
        }
        return sum

    }

    fun getProgress(totalNeg: Int?, totalPos: Int?, negAchived: Int?, posAchived: Int?): Int {


        if (posAchived != null) {
            if (posAchived > 0) {
                return ((posAchived / (totalPos!! * 1.0) * 100)).toInt()
            }
        }
        return 0


    }

    private fun fetchClassList() {
        val uid = Firebase.auth.currentUser?.uid
        viewModelScope.launch(Dispatchers.IO) {
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val classList = arrayListOf<Class>()
                    dataSnapshot.children.forEach { classRoom ->
                        if (classRoom.child("teacherId").value.toString() == uid.toString()) {
                            classList.add(
                                Class(
                                    classRoom.child("classId").value.toString(),
                                    classRoom.child("teacherId").value.toString(),
                                    classRoom.child("name").value.toString(),
                                    classRoom.child("grade").value.toString(),
                                    classRoom.child("img").value.toString()
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

    fun fetchStudentByClassId(classId: String) {
        _studentList.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbStdRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val studentList = arrayListOf<Student>()
                    dataSnapshot.children.forEach { student ->
                        if (student.child("classId").value.toString() == classId) {
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
    }

    private fun fetchSkillByClassId(classId: String) {
        _posSkillList.value = Resource.Loading()
        _negSkillList.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbSkillRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val posSkillList = arrayListOf<Skill>()
                    val negSkillList = arrayListOf<Skill>()
                    dataSnapshot.children.forEach { skill ->
                        if (skill.child("classId").value.toString() == classId) {
                            if (skill.child("type").value.toString() == "pos" && skill.child("skillId").value != "attendance123") {
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
                                if (skill.child("skillId").value != "attendance123") {
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

    fun createClass(
        name: String, grade: String, classId: String, tcrId: String, clsImg: String, type: String
    ) {
        _createClass.value = Resource.Loading()

        if (name.isEmpty()) {
            _createClass.value = Resource.Error("Class name can't be empty")
            return
        }
        if (name.isEmpty()) {
            _createClass.value = Resource.Error("You have to select at least one grade")
            return
        }
        var clsId = ""
        var teacherId = ""
        var img = ""
        if (type == "create") {
            clsId = dbRef.push().key.toString()
            if (uid != null) {
                teacherId = uid
            }
            img = clsImage.random().toString()
        } else {
            clsId = classId
            teacherId = tcrId
            img = clsImg
        }
        viewModelScope.launch(Dispatchers.IO) {
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    dataSnapshot.children.forEach { cls ->
                        if (cls.child("classId").value.toString() == classId) {
                            val k = cls.key
                            dbRef.child(k.toString()).removeValue()
                        }

                    }
                    val clsRoom = Class(clsId ?: "", teacherId ?: "", name, grade, img)
                    val attendanceSkill = clsId?.let {
                        Skill(
                            it,
                            "attendance123",
                            "Attendance",
                            "1",
                            (R.drawable.cls_alculating).toString(),
                            "pos"
                        )
                    }
                    dbRef.push().setValue(clsRoom).addOnCompleteListener {
                        _createClass.value = Resource.Success(clsRoom)
                        if (type == "create") {
                            dbSkillRef.push().setValue(attendanceSkill)

                        }
                        fetchClassList()
                    }.addOnFailureListener {
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _createClass.value = Resource.Error("Failed")
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createStudent(clsId: String, name: String) {
        _createStudent.value = Resource.Loading()
        if (name.isEmpty()) {
            _createStudent.value = Resource.Error("Class name can't be empty")
            return
        }
        val stdId = dbStdRef.push().key
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
            _createStudent.value = Resource.Error("User creation Failed")
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
            clsId ?: "", skillId ?: "", name, hScore, skillImage.random().toString(), typeofSkill
        )

        dbSkillRef.push().setValue(skill).addOnCompleteListener {
            _createSkill.value = Resource.Success(skill)
        }.addOnFailureListener {
            _createSkill.value = Resource.Error("Skill creation Failed")
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
        highestScore: String,
        clsId: String,
        stdName: String,
        stdProfile: String
    ) {
        val st = (skillId + "_" + stdId)
        viewModelScope.launch(Dispatchers.IO) {
            dbMarksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children.forEach { mark ->
                        if (mark.child("skillIdStdId").value.toString() == st) {
                            val k = mark.key
                            FirebaseDatabase.getInstance().getReference("Marks").child(k.toString())
                                .removeValue()
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
                        highestScore,
                        stdName,
                        stdProfile
                    )
                    dbMarksRef.push().setValue(mark).addOnCompleteListener {
                        _addMarks.value = Resource.Success(mark)
                    }.addOnFailureListener {
                        _createSkill.value = Resource.Error("Skill creation Failed")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    _markList.value = Resource.Error("Data Retrieve unsuccessful")
                }
            })
        }
    }

    fun getReportSkillSpecific(skillId: String) {
        _skillReportList.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbMarksRef.orderByChild("marks")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    var list = ArrayList<Report>()

                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataSnapshot.children.forEach {
                            if (it.child("skillId").value == skillId && skillId != "attendance123") {
                                var mark = Report(
                                    it.child("skillId").value.toString(),
                                    it.child("stdId").value.toString(),
                                    it.child("marks").value.toString(),
                                    it.child("date").value.toString(),
                                    it.child("skillIdStdId").value.toString(),
                                    it.child("skillName").value.toString(),
                                    it.child("skillPhoto").value.toString(),
                                    it.child("highestScore").value.toString(),
                                    it.child("stdName").value.toString(),
                                    it.child("stdProfile").value.toString(),

                                    )
                                list.add(mark)
                            }
                        }
                        _skillReportList.value = Resource.Success(list)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _skillReportList.value = Resource.Error("Data Retrieve unsuccessful")
                    }
                })
        }

    }

    fun deleteClass(classId: String) {
        _deleteClass.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dataSnapshot.children.forEach { cls ->
                        if (cls.child("classId").value.toString() == classId) {
                            val k = cls.key
                            dbRef.child(k.toString()).removeValue().addOnCompleteListener {
                                _deleteClass.value = Resource.Success(classId)
                                fetchClassList()
                            }.addOnFailureListener {
                                _deleteClass.value = Resource.Error("delete failed")
                            }
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    private fun fetchNotification() {
        viewModelScope.launch(Dispatchers.IO) {
            dbRequestedStudentRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val arr = arrayListOf<Request>()
                    snapshot.children.forEach { request ->
                        if (request.child("clsId").value.toString() == _selectedClass.value?.classId.toString()) {
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
                                )
                            )
                        }
                    }
                    _notificationList.value = Resource.Success(arr)
                }

                override fun onCancelled(error: DatabaseError) {
                    _notificationList.value = null
                }
            })
        }

    }

    fun updateNotificationState() {
        viewModelScope.launch(Dispatchers.IO) {
            dbRequestedStudentRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { request ->
                        if (request.child("clsId").value.toString() == _selectedClass.value?.classId.toString() && request.child(
                                "state"
                            ).value.toString() == "pending"
                        ) {
                            val k = request.key.toString()
                            val req = Request(
                                request.child("parentId").value.toString(),
                                request.child("teacherId").value.toString(),
                                request.child("clsId").value.toString(),
                                request.child("stdId").value.toString(),
                                "viewed",
                                request.child("uPhoto").value.toString(),
                                request.child("stdImage").value.toString(),
                                request.child("parentPhoto").value.toString(),
                                request.child("name").value.toString(),
                                request.child("parentName").value.toString(),
                                request.child("stdName").value.toString(),
                                request.child("time").value.toString(),
                            )
                            dbRequestedStudentRef.child(k).removeValue()
                            dbRequestedStudentRef.push().setValue(req)
                        }
                    }

                    fetchNotification()
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

data class Class(
    var classId: String, var teacherId: String, var name: String, var grade: String, var img: String
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
    val classId: String,
    var skillId: String,
    var stdId: String,
    var marks: String,
    var date: String,
    var skillIdStdId: String,
    var skillName: String,
    var skillPhoto: String,
    var highestScore: String,
    var stdName: String,
    var stdProfile: String
)


