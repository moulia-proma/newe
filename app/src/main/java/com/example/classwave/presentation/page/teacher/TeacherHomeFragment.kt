package com.example.classwave.presentation.page.teacher

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.classwave.R
import com.example.classwave.databinding.FragmentTeacherHomeBinding
import com.example.classwave.presentation.page.signin.SignInActivity
import com.example.classwave.presentation.page.signout.SignoutViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TeacherHomeFragment : Fragment() {
   var imageList= arrayListOf<Int>(
       R.drawable.boy,
       R.drawable.bussiness_man,
       R.drawable.profile_boy,
       R.drawable.profile ,
       R.drawable.profile_man,
       R.drawable.user
   )
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    private val viewModel: SignoutViewModel by viewModels()


    private var _binding: FragmentTeacherHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        currentUser = auth.currentUser!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeacherHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = currentUser.uid

        binding.cardAttendance.setOnClickListener {
            viewModel.signOut()
            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START

        binding.recyclerViewStdInfo.layoutManager = layoutManager
        val adapter = context?.let { AddStudentAdapter(it, getStudents()) }
        binding.recyclerViewStdInfo.adapter = adapter
    }

    @SuppressLint("SuspiciousIndentation")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

//https://github.com/google/flexbox-layout