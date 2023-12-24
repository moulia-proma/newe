package com.example.classwave.presentation.page.teacher

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemReportDetailBinding


class StudentReportAdapter(private val mark: List<Marks>, private val date: String) :
    RecyclerView.Adapter<StudentReportAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReportDetailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        Log.d("kk", "onBindViewHolder: ${mark.size}")
        return mark.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("kk", "onBindViewHolder: jjj")
        if (mark[position].date == date) {
            holder.viewMarks(mark[position])
        }

    }


    inner class ViewHolder(private val binding: ItemReportDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun viewMarks(marks: Marks) {
            binding.textMarksDetails.text = "${marks.marks} marks achived on ${marks.skillName}"

            binding.textDate.text = marks.date
            binding.imageViewMarks.setImageResource(marks.skillPhoto.toInt())


        }
    }


}


