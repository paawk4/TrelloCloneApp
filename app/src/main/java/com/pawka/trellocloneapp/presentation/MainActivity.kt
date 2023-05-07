package com.pawka.trellocloneapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.pawka.trellocloneapp.R
import com.pawka.trellocloneapp.presentation.app_drawer.AppDrawer
import com.pawka.trellocloneapp.utils.Constants.APP_ACTIVITY
import com.pawka.trellocloneapp.utils.Constants.NAV_CONTROLLER

class MainActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var appDrawer: AppDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        APP_ACTIVITY = this
        initViews()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_main_activity)
        appDrawer = AppDrawer()
    }
}