package com.example.classwave.presentation.page.parent

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemClassesBinding
import com.example.classwave.presentation.page.teacher.Class

class ClassListAdapter : RecyclerView.Adapter<ClassListAdapter.ViewHolder>() {

    private var teacherList = arrayListOf<Class>()
    private var mListener: ClassListAdapter.Listener? = null

    interface Listener {
        fun onRequestClicked(clsId: String, type: String, name: String, img: String)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Log.d("_xyz", "onBindViewHolder: ${childList[position]}")
        holder.set(teacherList[position])

    }

    fun setChild(childList: ArrayList<Class>) {
        teacherList = childList
        Log.d("_xyz", "setChild: ${teacherList.size}")
        notifyDataSetChanged()
        //Log.d("_xyz", "setChild: ${childList.size}")
    }

    fun setListener(listener: ClassListAdapter.Listener) {
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClassListAdapter.ViewHolder {
        val binding = ItemClassesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return teacherList.size

    }

    inner class ViewHolder(private val binding: ItemClassesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun set(cls: Class) {
            binding.txtClsName.text = cls.name
            binding.imageClsProfile.setImageResource(cls.img.toInt())

            binding.cardAddClass.setOnLongClickListener {
                if (binding.imageViewEditClass.visibility == View.INVISIBLE) {
                    binding.imageViewEditClass.visibility = View.VISIBLE
                    mListener?.onRequestClicked(cls.classId, "add",cls.name,cls.img)
                } else {
                    binding.imageViewEditClass.visibility = View.INVISIBLE
                    mListener?.onRequestClicked(cls.classId, "remove",cls.name,cls.img)
                }


                true
            }


            /*  binding.textViewName.setOnClickListener {
                  mListener?.onTeacherClicked(tcr)

              }*/
            /*   binding.textViewName.text = tcr.name*/
            //   binding.textViewName.text = child.stdName
            // binding.shapableImageViewStdPhoto.setImageResource(child.stdImage.toInt())
        }


    }


}