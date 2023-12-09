package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.content.Context
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


class AddClassAdapter(
    private val context: Context,
    private val stdList: ArrayList<Student>
) : RecyclerView.Adapter<AddClassAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        private val stdName: TextView = view.findViewById(R.id.txt_std_name)
//        private val stdImage: ImageView = view.findViewById(R.id.image_std_profile)
//        private val stdCard: MaterialCardView = view.findViewById(R.id.card_add_new_std)

        @SuppressLint("SuspiciousIndentation")
        fun setStdData(Std: Student, Position: Int, size: Int) {
//            if (Position != size) {
//                stdName.text = Std.name
//                stdImage.setImageResource(Std.img)
//            } else {
//                stdName.text = "Add"
//                stdCard.setOnClickListener {
//                    val dialog = AddNewStdDialog()
//                    val fm = (context as AppCompatActivity).supportFragmentManager;
//                    dialog.show(fm,"CreateStdDialog")
//
//                }
//
//            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_created_classroom, parent, false)

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


