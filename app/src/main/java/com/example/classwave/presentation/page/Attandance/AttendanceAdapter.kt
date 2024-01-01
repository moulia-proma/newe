package com.example.classwave.presentation.page.Attandance

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemAttendanceDatewiseBinding
import com.example.classwave.presentation.page.report.Report
import com.example.classwave.presentation.page.teacher.Student


class AttendanceAdapter : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {
    private var mStudentList = listOf<Student>()
    lateinit var classId: String
    private var atten = mutableMapOf<String, Boolean>()

    /*    private var mListener: AttendanceAdapter.Listener? = null*/
    private var attendance: MutableMap<String, MutableList<Report>> = mutableMapOf()

    /*   interface Listener {
           fun onStudentClicked(b: Boolean, studentId: String)

       }*/
    private var mListener: Listener? = null

    private var attendanceChildAdapter = AttendanceChilddapter()
    private var name = mutableMapOf<String,String>()
    private var profile = mutableMapOf<String,String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttendanceDatewiseBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    /*    attendanceChildAdapter.setListener(listener = object : AttendanceChilddapter.Listener {
            override fun onStudentClicked(value: Boolean, studentId: String) {
                Log.d("_r", "onStudentClicked: rrr")
                attendance[studentId] = value
                Log.d("_atten", "onStudentClicked: ${value}")
            }
        })*/

    override fun getItemCount(): Int {
        return attendance.size
    }
    fun setData(name: MutableMap<String, String>, profile: MutableMap<String, String>) {
        Log.d("_xyz", "setData: $name")
        this.name = name
        this.profile = profile
    }

    fun setListener(listener: Listener) {
        Log.d("_atten", "setListener: tt")
        mListener = listener
    }

    interface Listener {
        fun onStudentClicked(value: Boolean, studentId: String)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("abc", "onBindViewHolder:ee")

        val key = attendance.keys.elementAt(position)
        val value = attendance[key]
        Log.d("_atten", "onBindViewHolder: ${key} ${value} ")
        holder.bindData(key, value)
    }

    fun setStudents(students: List<Student>) {
        mStudentList = students
        notifyDataSetChanged()

    }

    fun setDefaultAttendance(attendance: MutableMap<String, MutableList<Report>>) {
        this.attendance.clear()
        this.attendance.putAll(attendance)


        notifyDataSetChanged()

    }


    /*   fun setListener(listener: AttendanceAdapter.Listener) {
           mListener = listener
       }*/

    inner class ViewHolder(private val binding: ItemAttendanceDatewiseBinding) :
        RecyclerView.ViewHolder(binding.root) {


        /* fun Attendance(student: Student, position: Int) {*/


        /*
                    Log.d("pik", "Attendance: ${attendance[student.studentId]}")*/


        /*         if (attendance[student.studentId] == null) {
                     attendance[student.studentId] = false
                 }

                 if (attendance[student.studentId] == true) {
                     binding.textViewAttendance.text = "1"

                 } else {
                     binding.textViewAttendance.text = "0"
                 }*/
        /*       binding.textViewName.setOnClickListener {

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

           }*/

        fun bindData(date: String, reports: MutableList<Report>?) {
            binding.textMarksDate.text = date
            val childAdapter = AttendanceChilddapter()
            childAdapter.setListener(object : AttendanceChilddapter.Listener {
                override fun onStudentClicked(value: Boolean, studentId: String) {
                    atten[studentId] = value
                    mListener?.onStudentClicked(value, studentId)
                }

            })
            binding.recyclerViewMarksDetails.adapter = childAdapter
            childAdapter.setReports(reports ?: mutableListOf())
            childAdapter.setData(name, profile)


        }
    }


}


