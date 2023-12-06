package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.classwave.R
import com.example.classwave.databinding.FragmentTeacherHomeBinding
import com.example.classwave.presentation.page.signIn.SignInActivity
import com.example.classwave.presentation.page.signUp.SignUpViewModel
import com.example.classwave.presentation.page.signout.SignoutViewModel
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TeacherHomeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var nameText: MaterialToolbar
    private lateinit var currentUser: FirebaseUser
    private val viewModel: SignoutViewModel by viewModels()
    private lateinit var  binding: FragmentTeacherHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        currentUser = auth.currentUser!!

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding = FragmentTeacherHomeBinding.inflate(layoutInflater)
        val view = binding.root
         binding.toolbar.title = currentUser.email
         binding.cardAttendance.setOnClickListener(){
             viewModel.signOut()
             val intent = Intent(context,SignInActivity::class.java)
             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
             startActivity(intent)

         }
        val std = getStudents()
        val adapter = context?.let { AddStudentAdapter(it,std) }
        binding.recyclerViewStdInfo.adapter = adapter
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.apply {
            flexDirection = FlexDirection.COLUMN_REVERSE
            flexWrap = FlexWrap.WRAP
            //justifyContent= JustifyContent.FLEX_END
            //alignContent = AlignContent.FLEX_START
        }
        binding.recyclerViewStdInfo.layoutManager=layoutManager

        return view

    }

    fun getStudents(): ArrayList<Student>{

        val Students : ArrayList<Student> = arrayListOf()
           Students.add(
               Student(
                      "maik",
                       "jjjjj"
               )

           )
        Students.add(
            Student(
                "mak",
                "jjjjj"
            )

        )
        Students.add(
            Student(
                "malaika",
                "jjjjj"
            )

        )
        Students.add(
            Student(
                "mina",
                "jjjjj"
            )

        )
        Students.add(
            Student(
                "mikasa",
                "jjjjj"
            )

        )
        Students.add(
            Student(
                "mizuka",
                "jjjjj"
            )

        )
        Students.add(
            Student(
                "mifan",
                "jjjjj"
            )

        )
        Students.add(
            Student(
                "mouli",
                "jjjjj"
            )

        )
        Students.add(
            Student(
                "mou",
                "jjjjj"
            )

        )
        Students.add(
            Student(
                "maz",
                "jjjjj"
            )

        )
        Students.add(
            Student(
                "mas",
                "jjjjj"
            )

        )

        return Students

    }



}

//https://github.com/google/flexbox-layout