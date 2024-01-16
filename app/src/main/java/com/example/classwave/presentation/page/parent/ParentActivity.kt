package com.example.classwave.presentation.page.parent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.classwave.databinding.ActivityParentBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.dialog.AddNewStdDialog
import com.example.classwave.presentation.dialog.EnterChildCodeDialog
import com.example.classwave.presentation.dialog.JoinClassParentDialog
import com.example.classwave.presentation.dialog.SearchTeacherDialog
import com.example.classwave.presentation.page.main.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ParentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityParentBinding
    private val viewModel: ParentViewModel by viewModels()
    private val clsId = ""
    private val notAssignedAdapter = ChildNotAssignedAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cardViewJoinClass.setOnClickListener {
            val dialog = JoinClassParentDialog()
            dialog.show(supportFragmentManager, AddNewStdDialog.TAG)
        }
        binding.cardViewAddChild.setOnClickListener {
            val dialog = EnterChildCodeDialog("")
            dialog.show(supportFragmentManager, EnterChildCodeDialog.TAG)

        }

        val popup = binding.imgViewMore
        val showPopup = PopupMenu(this, popup)
        showPopup.menu.add(Menu.NONE, 0, 0, "Share class")
        showPopup.menu.add(Menu.NONE, 1, 1, "Sign out")
        showPopup.gravity = Gravity.END
        popup.setOnClickListener { showPopup.show() }
        showPopup.setOnMenuItemClickListener { menuItem ->
            val id = menuItem.itemId
            if (id == 0) {

            } else if (id == 1) {
                viewModel.signOut()
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            true
        }
        viewModel.fetchChild(Firebase.auth.uid.toString())

        notAssignedAdapter.setListener(object : ChildNotAssignedAdapter.Listener {
            override fun onAssignClassClicked(child: Child) {
                val dialog = SearchTeacherDialog(child.stdId,child.parentId,child.parentName,child.stdName,child.parentPhoto,child.stdImage)
                dialog.show(supportFragmentManager, SearchTeacherDialog.TAG)
            }

        })



        binding.recyclerViewNotAssignedChildList.adapter = notAssignedAdapter

        initialFlowCollectors()

    }

    fun initialFlowCollectors() {
        lifecycleScope.launch {
            viewModel.childList.collectLatest {
                it?.let {
                    when (it) {
                        is Resource.Error -> {}
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            Log.d("_xyz", "initialFlowCollectors: ${it.data}")
                            it.data?.let { it1 -> notAssignedAdapter.setChild(it1) }

                        }
                    }
                }
            }
        }


    }


}