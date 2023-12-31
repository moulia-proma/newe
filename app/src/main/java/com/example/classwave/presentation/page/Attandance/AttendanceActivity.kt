package com.example.classwave.presentation.page.Attandance

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.DialogAttendanceBinding
import com.example.classwave.presentation.page.report.FilterType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class AttendanceActivity : AppCompatActivity() {
    private val viewModel: AttendanceViewModel by viewModels()

    private var attendanceAdapter = AttendanceAdapter()
    private lateinit var binding: DialogAttendanceBinding
    private var selectedFilterType = FilterType.Day
    private var currentDate = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var clsId = intent.getStringExtra("clsId")
        binding = DialogAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.recyclerView.adapter = attendanceAdapter

        val attendance = mutableMapOf<String, Boolean>()

        attendanceAdapter.setListener(listener = object : AttendanceAdapter.Listener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onStudentClicked(att: Boolean, studentId: String) {
                attendance[studentId] = att
            }


        })
        if (clsId != null) {
            viewModel.fetchStudentByClassId(clsId)
        }
        binding.buttonSave.setOnClickListener {
            attendance.forEach {
                if (clsId != null) {
                    viewModel.addMarks(
                        it.key,
                        "attendance123",
                        attendance[it.key].toString(),
                        "Attendance",
                        R.drawable.cls_alculating.toString(),
                        "1",
                        clsId
                    )
                }
            }
        }

        Log.d("_xyz", "onCreate: ${clsId}")
        viewModel.fetchAttendance()
        initialFlowCollectors()

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun registerListener(){
        binding.btnNext.setOnClickListener {
            if (selectedFilterType == FilterType.Day) {

                Log.d("_xyz", "registerListener: jj")
                currentDate = currentDate.plusDays(1)
                binding.textReportDate.text = currentDate.toString()
                clsId?.let { it1 ->
                    viewModel.fetchClassReport(
                        it1,
                        currentDate.dayOfMonth,
                        currentDate.monthValue,
                        currentDate.year
                    )
                }
            }
            if (selectedFilterType == FilterType.Month) {


                currentDate = currentDate.plusMonths(1)
                binding.textReportDate.text =
                    currentDate.month.toString() + " " + currentDate.year.toString()
                clsId?.let { it1 ->
                    viewModel.fetchClassReport(
                        it1, null, currentDate.monthValue, currentDate.year
                    )
                }
            }
            if (selectedFilterType == FilterType.Year) {

                currentDate = currentDate.plusYears(1)
                binding.textReportDate.text = currentDate.year.toString()
                clsId?.let { it1 ->
                    viewModel.fetchClassReport(
                        it1, null, null, currentDate.year
                    )
                }
            }
        }

        binding.btnPrev.setOnClickListener {


            if (selectedFilterType == FilterType.Day) {
                Log.d("_xyz", "registerListener: jj")
                currentDate = currentDate.minusDays(1)
                binding.textReportDate.text = currentDate.toString()
                clsId?.let { it1 ->
                    viewModel.fetchClassReport(
                        it1, currentDate.dayOfMonth, null, currentDate.year
                    )
                }
            }

            if (selectedFilterType == FilterType.Month) {

                currentDate = currentDate.minusMonths(1)
                binding.textReportDate.text =
                    currentDate.month.toString() + " " + currentDate.year.toString()
                clsId?.let { it1 ->
                    viewModel.fetchClassReport(
                        it1, null, currentDate.monthValue, currentDate.year
                    )
                }
            }
            if (selectedFilterType == FilterType.Year) {
                currentDate = currentDate.minusYears(1)
                binding.textReportDate.text = currentDate.year.toString()
                clsId?.let { it1 ->
                    viewModel.fetchClassReport(
                        it1, null, null, currentDate.year
                    )
                }
            }
        }

        binding.cardDayReport.setOnClickListener {
            currentDate = LocalDate.now()
            selectedFilterType = FilterType.Day
            binding.textReportDate.text = currentDate.toString()
            clsId?.let { it1 ->
                viewModel.fetchClassReport(
                    it1,
                    currentDate.dayOfMonth,
                    currentDate.monthValue,
                    currentDate.year
                )
            }

            /*  selectedFilterType = FilterType.Day*/
        }

        binding.cardMonthReport.setOnClickListener {
            currentDate = LocalDate.now()
            selectedFilterType = FilterType.Month
            binding.textReportDate.text =
                currentDate.month.toString() + " " + currentDate.year.toString()
            clsId?.let { it1 ->
                viewModel.fetchClassReport(
                    it1, null, currentDate.monthValue, currentDate.year
                )
            }
        }

        binding.cardYearReport.setOnClickListener {
            currentDate = LocalDate.now()
            binding.textReportDate.text = currentDate.year.toString()
            selectedFilterType = FilterType.Year
            clsId?.let { it1 ->
                viewModel.fetchClassReport(
                    it1, null, null, currentDate.year
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initialFlowCollectors() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.studentList.collectLatest { std ->
                    if (std != null) {
                        Log.d("_xyz", "initialFlowCollectors: ${std.data?.size}")
                        std.data?.let { attendanceAdapter.setStudents(it) }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.attendance.collectLatest { atn ->

                    Log.d("_sk", "initialFlowCollectors:size  ${atn}")
                    atn.forEach{
                        Log.d("_x", "initialFlowCollectors: ${atn[it.key]}")
                    }
                      attendanceAdapter.setDefaultAttendance(atn)
                }
            }
        }

    }


}