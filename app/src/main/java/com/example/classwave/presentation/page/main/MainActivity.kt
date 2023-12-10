package com.example.classwave.presentation.page.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.databinding.ActivityMainBinding
import com.example.classwave.presentation.page.signin.SignInActivity
import com.example.classwave.presentation.page.teacher.TeacherActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.checkIsAuthenticated()

        initializeFlowCollectors()
        registerListener()
    }


    private fun registerListener() {
        binding.cardParent.setOnClickListener { }

        binding.cardStudent.setOnClickListener { }

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
                        val intent = Intent(this@MainActivity, TeacherActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        binding.groupPicker.visibility = View.VISIBLE
                        binding.progressCircular.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

}