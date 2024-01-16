package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.R
import com.example.classwave.databinding.ItemOldNotificationBinding
import com.example.classwave.databinding.ItemStudentBinding
import com.example.classwave.presentation.page.parent.Request

class oldNotificationAdapter : RecyclerView.Adapter<oldNotificationAdapter.ViewHolder>() {
    private var notificationList = listOf<Request>()
    /*  private var mListener: NewNotificationAdapter.Listener? = null*/
    lateinit var classId: String

  /*  interface Listener {
        fun onAddNewStudentClicked(clsId: String)

        fun onClassSelected(
            clsId: String,
            stdId: String,
            studentName: String,
            img: String,

        )

    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOldNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        Log.d("_ok", "getItemCount: ${notificationList.size}")
        return notificationList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setStudent(notificationList[position])
    }

    fun setNotification(notification: ArrayList<Request>) {
        notificationList = notification
        notifyDataSetChanged()
    }

 /*   fun setListener(listener: oldNotificationAdapter.Listener) {
        mListener = listener
    }*/

    inner class ViewHolder(private val binding: ItemOldNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SuspiciousIndentation")
        fun setStudent(student: Request) {
            binding.txtNotificationMsg.text = student.parentId


            /*    binding.imageStdProfile.setImageResource(
                    student.img.toInt()
                )
                binding.txtStdName.text = student.studentName
                binding.cardAddNewStd.setOnClickListener {
                    mListener?.onClassSelected(clsId = classId,student.studentId,student.studentName,student.img)
                }*/

        }

/*
        fun setAddStudent() {
            binding.imageStdProfile.setImageResource(
                R.drawable.ic_add
            )
            binding.txtStdName.text = "Add Student"

            binding.cardAddNewStd.setOnClickListener {
                mListener?.onAddNewStudentClicked(clsId = classId)
            }
        }*/
    }


}


