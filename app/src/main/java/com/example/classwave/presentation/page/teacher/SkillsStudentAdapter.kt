package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.R
import com.example.classwave.databinding.ItemSkillsBinding

class SkillsStudentAdapter : RecyclerView.Adapter<SkillsStudentAdapter.ViewHolder>() {
    private var mSkillList = listOf<Skill>()
    private var mListener: SkillsStudentAdapter.Listener? = null


    interface Listener {
        fun onAddNewSkillClicked()

        fun onSkillSelected(skillId: String, highestScore: String)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSkillsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        Log.d("TAG", "initializeFlowCollectors: bind ")
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        val size = (mSkillList.size) + 1
        Log.d("TAG", "initializeFlowCollectors: ${size}")
        return (mSkillList.size + 1)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("TAG", "initializeFlowCollectors: onviewholder ")
        if (position == mSkillList.size) {
            Log.d("TAG", "initializeFlowCollectors: 0 ")
            holder.setAddSkill()
        } else {
            holder.setSkill(mSkillList[position])
        }

    }

    fun setStudents(students: List<Skill>) {
        mSkillList = students
        Log.d("TAG", "setStudents:${mSkillList} ")
        notifyDataSetChanged()
    }

  /*  fun setId(classId: String) {
        this.classId = classId
        notifyDataSetChanged()
    }*/

    fun setListener(listener: SkillsStudentAdapter.Listener) {
        mListener = listener
    }

    inner class ViewHolder(private val binding: ItemSkillsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SuspiciousIndentation")
        fun setSkill(skill: Skill) {
            binding.imageStdProfile.setImageResource(
                skill.img.toInt()
            )
            binding.txtStdName.text = skill.name
            binding.cardAddNewStd.setOnClickListener {
                mListener?.onSkillSelected(skill.skillId,skill.highestScore)
            }

        }

        fun setAddSkill() {
            binding.imageStdProfile.setImageResource(
                R.drawable.ic_add
            )
            binding.txtStdName.text = "Add Skill"

            binding.cardAddNewStd.setOnClickListener {
                mListener?.onAddNewSkillClicked()
            }
        }
    }


}


