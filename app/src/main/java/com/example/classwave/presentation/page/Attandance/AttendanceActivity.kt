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
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.page.report.FilterType
import com.example.classwave.presentation.page.teacher.Student
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class AttendanceActivity : AppCompatActivity() {
    private val viewModel: AttendanceViewModel by viewModels()

    private var attendanceAdapter = AttendanceAdapter()
    private var attendanceChildAdapter = AttendanceChilddapter()
    private lateinit var binding: DialogAttendanceBinding
    private var selectedFilterType = FilterType.Day

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentDate = LocalDate.now()
    var stdList = listOf<Student>()
    var clsId = ""
    private var name = mutableMapOf<String, String>()
    private var profile = mutableMapOf<String, String>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clsId = intent.getStringExtra("clsId").toString()
        binding = DialogAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.adapter = attendanceAdapter

        val attendance = mutableMapOf<String, Boolean>()

        attendanceAdapter.setListener(listener = object : AttendanceAdapter.Listener {
            override fun onStudentClicked(value: Boolean, studentId: String) {
                attendance[studentId] = value
            }
        })

        initialFlowCollectors()
        if (clsId != null) {
            Log.d("_xyz", "onCreate: $clsId")
            viewModel.fetchStudentByClassId(clsId)
        }
        binding.buttonSave.setOnClickListener {
            Log.d("_save", "onCreate: save button clicked ${attendance}")

            attendance.forEach {
                viewModel.addMarks(
                    it.key,
                    "attendance123",
                    attendance[it.key].toString(),
                    "Attendance",
                    R.drawable.cls_alculating.toString(),
                    "1",
                    clsId,
                    "",
                    ""
                )

            }


            onBackPressed()


        }


        binding.textReportDate.text = "Today"
        Log.d("_xyz", "onCreate: $stdList")
        Log.d("_xyz", "onCreate: ${clsId}")
        registerListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerListener() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnNext.setOnClickListener {
            if (currentDate == LocalDate.now()) {
                binding.btnNext.isClickable = false
                binding.btnNext.setImageResource(R.drawable.ic_arrow_front_grey)
            }

            if (selectedFilterType == FilterType.Day) {

                Log.d("_xyz", "registerListener: jj")

                currentDate = currentDate.plusDays(1)
                if (currentDate == LocalDate.now()) {
                    binding.buttonSave.visibility = View.VISIBLE
                    binding.btnNext.isClickable = false
                    binding.btnNext.setImageResource(R.drawable.ic_arrow_front_grey)
                } else {
                    binding.buttonSave.visibility = View.INVISIBLE
                }
                if (currentDate == LocalDate.now()) {
                    binding.textReportDate.text = "Today"
                } else {
                    binding.textReportDate.text =
                        currentDate.toString()
                }
                binding.textReportDate.text = currentDate.toString()
                viewModel.fetchAttendance(
                    clsId,
                    stdList,
                    currentDate.dayOfMonth,
                    currentDate.monthValue,
                    currentDate.year,
                    currentDate
                )
            }


            if (selectedFilterType == FilterType.Month) {


                currentDate = currentDate.plusMonths(1)
                if (currentDate == LocalDate.now()) {
                    binding.btnNext.isClickable = false
                    binding.btnNext.setImageResource(R.drawable.ic_arrow_front_grey)
                }
                if (currentDate == LocalDate.now()) {
                    binding.textReportDate.text = "Current month"
                } else {
                    binding.textReportDate.text =
                        currentDate.month.toString() + " " + currentDate.year.toString()
                }
                binding.buttonSave.visibility = View.INVISIBLE

                viewModel.fetchAttendance(
                    clsId,
                    stdList,
                    null,
                    currentDate.monthValue,
                    currentDate.year,
                    currentDate
                )

            }
            if (selectedFilterType == FilterType.Year) {
                binding.buttonSave.visibility = View.INVISIBLE
                currentDate = currentDate.plusYears(1)
                if (currentDate == LocalDate.now()) {
                    binding.btnNext.isClickable = false
                    binding.btnNext.setImageResource(R.drawable.ic_arrow_front_grey)
                }
                if (currentDate == LocalDate.now()) {
                    binding.textReportDate.text = "Current Year"
                } else {
                    binding.textReportDate.text = currentDate.year.toString()
                }

                viewModel.fetchAttendance(clsId, stdList, null, null, currentDate.year, currentDate)
            }
        }


        binding.btnPrev.setOnClickListener {

            binding.btnNext.isClickable = true
            binding.btnNext.setImageResource(R.drawable.ic_arrow_front)
            if (selectedFilterType == FilterType.Day) {

                Log.d("_xyz", "registerListener: jj")
                currentDate = currentDate.minusDays(1)

                if (currentDate == LocalDate.now()) {
                    binding.btnNext.isClickable = false
                    binding.btnNext.setImageResource(R.drawable.ic_arrow_front_grey)
                }
                if (currentDate == LocalDate.now()) {
                    binding.buttonSave.visibility = View.VISIBLE
                    binding.textReportDate.text = "Today"
                } else {
                    binding.buttonSave.visibility = View.INVISIBLE
                    binding.textReportDate.text =
                        currentDate.toString()
                }

                binding.textReportDate.text = currentDate.toString()
                clsId?.let { it1 ->
                    viewModel.fetchAttendance(
                        clsId,
                        stdList,
                        currentDate.dayOfMonth,
                        null,
                        currentDate.year,
                        currentDate
                    )
                }
            }

            if (selectedFilterType == FilterType.Month) {

                currentDate = currentDate.minusMonths(1)
                if (currentDate == LocalDate.now()) {
                    binding.btnNext.isClickable = false
                    binding.btnNext.setImageResource(R.drawable.ic_arrow_front_grey)
                }
                clsId?.let { it1 ->
                    viewModel.fetchAttendance(
                        clsId,
                        stdList,
                        null,
                        currentDate.monthValue,
                        currentDate.year,
                        currentDate
                    )
                }
                if (currentDate == LocalDate.now()) {
                    binding.textReportDate.text = "This month"
                } else {
                    binding.textReportDate.text =
                        currentDate.month.toString() + " " + currentDate.year.toString()
                }
            }
            if (selectedFilterType == FilterType.Year) {
                currentDate = currentDate.minusYears(1)
                if (currentDate == LocalDate.now()) {
                    binding.btnNext.isClickable = false
                    binding.btnNext.setImageResource(R.drawable.ic_arrow_front_grey)
                }
                if (currentDate == LocalDate.now()) {
                    binding.textReportDate.text = "Current year"
                } else {

                    binding.textReportDate.text = currentDate.year.toString()
                }

                clsId?.let { it1 ->
                    viewModel.fetchAttendance(
                        clsId,
                        stdList,
                        null,
                        null,
                        currentDate.year,
                        currentDate
                    )
                }
            }
        }

        binding.cardDayReport.setOnClickListener {
            binding.buttonSave.visibility = View.VISIBLE
            currentDate = LocalDate.now()
            selectedFilterType = FilterType.Day
            binding.textReportDate.text = currentDate.toString()
            clsId?.let { it1 ->
                viewModel.fetchAttendance(
                    clsId,
                    stdList,
                    currentDate.dayOfMonth,
                    currentDate.monthValue,
                    currentDate.year,
                    currentDate
                )
            }
        }
        binding.cardMonthReport.setOnClickListener {
            binding.buttonSave.visibility = View.INVISIBLE
            currentDate = LocalDate.now()
            selectedFilterType = FilterType.Month
            binding.textReportDate.text =
                currentDate.month.toString() + " " + currentDate.year.toString()
            viewModel.fetchAttendance(
                clsId,
                stdList,
                null,
                currentDate.monthValue,
                currentDate.year,
                currentDate
            )
        }
        binding.cardYearReport.setOnClickListener {
            binding.buttonSave.visibility = View.INVISIBLE
            currentDate = LocalDate.now()
            binding.textReportDate.text = currentDate.year.toString()
            selectedFilterType = FilterType.Year
            clsId?.let { it1 ->
                viewModel.fetchAttendance(clsId, stdList, null, null, currentDate.year, currentDate)
            }
        }


    }

    /*  selectedFilterType = FilterType.Day*//*
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
    }*/

    @RequiresApi(Build.VERSION_CODES.O)

    private fun initialFlowCollectors() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.studentList.collectLatest {
                    it.let {
                        when (it) {
                            is Resource.Error -> {

                            }

                            is Resource.Loading -> {
                                Log.d("_rak", "initialFlowCollectors: jijdddddddddddddddddddi")
                                binding.progressBarLoading.visibility = View.VISIBLE
                            }

                            is Resource.Success -> {
                                binding.progressBarLoading.visibility = View.INVISIBLE
                                it.data?.forEach { st ->
                                    name[st.studentId] = st.studentName
                                    profile[st.studentId] = st.img
                                }
                                attendanceAdapter.setData(name, profile)
                                callAttendance(it.data, clsId)

                            }

                            null -> {
                                Log.d("_rak", "initialFlowCollectors: null")
                            }
                        }
                    }


                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.attendance.collectLatest { atn ->

                    /*     Log.d("_sk", "initialFlowCollectors:size  ${atn}")
                         atn.forEach{
                             Log.d("_x", "initialFlowCollectors: ${atn[it.key]}")
                         }
                           attendanceAdapter.setDefaultAttendance(atn)*/

                    Log.d("_xyz", "initialFlowCollectors: see $atn")
                    attendanceAdapter.setDefaultAttendance(atn)
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun callAttendance(resource: List<Student>?, clsId: String) {
        if (resource != null) {
            stdList = resource
        }
        if (resource != null) {
            val date = currentDate
            viewModel.fetchAttendance(
                clsId,
                resource,
                LocalDate.now().dayOfMonth,
                LocalDate.now().monthValue,
                LocalDate.now().year,
                date
            )
        }


    }


}