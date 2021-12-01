package com.example.ipz_project_2.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.ipz_project_2.FirebaseUserViewModel
import com.example.ipz_project_2.R
import com.example.ipz_project_2.User
import com.example.ipz_project_2.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class RegisterFragment : Fragment(R.layout.fragment_register), View.OnClickListener {


    private lateinit var accountAlreadyCreated: TextView
    private lateinit var registerButton: Button

    private lateinit var username: String
    private lateinit var email: String
    private lateinit var phoneNumber: String
    private lateinit var password: String

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var mDatabase: DatabaseReference
    private lateinit var new_user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        auth = Firebase.auth
        database = Firebase.database
        mDatabase = database.getReference("user")
        binding = FragmentRegisterBinding.bind(view)
        navController = Navigation.findNavController(view)

        var currentUser = auth.currentUser

        if (currentUser != null) { //TODO Make function of this
//            navController.navigate(R.id.action_register_fragment_to_log_in_fragment)  //TODO
            Log.d("Auth", "Logged in")
        } else {
//            navController.navigate(R.id.action_register_fragment_to_log_in_fragment)//TODO
            Log.d("Auth", "Not logged in")
        }

        username = binding.usernameRegister.text.toString().trim()
        email = binding.emailRegister.text.toString().trim()
        phoneNumber = binding.phoneRegister.text.toString().trim()
        password = binding.passwordRegister.text.toString().trim()
        registerButton = binding.registerButton
        accountAlreadyCreated = binding.accountAlreadyCreatedTextview

        accountAlreadyCreated.setOnClickListener(this)
        registerButton.setOnClickListener(this)


    }

    private fun getIP(): String {
        //TODO IMPLEMENT (hardcoded for tests)
        return "SOME IP"
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            accountAlreadyCreated.id -> navController.navigate(R.id.action_register_fragment_to_log_in_fragment)
            registerButton.id -> registerUser()

        }
    }

    private fun registerUser() {
        username = binding.usernameRegister.text.toString().trim()
        email = binding.emailRegister.text.toString().trim()
        phoneNumber = binding.phoneRegister.text.toString().trim()
        password = binding.passwordRegister.text.toString().trim()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {

                    val user = auth.currentUser
                    if (user != null) {
                        new_user = User(username, email, phoneNumber, password, getIP(),user.uid)
                    }
                        updateUI(user)
                } else {
                    Log.d("Register", "New User Failed")
                    updateUI(null)

                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        val viewModel: FirebaseUserViewModel by activityViewModels()
        viewModel.selectItem(user)
        mDatabase.child(new_user.uid).setValue(new_user)
//        if(user!=null){
//            mDatabase.child(username).setValue(new_user)

            }

        }






