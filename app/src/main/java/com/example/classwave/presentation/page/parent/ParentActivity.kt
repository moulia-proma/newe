package com.example.classwave.presentation.page.parent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.classwave.data.datasource.remote.model.UserItemResponse
import com.example.classwave.databinding.ActivityParentBinding
import com.example.classwave.domain.model.Resource
import com.example.classwave.presentation.dialog.AddNewStdDialog
import com.example.classwave.presentation.dialog.ChildDetailsDialog
import com.example.classwave.presentation.dialog.EnterChildCodeDialog
import com.example.classwave.presentation.dialog.JoinChildsClassDialog
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
        viewModel.findUserType(Firebase.auth.uid.toString())
        super.onCreate(savedInstanceState)
        binding = ActivityParentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("_xyz", "onCreate: max")




        val popup = binding.imgViewMore
        val showPopup = PopupMenu(this, popup)
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
        Log.d("_hm", "onCreate: hahaha5")
        viewModel.fatchChildList(Firebase.auth.uid.toString())

        notAssignedAdapter.setListener(object : ChildNotAssignedAdapter.Listener {
       /*     override fun onAssignClassClicked(child: Child) {
                val dialog = SearchTeacherDialog(
                    child.stdId,
                    child.parentId,
                    child.parentName,
                    child.stdName,
                    child.parentPhoto,
                    child.stdImage
                )
                dialog.show(supportFragmentManager, SearchTeacherDialog.TAG)
            }*/

            override fun onStudentDetailClicked(child: UserItemResponse) {
                val dialog = ChildDetailsDialog(
                    child.name,
                    child.uPhoto,
                    child.parent,
                    child.uid,
                    child.email,
                    child.type,


                )
                dialog.show(supportFragmentManager,SearchTeacherDialog.TAG )

            }

        })
        binding.recyclerViewNotAssignedChildList.adapter = notAssignedAdapter

        initialFlowCollectors()
        registerListener()

    }


    fun registerListener(){
        binding.cardViewAddChild.setOnClickListener {
            viewModel.setUtypeNull()
            val dialog = EnterChildCodeDialog("")
            dialog.setListener(object : EnterChildCodeDialog.Listener {
                override fun onDialogClosed(dialog: EnterChildCodeDialog) {
                    Log.d("_xyz", "onDialogClosed: called")
                   viewModel.setNull()
                    dialog.dismiss()
                    Log.d("_hm", "onCreate: hahaha4")
                    viewModel.fatchChildList(Firebase.auth.uid.toString())
                }
            })
            viewModel.setNull()
            dialog.show(supportFragmentManager, EnterChildCodeDialog.TAG)
        }
        binding.cardViewJoinClass.setOnClickListener {
            viewModel.setNull()

            val dialog = JoinChildsClassDialog()
            dialog.show(supportFragmentManager, AddNewStdDialog.TAG)
        }
    }

    fun initialFlowCollectors() {
        Log.d("_hm", "onCreate: hahaha3")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.childList.collectLatest {
                    Log.d("_hm", "onCreate: hahaha2")
                    it?.let {
                        when (it) {
                            is Resource.Error -> {}
                            is Resource.Loading -> {}
                            is Resource.Success -> {
                                //Log.d("_hm", "onCreate: hahaha1 ${it.data}")
                                Log.d("_xyz", "initialFlowCollectors: iii ${it.data}")
                                 if(it.data != null){
                                     Log.d("_xyz", "initialFlowCollectors: ${it.data.size}")
                                     notAssignedAdapter.setChild(it.data)
                                 }else{
                                     Log.d("_xyz", "initialFlowCollectors: i m empty ")
                                     notAssignedAdapter.setChild(arrayListOf())
                                 }
                               // viewModel.setNull()

                            }
                        }
                    }
                }
            }
        }


    }

    override fun onPause() {
        super.onPause()
        initialFlowCollectors()
        Log.d("_d", "onPause: called on pAuse")
    }

    override fun onResume() {
        super.onResume()
       // initialFlowCollectors()
        Log.d("_d", "onPause: called on repAuse")
    }


}
