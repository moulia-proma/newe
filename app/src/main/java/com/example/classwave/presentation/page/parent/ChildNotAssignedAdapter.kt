package com.example.classwave.presentation.page.parent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemNotAssignedChildListBinding

class ChildNotAssignedAdapter : RecyclerView.Adapter<ChildNotAssignedAdapter.ViewHolder>() {

    private var childList = arrayListOf<Child>()
    private var mListener: ChildNotAssignedAdapter.Listener? = null


    interface Listener {
        fun onAssignClassClicked(child: Child)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Log.d("_xyz", "onBindViewHolder: ${childList[position]}")
        holder.set(childList[position])

    }

    fun setChild(childList: ArrayList<Child>) {
        this.childList = childList
        notifyDataSetChanged()
        //Log.d("_xyz", "setChild: ${childList.size}")
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
        // Log.d("_xyz", "getItemCount: ${childList.size}")
        return childList.size

    }

    inner class ViewHolder(private val binding: ItemNotAssignedChildListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun set(child: Child) {
            //  Log.d("_xyz", "set: $child")
            binding.btnAssignClass.setOnClickListener {
                mListener?.onAssignClassClicked(child)

            }
            binding.textViewName.text = child.stdName
            binding.shapableImageViewStdPhoto.setImageResource(child.stdImage.toInt())
        }


    }


}