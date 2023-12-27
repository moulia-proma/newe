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
    private var attendance = arrayListOf<Boolean>()

    interface Listener {
        fun onStudentClicked( b: Boolean, position: Int, studentId: String)

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
        attendance.add(false)
        holder.Attendance(mStudentList[position], position)
    }

    fun setStudents(students: List<Student>) {
        mStudentList = students
        notifyDataSetChanged()
    }

    fun setListener(listener: AttendanceAdapter.Listener) {
        mListener = listener
    }

    inner class ViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SuspiciousIndentation")

        fun Attendance(student: Student, position: Int) {

            Log.d("_siz", "Attendance: ${attendance.size}")
            binding.textViewName.text = student.studentName

            binding.textViewName.setOnClickListener {
                attendance[position] = !attendance[position]
                if (attendance[position]) {
                    binding.textViewAttendance.text = "1"

                } else {
                    binding.textViewAttendance.text = "N/A"
                }
                mListener?.onStudentClicked(attendance[position],position,student.studentId)

            }


        }
    }


}


