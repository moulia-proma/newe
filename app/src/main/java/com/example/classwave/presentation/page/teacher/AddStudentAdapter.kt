package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemStudentBinding

class AddStudentAdapter : RecyclerView.Adapter<AddStudentAdapter.ViewHolder>() {
    private var mStudentList = listOf<Student>()
    private var mListener: AddStudentAdapter.Listener? = null
    lateinit var classId: String

    interface Listener {
        /*        fun onAddNewStudentClicked(clsId: String)*/

        fun onClassSelected(
            clsId: String,
            stdId: String,
            studentName: String,
            img: String,

            )

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return mStudentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setStudent(mStudentList[position])
    }

    fun setStudents(students: List<Student>) {
        mStudentList = students
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
                mListener?.onClassSelected(
                    clsId = classId,
                    student.studentId,
                    student.studentName,
                    student.img
                )
            }

        }
    }


}


