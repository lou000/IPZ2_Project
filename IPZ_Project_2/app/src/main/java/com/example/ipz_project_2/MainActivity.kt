package com.example.ipz_project_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var auth: FirebaseAuth
    private val viewModel: FirebaseUserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val toolbar = findViewById<Toolbar>(R.id.my_toolbar)
//        toolbar.inflateMenu(R.menu.menu_upper)


        auth = Firebase.auth
        var currentUser = auth.currentUser
        viewModel.selectedItem.observe(this, Observer { user ->
            currentUser = user
        })



        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment


        navController = navFragment.navController

        findViewById<BottomNavigationView>(R.id.bottom_nav_bar).setupWithNavController(navController)
        setSupportActionBar(toolbar)
        appBarConfiguration = AppBarConfiguration(navController.graph)
//        appBarConfiguration = AppBarConfiguration(setOf(R.id.login_fragment, R.id.register_fragment)) //TODO Usunac back button na wybranyh fragmentach??
        setupActionBarWithNavController(navController,appBarConfiguration)
//        NavigationUI.setupWithNavController(navBar, navController)



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_new_message_fragment,menu)
//        menuInflater.inflate(R.menu.menu_contact_llist_fragment,menu)
//        return super.onCreateOptionsMenu(menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}



