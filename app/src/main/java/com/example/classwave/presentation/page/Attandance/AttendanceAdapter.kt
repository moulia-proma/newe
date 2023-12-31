package com.example.classwave.presentation.page.Attandance

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemAttendanceBinding
import com.example.classwave.presentation.page.teacher.Student

class AttendanceAdapter : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {
    private var mStudentList = listOf<Student>()
    lateinit var classId: String

    private var mListener: AttendanceAdapter.Listener? = null
    private var attendance = mutableMapOf<String, Boolean>()

    interface Listener {
        fun onStudentClicked(b: Boolean, studentId: String)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttendanceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return (mStudentList.size)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("abc", "onBindViewHolder:ee")
        holder.Attendance(mStudentList[position], position)
    }

    fun setStudents(students: List<Student>) {
        mStudentList = students
        notifyDataSetChanged()

    }

    fun setDefaultAttendance(attendance: MutableMap<String, Boolean>) {
        this.attendance = attendance
        Log.d("pik", "setDefaultAttendance: ${this.attendance.size}")
        notifyDataSetChanged()

    }


    fun setListener(listener: AttendanceAdapter.Listener) {
        mListener = listener
    }

    inner class ViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SuspiciousIndentation")

        fun Attendance(student: Student, position: Int) {
            Log.d("pik", "Attendance: ${attendance[student.studentId]}")

            binding.textViewName.text = student.studentName
            if (attendance[student.studentId] == null) {
                attendance[student.studentId] = false
            }
            if (attendance[student.studentId] == true) {
                binding.textViewAttendance.text = "1"

            } else {
                binding.textViewAttendance.text = "0"
            }
            binding.textViewName.setOnClickListener {

                attendance[student.studentId] = !attendance[student.studentId]!!
                if (attendance[student.studentId] == true) {
                    binding.textViewAttendance.text = "1"
                } else {
                    binding.textViewAttendance.text = "0"
                }
                attendance[student.studentId]?.let { it1 ->
                    mListener?.onStudentClicked(
                        it1,
                        student.studentId
                    )
                }

            }

            attendance[student.studentId]?.let {
                mListener?.onStudentClicked(
                    it,
                    student.studentId
                )
            }

        }
    }


}


