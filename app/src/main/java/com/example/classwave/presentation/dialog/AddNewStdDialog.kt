package com.example.classwave.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.classwave.R
import com.google.android.material.button.MaterialButton


class AddNewStdDialog : DialogFragment() {
    private lateinit var btnAdd:MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       val view =  inflater.inflate(R.layout.dialog_add_new_std, container, false)
       btnAdd = view.findViewById(R.id.btn_add_std)
        btnAdd.setOnClickListener {
            dialog?.dismiss()
        }
        return view
    }


    companion object {
        const val TAG = "CreateStdialog"
    }
}