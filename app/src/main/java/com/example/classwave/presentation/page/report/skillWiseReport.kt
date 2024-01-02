package com.example.classwave.presentation.page.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemSkillReportBinding
import com.example.classwave.presentation.page.teacher.Class
import com.example.classwave.presentation.page.teacher.Skill

class skillWiseReportAdapter : RecyclerView.Adapter<skillWiseReportAdapter.ViewHolder>() {
    private var mList = listOf<Skill>()
    private var mListener: Listener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): skillWiseReportAdapter.ViewHolder {
        val binding = ItemSkillReportBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    interface Listener {

        fun onSkillSelected(skillId:String)
    }

    fun setListener(listener: Listener) {
        mListener = listener
    }

    override fun onBindViewHolder(holder: skillWiseReportAdapter.ViewHolder, position: Int) {

        holder.bindData(mList[position])
    }

    fun setClasses(classes: List<Skill>) {
        mList = classes
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(private val binding: ItemSkillReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(skill: Skill) {
            binding.cardDayReport.setOnClickListener {

                mListener?.onSkillSelected(skill.skillId)
            }
            binding.textSkillName.text = skill.name

        }
    }


}