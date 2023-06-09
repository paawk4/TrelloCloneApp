package com.pawka.trellocloneapp.presentation.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.pawka.trellocloneapp.R
import com.pawka.trellocloneapp.presentation.MainActivity
import com.pawka.trellocloneapp.utils.APP_ACTIVITY

open class BaseFragment(layout: Int) : Fragment(layout) {

    private lateinit var progressDialog: Dialog

    fun showProgressDialog() {
        progressDialog = Dialog(APP_ACTIVITY)
        progressDialog.setContentView(R.layout.progress_dialog)
        progressDialog.show()
    }

    fun hideProgressDialog() {
        progressDialog.dismiss()
    }
}