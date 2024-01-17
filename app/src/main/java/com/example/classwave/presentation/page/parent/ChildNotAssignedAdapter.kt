package com.example.classwave.presentation.page.parent

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.data.datasource.remote.model.UserItemResponse
import com.example.classwave.databinding.ItemNotAssignedChildListBinding

class ChildNotAssignedAdapter : RecyclerView.Adapter<ChildNotAssignedAdapter.ViewHolder>() {

    private var childList = arrayListOf<UserItemResponse>()
    private var mListener: ChildNotAssignedAdapter.Listener? = null


    interface Listener {
        fun onStudentDetailClicked(child: UserItemResponse)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Log.d("_xyz", "onBindViewHolder: ${childList[position]}")
        holder.set(childList[position])
    }

    fun setChild(childList: ArrayList<UserItemResponse>) {
        this.childList = childList
        Log.d("_xyz", "setChild: kdkkdk")
        notifyDataSetChanged()
    }

    fun setListener(listener: ChildNotAssignedAdapter.Listener) {
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChildNotAssignedAdapter.ViewHolder {
        val binding = ItemNotAssignedChildListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        Log.d("_xyz", "getItemCount:  ${childList.size}")
        return childList.size
    }

    inner class ViewHolder(private val binding: ItemNotAssignedChildListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun set(userItemResponse: UserItemResponse) {
            binding.textViewName.text = userItemResponse.name
            binding.textViewDetails.text = userItemResponse.email
            binding.btnAssignClass.text = "Click to view details"
            binding.btnAssignClass.setOnClickListener {
                mListener?.onStudentDetailClicked(userItemResponse)
            }
        }
        /*     fun notAssigned(child: Child) {
                 binding.btnAssignClass.setOnClickListener {
                     mListener?.onAssignClassClicked(child)
                 }
                 binding.textViewName.text = child.stdName
                 binding.textViewDetails.text = "Not Assigned to any classes yet"
     *//*            binding.shapableImageViewStdPhoto.setImageResource(child.stdImage.toInt())*//*
        }

        fun pending(child: Child) {
                  binding.btnAssignClass.visibility = View.GONE
                 binding.textViewDetails.text = "Your requst is pending"
        }

        fun assigned(child: Child) {
                  binding.textViewDetails.visibility = View.GONE
                  binding.btnAssignClass.text = "View report"
        }*/

    }


}