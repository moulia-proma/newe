package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.R
import com.example.classwave.databinding.ItemStudentBinding

class AddStudentAdapter : RecyclerView.Adapter<AddStudentAdapter.ViewHolder>() {
    private var mStudentList = listOf<Student>()
    private var mListener: AddStudentAdapter.Listener? = null
    lateinit var classId: String

    interface Listener {
        fun onAddNewStudentClicked(clsId: String)

        fun onClassSelected(clsId: String, stdId: String, studentName: String, img: String)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        Log.d("TAG", "initializeFlowCollectors: bind ")
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        val size = (mStudentList.size) + 1
        Log.d("TAG", "initializeFlowCollectors: ${size}")
        return (mStudentList.size + 1)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("TAG", "initializeFlowCollectors: onviewholder ")
        if (position == mStudentList.size) {
            Log.d("TAG", "initializeFlowCollectors: 0 ")
            holder.setAddStudent()
        } else {
            holder.setStudent(mStudentList[position])
        }

    }

    fun setStudents(students: List<Student>) {
        mStudentList = students
        Log.d("TAG", "setStudents:${mStudentList} ")
        notifyDataSetChanged()
    }

    fun setId(classId: String) {
        this.classId = classId
        notifyDataSetChanged()
    }

    fun setListener(listener: AddStudentAdapter.Listener) {
        mListener = listener
    }

    inner class ViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SuspiciousIndentation")
        fun setStudent(student: Student) {
            binding.imageStdProfile.setImageResource(
                student.img.toInt()
            )
            binding.txtStdName.text = student.studentName
            binding.cardAddNewStd.setOnClickListener {
                mListener?.onClassSelected(clsId = classId,student.studentId,student.studentName,student.img)
            }

        }

        fun setAddStudent() {
            binding.imageStdProfile.setImageResource(
                R.drawable.ic_add
            )
            binding.txtStdName.text = "Add Student"

            binding.cardAddNewStd.setOnClickListener {
                mListener?.onAddNewStudentClicked(clsId = classId)
            }
        }
    }


}


