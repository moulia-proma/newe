package com.example.classwave.presentation.Snackbars

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.example.classwave.R
import com.example.classwave.databinding.CustomSnackbarLayoutBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class Snackbar {

    fun ShowSnack(context: Context, msg: String, action: MaterialButton) {

        val snackView = View.inflate(
            context,
            R.layout.custom_snackbar_layout,
            null
        )

        val snackBinding = CustomSnackbarLayoutBinding.bind(snackView)
        val snackBar = Snackbar.make(action, "", Snackbar.LENGTH_LONG)

        snackBar.apply {
            (view as ViewGroup).addView(snackBinding.root)
            snackBinding.txnSnackMsg.text = msg
            show()
        }

        snackBar.setBackgroundTint(Color.TRANSPARENT)

    }


}