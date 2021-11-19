package com.example.ipz_project_2

import android.content.pm.PackageManager
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.room.Room

import com.google.android.material.navigation.NavigationBarView
import java.util.jar.Manifest


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navBar: BottomNavigationView = findViewById(R.id.bottom_nav_bar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navCont = navFragment?.findNavController()
        if (navCont != null) {
            NavigationUI.setupWithNavController(navBar, navCont)
        }

    }
}

