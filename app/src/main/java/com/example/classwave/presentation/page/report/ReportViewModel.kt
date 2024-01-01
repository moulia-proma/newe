package com.example.classwave.presentation.page.report

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classwave.domain.model.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor() : ViewModel() {

    private var dbMarksRef = FirebaseDatabase.getInstance().getReference("Marks")

    private val _reportList = MutableStateFlow<Resource<MutableMap<String, MutableList<Report>>>>(
        Resource.Success(mutableMapOf())
    )
    var reportList: StateFlow<Resource<MutableMap<String, MutableList<Report>>>> =
        _reportList.asStateFlow()
    private val _reportClassList =
        MutableStateFlow<Resource<MutableMap<String, MutableList<Report>>>>(
            Resource.Success(mutableMapOf())
        )
    var reportClassList: StateFlow<Resource<MutableMap<String, MutableList<Report>>>> =
        _reportClassList.asStateFlow()

    fun fetchReport(stdId: String, day: Int?, month: Int?, year: Int?) {

        val regex = createRegex(year, month, day)

        viewModelScope.launch(Dispatchers.IO) {
            dbMarksRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val reportMaps: MutableMap<String, MutableList<Report>> = mutableMapOf()

                        dataSnapshot.children.filter {
                            it.child("stdId").value.toString() == stdId &&
                                    it.child(
                                        "date"
                                    ).value.toString().matches(regex)
                        }.forEach { mark ->

                            val report = Report(
                                mark.child("skillId").value.toString(),
                                mark.child("stdId").value.toString(),
                                mark.child("marks").value.toString(),
                                mark.child("date").value.toString(),
                                mark.child("skillIdStdId").value.toString(),
                                mark.child("skillName").value.toString(),
                                mark.child("skillPhoto").value.toString(),
                                mark.child("highestScore").value.toString(),
                                mark.child("stdName").value.toString(),
                                mark.child("stdProfile").value.toString(),
                            )
                            val dateInStr = mark.child("date").value.toString()
                            val data = reportMaps[dateInStr] ?: mutableListOf()
                            data.add(report)
                            reportMaps[dateInStr] = data
                        }

                        _reportList.value = Resource.Success(reportMaps)
                        /* getTotalPosMarks(reportMaps)
                         getTotalNegMarks(reportMaps)
                         getTotalAchivedNegMarks(reportMaps)
                         getTotalAchivedPosMarks(reportMaps)*/
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _reportList.value = Resource.Error("Data Retrieve unsuccessful")
                    }
                })
        }
    }

    fun fetchClassReport(clsId: String, day: Int?, month: Int?, year: Int?) {

        val regex = createRegex(year, month, day)

        viewModelScope.launch(Dispatchers.IO) {
            dbMarksRef.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val reportMaps: MutableMap<String, MutableList<Report>> = mutableMapOf()

                        dataSnapshot.children.filter {
                            Log.d("IT", "onDataChange: ${it}")
                            Log.d(
                                "_debug",
                                "onDataChange: ${it.child("classId").value.toString()} ${clsId}  ${
                                    it.child("date").value.toString().matches(regex)
                                }"
                            )
                            it.child("classId").value.toString() == clsId &&
                                    it.child(
                                        "date"
                                    ).value.toString().matches(regex)
                        }.forEach { mark ->

                            val report = Report(
                                mark.child("skillId").value.toString(),
                                mark.child("stdId").value.toString(),
                                mark.child("marks").value.toString(),
                                mark.child("date").value.toString(),
                                mark.child("skillIdStdId").value.toString(),
                                mark.child("skillName").value.toString(),
                                mark.child("skillPhoto").value.toString(),
                                mark.child("highestScore").value.toString(),
                                mark.child("stdName").value.toString(),
                                mark.child("stdProfile").value.toString(),
                            )
                            val dateInStr = mark.child("date").value.toString()
                            val data = reportMaps[dateInStr] ?: mutableListOf()
                            data.add(report)
                            reportMaps[dateInStr] = data
                        }
                        Log.d("pik", "onDataChange: ${reportMaps}")

                        _reportClassList.value = Resource.Success(reportMaps)
                        /* getTotalPosMarks(reportMaps)
                         getTotalNegMarks(reportMaps)
                         getTotalAchivedNegMarks(reportMaps)
                         getTotalAchivedPosMarks(reportMaps)*/
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        _reportList.value = Resource.Error("Data Retrieve unsuccessful")
                    }
                })
        }
    }


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

    fun getTotalPosMarks(marks: MutableMap<String, MutableList<Report>>): Int {
        var sum = 0
        var map = mutableMapOf<String, String>()
        marks.forEach { key ->
            val list = marks[key.key]
            Log.d("_pro", "getTotalPosMarks: ${list}")
            list?.forEach {
                sum += it.highestScore.toInt()
            }
        }

        return sum
    }

    fun getTotalNegMarks(marks: MutableMap<String, MutableList<Report>>): Int {
        Log.d("pp", "getTotalNegMarks: I m negative")
        var sum = 0

        var map = mutableMapOf<String, String>()
        marks.forEach { key ->
            val list = marks[key.key]
            Log.d("_pro", "getTotalPosMarks: ${list}")
            if (list != null) {
                list.forEach {
                    if (it.highestScore.toInt() <= 0) {
                        sum += kotlin.math.abs(it.highestScore.toInt())
                    }

                }
            }
        }

        return sum


    }

    fun getTotalAchivedNegMarks(marks: MutableMap<String, MutableList<Report>>): Int {

        var sum = 0
        var map = mutableMapOf<String, String>()
        Log.d("_size", "getTotalAchivedNegMarks: ${marks.size}")
        marks.forEach { key ->
            val list = marks[key.key]
            Log.d("_pro", "getTotalPosMarks: ${list}")
            if (list != null) {
                Log.d("_abc", "getTotalAchivedNegMarks: ${sum}")
                list.forEach {
                    if (it.marks.toInt() <= 0)
                        sum += kotlin.math.abs(it.marks.toInt())
                }

            }
        }
        return sum


    }

    fun getTotalAchivedPosMarks(marks: MutableMap<String, MutableList<Report>>): Int {

        var sum = 0
        var map = mutableMapOf<String, String>()
        marks.forEach { key ->
            val list = marks[key.key]
            Log.d("_pro", "getTotalPosMarks: ${list}")
            if (list != null) {
                list.forEach {
                    Log.d("_pos", "getTotalAchivedPosMarks: ${sum}")
                    if (it.marks.toInt() > 0)
                        sum += it.marks.toInt()
                }

            }
        }
        return sum


    }


    fun getProgress(totalPos: Int, achievedPos: Int): Int {

        if (achievedPos != null) {
            if (achievedPos > 0) {
                return ((achievedPos / (totalPos!! * 1.0) * 100)).toInt()
            }
        }
        return 0
    }
}

enum class FilterType {
    Day,
    Month,
    Year
}

enum class ReportPercentage {
    MarksInPercent,
    TotalPositive,
    TotalNegative,
    AchievedPositive,
    AchievedNegative
}
data class Report(
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
