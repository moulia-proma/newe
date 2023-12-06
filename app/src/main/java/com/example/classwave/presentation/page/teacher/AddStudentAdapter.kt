package com.example.classwave.presentation.page.teacher

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.R

class AddStudentAdapter(
    private val context: Context,
    private val stdList: ArrayList<Student>
) : RecyclerView.Adapter<AddStudentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val stdPhoto: ImageView = view.findViewById(R.id.img_add_new_std)
        private val stdName: TextView = view.findViewById(R.id.txt_std_name)
        fun setStdData(Std: Student, Position: Int) {
            stdName.text = Std.name
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.new_student_view, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return stdList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentFruit = stdList[position]
        holder.setStdData(Std = currentFruit, Position = position)
    }
}