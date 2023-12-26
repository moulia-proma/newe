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
                                    it.child("date"
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
                                mark.child("highestScore").value.toString()
                            )
                            val dateInStr =  mark.child("date").value.toString()
                            val data = reportMaps[dateInStr] ?: mutableListOf()
                            data.add(report)
                            reportMaps[dateInStr] = data
                        }

                        _reportList.value = Resource.Success(reportMaps)
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

}

enum class FilterType {
    Day,
    Month,
    Year
}


data class Report(
    var skillId: String,
    var stdId: String,
    var marks: String,
    var date: String,
    var skillIdStdId: String,
    var skillName: String,
    var skillPhoto: String,
    var highestScore: String
)
