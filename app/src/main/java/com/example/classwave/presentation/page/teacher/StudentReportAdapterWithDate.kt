package com.example.classwave.presentation.page.teacher

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemStdReportInDetailsWithDateBinding
import java.time.LocalDate


class StudentReportAdapterWithDate : RecyclerView.Adapter<StudentReportAdapterWithDate.ViewHolder>() {

    var dateShowed: MutableList<String> = arrayListOf()
    private var mark: List<Marks> = listOf()
    private lateinit var context: Context

    private lateinit var date: String
    private var dateListSize:Int = 0
    private var mListener: Listener? = null

    interface Listener {
        fun ShowMarksDetails(
            date: String,
            marks: Marks,
            binding: ItemStdReportInDetailsWithDateBinding
        )

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStdReportInDetailsWithDateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return dateListSize
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewMarks(mark[position])
    }

    fun setMarks(mark: List<Marks>, context: Context, date: LocalDate,dateListSize:Int) {
        this.mark = mark
        this.date=date.toString()
        this.context = context
        this.dateListSize =dateListSize
        notifyDataSetChanged()
    }

    fun setListener(listener: StudentReportAdapterWithDate.Listener) {
        mListener = listener
    }
    inner class ViewHolder(private val binding: ItemStdReportInDetailsWithDateBinding) :
        RecyclerView.ViewHolder(binding.root) {


        @RequiresApi(Build.VERSION_CODES.O)
        fun viewMarks(marks: Marks) {
            mListener?.ShowMarksDetails(date,marks,binding)
            //val found = dateShowed.contains(marks.date)
            Log.d("_xyz", "viewMarks:${date} ${marks.date} ")

        }
    }
}

data class DateWiseMark(
    var date: String,
    var markList : List<Marks>
)


val data = listOf<DateWiseMark>()


