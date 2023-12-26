package com.example.classwave.presentation.page.report

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemStdReportInDetailsWithDateBinding


class ReportParentAdapter :
    RecyclerView.Adapter<ReportParentAdapter.ViewHolder>() {

    private var mReportSummaries: MutableMap<String, MutableList<Report>> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStdReportInDetailsWithDateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        Log.d("_xyz", "getItemCount: ${mReportSummaries.size}")
        return mReportSummaries.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("_xyz", "onBindViewHolder: ${mReportSummaries.keys.elementAt(position)}")
        val key = mReportSummaries.keys.elementAt(position)
        val value = mReportSummaries[key]
        holder.bindData(key, value)
    }

    fun setReports(reportSummaries: MutableMap<String, MutableList<Report>>) {
        Log.d("_xyz", "setReports: ${reportSummaries.size}")
        mReportSummaries.clear()
        mReportSummaries.putAll(reportSummaries)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemStdReportInDetailsWithDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(date: String, reports: MutableList<Report>?) {
            binding.textMarksDate.text = date
            val childAdapter = ReportChildAdapter()
            binding.recyclerViewMarksDetails.adapter = childAdapter
            childAdapter.setReports(reports ?: mutableListOf())
        }

    }
}