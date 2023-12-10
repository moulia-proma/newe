package com.example.classwave.presentation.page.teacher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.classwave.R
import com.example.classwave.databinding.ActivityTeacherBinding
import com.example.classwave.databinding.StdudentProfileFromTcrSideLayoutBinding

class StdProfileViewActivity : AppCompatActivity() {
    private lateinit var binding: StdudentProfileFromTcrSideLayoutBinding
    var imageList = arrayListOf<Int>(
        R.drawable.boy,
        R.drawable.bussiness_man,
        R.drawable.profile_boy,
        R.drawable.profile,
        R.drawable.profile_man,
        R.drawable.user
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = StdudentProfileFromTcrSideLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = SkillsStudentAdapter(this, getStudents())
        binding.recyclerViewStdInfo.adapter = adapter

    }

    fun getStudents(): ArrayList<Student> {

        val Students: ArrayList<Student> = arrayListOf()

        Students.add(
            Student(
                "mak",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "malaika",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mina",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mikasa",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mizuka",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mifan",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mouli",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mou",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "maz",
                imageList.random()
            )

        )
        Students.add(
            Student(
                "mas",
                imageList.random()
            )

        )

        return Students

    }
}