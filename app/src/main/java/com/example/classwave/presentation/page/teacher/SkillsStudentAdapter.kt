package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.R
import com.example.classwave.presentation.dialog.AddNewStdDialog
import com.google.android.material.card.MaterialCardView


class SkillsStudentAdapter(
    private val context: Context,
    private val stdList: ArrayList<Student>
) : RecyclerView.Adapter<SkillsStudentAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val stdName: TextView = view.findViewById(R.id.txt_std_name)
        private val stdImage: ImageView = view.findViewById(R.id.image_std_profile)
        private val stdCard: MaterialCardView = view.findViewById(R.id.card_add_new_std)

        @SuppressLint("SuspiciousIndentation")
        fun setStdData(Std: Student, Position: Int, size: Int) {
            if (Position != size) {

                stdName.text = Std.studentName
               /* stdImage.setImageResource(Std.img)*/
                stdCard.setOnClickListener {
                  val dialog =  AlertDialog.Builder(context)
                    dialog.setTitle("Hiiii")
                   dialog.setView(R.layout.give_marks_layout)

                   //val view = LayoutInflater.from(context).createView()
                   dialog.show()

                }

            } else {
                stdName.text = "Add"
                stdCard.setOnClickListener {
                    Log.d("TAG", "setStdData:clicked ")
                  /*  val dialog = AddNewStdDialog(cls)*/
                    val fm = (context as AppCompatActivity).supportFragmentManager;
                  /*  dialog.show(fm,"CreateStdDialog")*/

                }

            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.new_student_view, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        val size = (stdList.size) + 1
        return size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listSize = stdList.size
        val currentStd: Student
        val itemPosition: Int
        if (position == listSize) {
            itemPosition = listSize
            currentStd = stdList[position - 1]
        } else {
            itemPosition = position
            currentStd = stdList[position]

        }

        holder.setStdData(Std = currentStd, Position = itemPosition, size = listSize)
    }
}


