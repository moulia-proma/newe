package com.example.classwave.presentation.page.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.databinding.ActivityMainBinding
import com.example.classwave.presentation.page.parent.ParentActivity
import com.example.classwave.presentation.page.signin.SignInActivity
import com.example.classwave.presentation.page.student.StudentActivity
import com.example.classwave.presentation.page.teacher.TeacherActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private var uName = ""
    private var uEmail = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.checkIsAuthenticated()

        initializeFlowCollectors()
        registerListener()
    }


    private fun registerListener() {
        binding.cardParent.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("user_type", "parent")
            startActivity(intent)
            finish()
        }

        binding.cardStudent.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("user_type", "student")
            startActivity(intent)
            finish()
        }

        binding.cardTeacher.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("user_type", "teacher")
            startActivity(intent)
            finish()
        }
    }


    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isAuthenticated.collectLatest { isAuthenticated ->
                    if (isAuthenticated == null) return@collectLatest
                    if (isAuthenticated) {
                        Log.d("_pkz", "initializeFlowCollectors: proma")
                        Firebase.auth.uid?.let { viewModel.findUserType(it) }
                        // Log.d("_pt", "initializeFlowCollectors: mm $isAuthenticated ")

                    } else {
                        binding.groupPicker.visibility = View.VISIBLE
                        binding.progressCircular.visibility = View.INVISIBLE
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userType.collectLatest {
                    //  Log.d("_pt", "initializeFlowCollectors: ${it}")
                    if (it.isNotEmpty()) {
                        if (it[0] == "teacher") {
                            val intent = Intent(this@MainActivity, TeacherActivity::class.java)
                            startActivity(intent)
                        } else if (it[0] == "parent") {
                            val intent = Intent(this@MainActivity, ParentActivity::class.java)
                            startActivity(intent)

                        } else if (it[0] == "student") {
                            Log.d("pro", "initializeFlowCollectors: $uName")
                            val intent = Intent(this@MainActivity, StudentActivity::class.java)
                            intent.putExtra("name", it[1])
                            intent.putExtra("email", it[2])
                            startActivity(intent)

                        }
                    }


                }
            }
        }


    }

}