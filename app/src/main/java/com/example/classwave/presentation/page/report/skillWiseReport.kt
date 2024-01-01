package com.example.classwave.presentation.page.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemReportDetailBinding
import com.example.classwave.databinding.ItemSkillReportBinding

class skillWiseReportAdapter:RecyclerView.Adapter<skillWiseReportAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): skillWiseReportAdapter.ViewHolder {
        val binding = ItemSkillReportBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: skillWiseReportAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
       return  0
    }
    inner class ViewHolder(private val binding: ItemSkillReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(report: Report) {

        }
    }


}