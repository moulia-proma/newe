package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.R
import com.example.classwave.databinding.NewStudentViewBinding


class AddStudentAdapter : RecyclerView.Adapter<AddStudentAdapter.ViewHolder>() {
    private var mStudentList = listOf<Student>()
    private var mListener: AddStudentAdapter.Listener? = null
    lateinit var classId: String

    interface Listener {
        fun onAddNewClassClicked(cls: String)
        /*    fun onClassSelected(cls: Class)*/
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NewStudentViewBinding.inflate(
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

    inner class ViewHolder(private val binding: NewStudentViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SuspiciousIndentation")
        fun setStudent(student: Student) {
            binding.txtStdName.text = student.studentName
            /*  binding.cardAddClass.setOnClickListener {
                  mListener?.onClassSelected(cls = cls)
              }*/
        }

        fun setAddStudent() {
            binding.imageStdProfile.setImageResource(
                R.drawable.ic_add
            )
            binding.txtStdName.text = "Add Student"


            binding.cardAddNewStd.setOnClickListener {
                mListener?.onAddNewClassClicked(cls = classId)
            }
        }
    }


}


