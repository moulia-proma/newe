package com.example.classwave.presentation.page.student

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.R
import com.example.classwave.databinding.FragmentStudentHomeBinding
import com.example.classwave.databinding.NavDrawerBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.dialog.ClassInviteDialog
import com.example.classwave.presentation.dialog.EnterClassCodeDialog
import com.example.classwave.presentation.dialog.ShareProfileDialog
import com.example.classwave.presentation.page.main.MainActivity
import com.example.classwave.presentation.page.report.Repo0rtActivity
import com.example.classwave.presentation.page.teacher.Class
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StudentActivity : AppCompatActivity() {
    private val viewModel: StudentViewModel by viewModels()
    private lateinit var binding: FragmentStudentHomeBinding
    private lateinit var headerBinding: NavDrawerBinding
    private var joinClassAdapter = JoinClassAdapter()
    private var classId = ""
    var uName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        uName = intent.getStringExtra("name").toString()
        var uEmail = intent.getStringExtra("email").toString()

        super.onCreate(savedInstanceState)
        binding = FragmentStudentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*        val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
                val navController = navHostFragment.navController
                val navView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
                navView.setupWithNavController(navController)*/

        headerBinding =
            NavDrawerBinding.inflate(layoutInflater, binding.navigationViewDrawer, false)
        binding.navigationViewDrawer.addHeaderView(headerBinding.root)
        binding.drawerLayout.openDrawer(GravityCompat.START)

        headerBinding.recyclerView.adapter = joinClassAdapter
        joinClassAdapter.setListener(listener = object : JoinClassAdapter.Listener {
            override fun onAddNewClassClicked() {
                val dialog = EnterClassCodeDialog("", "", "", "", "", "create", uName)
                dialog.show(supportFragmentManager, EnterClassCodeDialog.TAG)
            }

            override fun onClassSelected(cls: com.example.classwave.presentation.page.teacher.Class) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
                viewModel.updateClass(cls = cls)
            }

            override fun onEditClassClicked(cls: Class) {
                var dialog = EnterClassCodeDialog(
                    cls.classId,
                    cls.teacherId,
                    cls.name,
                    cls.img,
                    cls.grade,
                    "update",
                    uName
                )
                dialog.show(supportFragmentManager, EnterClassCodeDialog.TAG)
            }
        })

        viewModel.fetchClassList(Firebase.auth.uid.toString())

        viewModel.findUserType(Firebase.auth.uid.toString())
        val popup = binding.imgViewMore
        val showPopup = PopupMenu(this, popup)
        showPopup.menu.add(Menu.NONE, 0, 0, "Share class")
        showPopup.menu.add(Menu.NONE, 1, 1, "Sign out")
        showPopup.gravity = Gravity.END
        popup.setOnClickListener { showPopup.show() }

        showPopup.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if (id == 0) {
                val dialog = ShareProfileDialog(Firebase.auth.uid.toString())
                dialog.show(supportFragmentManager, ClassInviteDialog.TAG)
            } else if (id == 1) {
                viewModel.signOut()
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            true
        }


        initializeFlowCollectors()
        registerListener()
    }


    private fun registerListener() {
        val dialogView =
            layoutInflater.inflate(R.layout.custom_dialog_student_profile_edit_layout, null)
        val btnSave = dialogView.findViewById<View>(R.id.btn_save) as Button
        val btnCancel = dialogView.findViewById<View>(R.id.image_view_cancel) as ImageView
        val editText = dialogView.findViewById<View>(R.id.edit_text_name) as EditText
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()


        binding.imageViewEdit.setOnClickListener {
            dialog.show()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            editText.setText(uName)
            viewModel.updateStudentName()
        }

        btnSave.setOnClickListener {
            dialog.dismiss()
        }
        binding.appBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        binding.cardViewReportBg.setOnClickListener {

            val intent = Intent(this, Repo0rtActivity::class.java)
            if (classId.isNotEmpty()) {
                intent.putExtra("classId", classId)
                intent.putExtra("student_id", Firebase.auth.uid.toString())
                startActivity(intent)
            }

        }

    }


    private fun initializeFlowCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.selectedClass.collectLatest { cls ->
                    Log.d("TAG", "initializeFlowCollectors: i am cls")
                    if (cls != null) {
                        binding.textClassName.visibility = View.VISIBLE
                        binding.textTeacherName.visibility = View.VISIBLE

                        binding.textClassName.text = cls.name
                        classId = cls.classId
                        binding.textTeacherName.text = cls.teacherName
                    } else {
                        Log.d("TAG", "initializeFlowCollectors: abc")
                        binding.textClassName.visibility = View.INVISIBLE
                        binding.textTeacherName.visibility = View.INVISIBLE

                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.classList.collectLatest {
                    it?.let {
                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                viewModel.showClasses(it.data)
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userType.collectLatest { user ->
                    if (user.isNotEmpty()) {
                        binding.textViewName.text = "Welcome, ${user[1]}"
                    } else {
                        binding.textViewName.text = "No Class"
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.newClassList.collectLatest {


                    it?.let {

                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                Log.d("TAG", "initializeFlowCollectors: ol ${it.data}")
                                showSuccessView(it.data)
                            }
                        }
                    }
                }
            }
        }

        /*
                     lifecycleScope.launch {
                         repeatOnLifecycle(Lifecycle.State.CREATED) {
                             viewModel.createStudent.collectLatest {
                                 it?.let {
                                     when (it) {
                                         is Resource.Error -> {}
                                         is Resource.Loading -> {
                                             binding.
                                         }
                                         is Resource.Success -> {
                                             showSuccessView(it.data)
                                         }
                                     }
                                 }
                             }
                         }
                     }*/

    }

    private fun showSuccessView(classList: List<Class>?) {
        headerBinding.progressBarClassLoading.visibility = View.INVISIBLE
        if (!classList.isNullOrEmpty()) {
            Log.d("TAG", " okok showSuccessView: ${classList[0]}")
            viewModel.updateClass(classList[0])

            joinClassAdapter.setClasses(classList)
            binding.textClassName.visibility = View.VISIBLE
            binding.textTeacherName.visibility = View.VISIBLE

            binding.textClassName.text = classList[0].name
            classId = classList[0].classId
            binding.textTeacherName.text = classList[0].teacherName

        } else {
            binding.textBy.text = "You haven't joined any classes!"

            viewModel.updateClass(null)
            joinClassAdapter.setClasses(listOf())
        }
    }


    private fun showSuccessViewClassList(classList: List<Class>?) {
        headerBinding.progressBarClassLoading.visibility = View.INVISIBLE
        if (!classList.isNullOrEmpty()) {
            viewModel.updateClass(classList[0])
            joinClassAdapter.setClasses(classList)
        } else {
            viewModel.updateClass(null)
            joinClassAdapter.setClasses(listOf())
        }
    }
}