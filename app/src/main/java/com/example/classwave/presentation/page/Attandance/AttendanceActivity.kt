package com.example.classwave.presentation.page.Attandance

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.DialogAttendanceBinding
import com.example.classwave.presentation.page.teacher.TeacherViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AttendanceActivity : AppCompatActivity() {
    private val viewModel: AttendanceViewModel by viewModels()

    private var attendanceAdapter = AttendanceAdapter()
    private lateinit var binding: DialogAttendanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var clsId = intent.getStringExtra("clsId")
        binding = DialogAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clsId?.let { viewModel.fetchStudentByClassId(it) }
        binding.recyclerView.adapter = attendanceAdapter
        val attendance = mutableMapOf<String, Boolean>()
        attendanceAdapter.setListener(listener = object : AttendanceAdapter.Listener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onStudentClicked(

                value: Boolean,
                position: Int,
                studentId: String
            ) {
                attendance[studentId] = value

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
                /* attendanceAdapter.setAttendance(!attendance)*/
            }


        })

        if (clsId != null) {
            viewModel.fetchStudentByClassId(clsId)
        }
        initialFlowCollectors()

    }

    fun initialFlowCollectors() {

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
                viewModel.studentList.collectLatest { std ->
                    if (std != null) {
                        Log.d("_xyz", "initialFlowCollectors: ${std.data?.size}")
                        std.data?.let { attendanceAdapter.setStudents(it) }
                    }
                }
            }
        }


    }


}