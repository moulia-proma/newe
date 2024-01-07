package com.example.classwave.presentation.page.student

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.R
import com.example.classwave.databinding.ItemAddClassBinding
import com.google.android.material.navigation.NavigationView


class JoinClassAdapter : RecyclerView.Adapter<JoinClassAdapter.ViewHolder>() {

    private var mClassList = listOf<com.example.classwave.presentation.page.teacher.Class>()
    private var mListener: Listener? = null

    interface Listener {
        fun onAddNewClassClicked()
        fun onClassSelected(cls: com.example.classwave.presentation.page.teacher.Class)

        fun onEditClassClicked(cls:com.example.classwave.presentation.page.teacher.Class)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAddClassBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mClassList.size + 1

    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == mClassList.size) {
            Log.d("_xyz", "onBindViewHolder: ok")
            holder.setAddClass()
        } else {
            holder.setClass(mClassList[position])
        }
    }

    fun setClasses(classes: List<com.example.classwave.presentation.page.teacher.Class>) {
        mClassList = classes
        Log.d("TAG", "getItemCount: ${mClassList}")
        notifyDataSetChanged()
    }



    fun setListener(listener: Listener) {
        mListener = listener
    }


    inner class ViewHolder(private val binding: ItemAddClassBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setClass(cls: com.example.classwave.presentation.page.teacher.Class) {
            binding.imageClsProfile.setImageResource(
                cls.img.toInt()
            )
            binding.txtClsName.text = cls.name
            binding.cardAddClass.setOnClickListener {
                mListener?.onClassSelected(cls = cls)
            }
            binding.imageViewEditClass.setOnClickListener {
                mListener?.onEditClassClicked(cls = cls)
            }
            binding.imageViewEditClass.visibility = View.VISIBLE
        }

        fun setAddClass() {
            binding.imageViewEditClass.visibility = View.INVISIBLE
            binding.imageClsProfile.setImageResource(
                R.drawable.ic_add
            )
            binding.txtClsName.text = "Join Class"

            binding.cardAddClass.setOnClickListener {
                mListener?.onAddNewClassClicked()
            }

        }
    }


}


