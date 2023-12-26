package com.example.classwave.presentation.page.report

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemReportDetailBinding

class ReportChildAdapter : RecyclerView.Adapter<ReportChildAdapter.ViewHolder>() {

    private var mReports: MutableList<Report> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReportDetailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mReports.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(mReports[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setReports(reports: MutableList<Report>) {
        this.mReports = reports
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemReportDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(report: Report) {
            binding.textMarksDetails.text = "${report.marks} marks achieved on ${report.skillName}"
            binding.textDate.text = report.date
            binding.imageViewMarks.setImageResource(report.skillPhoto.toInt())
        }
    }


}