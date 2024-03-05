package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.R
import com.example.classwave.databinding.ItemNotificationBinding
import com.example.classwave.presentation.dialog.DetailsNotificationDialog
import com.example.classwave.presentation.page.parent.Request

class NewNotificationAdapter : RecyclerView.Adapter<NewNotificationAdapter.ViewHolder>() {
    private var notificationList = listOf<Request>()
lateinit var context :Context

    lateinit var classId: String
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {

        return notificationList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setStudent(notificationList[position])
    }

    fun setNotification(notification: ArrayList<Request>, context: Context?) {
        notificationList = notification
        if (context != null) {
            this.context= context
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SuspiciousIndentation", "ResourceAsColor")
        fun setStudent(student: Request) {
            binding.txtNotificationDate.text = student.time
            if (student.state == "pending") {
                binding.txtNotificationMsg.text =
                    "${student.parentName}(Parent) Wants to join your Class"

                binding.constraintLayout.background = ContextCompat.getDrawable(
                    binding.constraintLayout.context, R.color.secendryColor

                )
            } else {
                binding.txtNotificationMsg.text =
                    "${student.parentName}(Parent) Wants to join your Class"
               binding.constraintLayout.background = ContextCompat.getDrawable(
                    binding.constraintLayout.context, R.color.oldNotificationColor
                )
            }
            binding.constraintLayout.setOnClickListener {
              val dialog = DetailsNotificationDialog(student.parentName,student.parentId,student.parentPhoto,student.clsId,student.clsName,student.clsImage,student.stdId,student.stdName,student.stdImage,student.state)
                val ft = ( context as AppCompatActivity).supportFragmentManager.beginTransaction()
                dialog.show(ft, ContentValues.TAG)
            }

        }

    }


}


