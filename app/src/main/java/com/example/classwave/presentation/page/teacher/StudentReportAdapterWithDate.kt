package com.example.classwave.presentation.page.teacher

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.classwave.databinding.ItemStdReportInDetailsWithDateBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import java.time.LocalDate
import kotlin.math.min


class StudentReportAdapterWithDate : RecyclerView.Adapter<StudentReportAdapterWithDate.ViewHolder>() {

    var dateShowed: MutableList<String> = arrayListOf()
    private var mark: List<Marks> = listOf()
    private lateinit var context: Context

    private lateinit var date: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStdReportInDetailsWithDateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return min(mark.size, 1)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewMarks(mark[position])
    }

    fun setMarks(mark: List<Marks>, context: Context, date: LocalDate) {
        this.mark = mark
        this.date=date.toString()
        this.context = context
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemStdReportInDetailsWithDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun viewMarks(marks: Marks) {
            //val found = dateShowed.contains(marks.date)
            Log.d("_xyz", "viewMarks:${date} ${marks.date} ")
                val layoutManager2 = FlexboxLayoutManager(context)
                layoutManager2.flexDirection = FlexDirection.ROW
                layoutManager2.justifyContent = JustifyContent.FLEX_START
                binding.textMarksDate.text = marks.date
                val childAdapter = StudentReportAdapter(mark, date)
                binding.recyclerViewMarksDetails.layoutManager = layoutManager2
                binding.recyclerViewMarksDetails.adapter = childAdapter
        }
    }
}

data class DateWiseMark(
    var date: String,
    var markList : List<Marks>
)


val data = listOf<DateWiseMark>()


