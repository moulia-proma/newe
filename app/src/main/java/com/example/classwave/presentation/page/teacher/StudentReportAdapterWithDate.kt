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


class StudentReportAdapterWithDate :
    RecyclerView.Adapter<StudentReportAdapterWithDate.ViewHolder>() {

    var dateShowed: MutableList<String> = arrayListOf()
    private var mark: List<Marks> = listOf()
    private lateinit var context: Context

    private var date = listOf<LocalDate>()
    private var dateListSize: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStdReportInDetailsWithDateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        Log.d("_xyz", "getItemCount: ${date.size}")
        return date.size
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.viewMarks(date[position])
    }

    fun setMarks(mark: List<Marks>, context: Context, date: List<LocalDate>, dateListSize: Int) {
        this.mark = mark
        this.date = date
        this.context = context
        this.dateListSize = dateListSize

        for( i in mark.indices) {
            Log.d("_xyz", "parv: ${mark[i].date} |  ${mark[i].skillName} |  ${mark[i].marks}")
        }

        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemStdReportInDetailsWithDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun viewMarks(dat: LocalDate) {

            Log.d("_xyz", "viewMarks: ${dat}")

            Log.d("_pp", "viewMarks: count ${dat}")
            binding.textMarksDate.text = dat.toString()
            var i = 0
            val report = arrayListOf<Marks>()
            while (i < mark.size) {
                Log.d("_xyz", "viewMarks: $dat ${mark[i].date}")
                if (dat.toString() == mark[i].date) {
                    report.add(mark[i])
                }
                i++
            }
            Log.d("proma", "viewMarks: ${dat}   ${report.size}")

            val childAdapter = StudentReportAdapter()


            val layoutManager2 = FlexboxLayoutManager(context)
            layoutManager2.flexDirection = FlexDirection.ROW
            layoutManager2.justifyContent = JustifyContent.FLEX_START
            binding.recyclerViewMarksDetails.layoutManager = layoutManager2
            binding.recyclerViewMarksDetails.adapter = childAdapter
            childAdapter.setMarks(report)
        }
    }
}

data class DateWiseMark(
    var date: String,
    var markList: List<Marks>
)


val data = listOf<DateWiseMark>()


