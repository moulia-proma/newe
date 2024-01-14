package com.example.classwave.presentation.page.parent

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.data.datasource.remote.model.UserItemResponse
import com.example.classwave.databinding.ItemTeacherSearchResultBinding

class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private var teacherList = arrayListOf<UserItemResponse>()
    private var mListener: SearchResultAdapter.Listener? = null

    interface Listener {
        fun onTeacherClicked(tcr: UserItemResponse)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Log.d("_xyz", "onBindViewHolder: ${childList[position]}")
        holder.set(teacherList[position])

    }

    fun setChild(childList: ArrayList<UserItemResponse>) {
        teacherList = childList
        Log.d("_xyz", "setChild: ${teacherList.size}")
        notifyDataSetChanged()
        //Log.d("_xyz", "setChild: ${childList.size}")
    }

    fun setListener(listener: SearchResultAdapter.Listener) {
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultAdapter.ViewHolder {
        val binding = ItemTeacherSearchResultBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        Log.d("_xyz", "getItemCount: ${teacherList.size}")
        return teacherList.size

    }

    inner class ViewHolder(private val binding: ItemTeacherSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun set(tcr: UserItemResponse) {
            Log.d("_xyz", "set: jj")
            binding.textViewName.setOnClickListener {
                mListener?.onTeacherClicked(tcr)

            }
            binding.textViewName.text = tcr.name
          //   binding.textViewName.text = child.stdName
            // binding.shapableImageViewStdPhoto.setImageResource(child.stdImage.toInt())
        }


    }


}