package com.example.classwave.presentation.page.parent

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemClassesBinding
import com.example.classwave.presentation.page.report.Repo0rtActivity


class StdClassListAdapter() : RecyclerView.Adapter<StdClassListAdapter.ViewHolder>() {

    private var classList = arrayListOf<Request>()
    private var mListener: StdClassListAdapter.Listener? = null
    private lateinit var context : Context

    interface Listener {
        fun onRequestClicked(clsId: String, type: String)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Log.d("_xyz", "onBindViewHolder: ${childList[position]}")
        holder.set(classList[position])

    }

    fun setChild(childList: ArrayList<Request>, context: Context?) {
        classList = childList
        if (context != null) {
            this.context = context
        }
        //Log.d("_xyz", "setChild: ${teacherList.size}")
        notifyDataSetChanged()
        //Log.d("_xyz", "setChild: ${childList.size}")
    }

    fun setListener(listener: StdClassListAdapter.Listener) {
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StdClassListAdapter.ViewHolder {
        val binding = ItemClassesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return classList.size

    }

    inner class ViewHolder(private val binding: ItemClassesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun set(cls: Request) {
            if(cls.state != "rejected" ){
                binding.txtClsName.text = cls.clsName
                binding.imageClsProfile.setImageResource(cls.clsImage.toInt())
                binding.textViewStatus.visibility = View.VISIBLE
                binding.textViewStatus.text = cls.state
            }
            binding.cardAddClass.setOnClickListener {
                if(cls.state != "pending" && cls.state != "viewed"  ){
                    val intent = Intent(context, Repo0rtActivity::class.java)
                    intent.putExtra("classId", cls.clsId)
                    intent.putExtra("student_id", cls.stdId)
                    context.startActivity(intent)
              }
            }

       /*     binding.cardAddClass.setOnLongClickListener {
                if (binding.imageViewEditClass.visibility == View.INVISIBLE) {
                    binding.imageViewEditClass.visibility = View.VISIBLE
                  ////  mListener?.onRequestClicked(cls.classId, "add")
                } else {
                    binding.imageViewEditClass.visibility = View.INVISIBLE
                   // mListener?.onRequestClicked(cls.classId, "remove")
                }


                true
            }*/


            /*  binding.textViewName.setOnClickListener {
                  mListener?.onTeacherClicked(tcr)

              }*/
            /*   binding.textViewName.text = tcr.name*/
            //   binding.textViewName.text = child.stdName
            // binding.shapableImageViewStdPhoto.setImageResource(child.stdImage.toInt())
        }


    }


}