package com.example.classwave.presentation.page.Attandance

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemAttendanceBinding
import com.example.classwave.presentation.page.report.Report

class AttendanceChilddapter : RecyclerView.Adapter<AttendanceChilddapter.ViewHolder>() {

    private var mReports: MutableList<Report> = mutableListOf()
    private var attendance = mutableMapOf<String, Boolean>()
    private var mListener: Listener? = null
    private var name = mutableMapOf<String, String>()
    private var profile = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAttendanceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    fun setData(name: MutableMap<String, String>, profile: MutableMap<String, String>) {
        Log.d("_xyz", "setData: $name")
        this.name = name
        this.profile = profile
    }

    override fun getItemCount(): Int {
        return mReports.size
    }

    interface Listener {
        fun onStudentClicked(value: Boolean, studentId: String)
    }

    fun setListener(listener: Listener) {
        Log.d("_atten", "setListener: tt")
        mListener = listener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(mReports[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setReports(reports: MutableList<Report>) {
        this.mReports = reports
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(report: Report) {
            binding.textViewName.text = name[report.stdId]
            profile[report.stdId]?.let { binding.imageViewProfile.setImageResource(it.toInt()) }
            attendance[report.stdId] = report.marks.toBoolean()

            if (attendance[report.stdId] == true) {
                binding.textViewAttendance.text = "1"

            } else {
                binding.textViewAttendance.text = "0"
            }
            binding.textViewName.setOnClickListener {

                attendance[report.stdId] = !attendance[report.stdId]!!

                if (attendance[report.stdId] == true) {
                    binding.textViewAttendance.text = "1"
                } else {
                    binding.textViewAttendance.text = "0"
                }
                attendance[report.stdId]?.let { it1 ->
                    Log.d("_atten", "bindData: not null")
                    mListener?.onStudentClicked(
                        it1, report.stdId
                    )
                }
            }

            attendance[report.stdId]?.let { it1 ->
                Log.d("_atten", "bindData: not null")
                mListener?.onStudentClicked(
                    it1, report.stdId
                )
            }


        }
    }


}