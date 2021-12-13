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
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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
import java.lang.Exception
import java.math.BigInteger
import java.security.MessageDigest
import javax.xml.transform.ErrorListener
import com.android.volley.VolleyError
import com.android.volley.RequestQueue
import com.android.volley.Response


fun sha_256(input: String): String {
    val md = MessageDigest.getInstance("SHA-1")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

interface VolleyCallBack {
    fun onSuccess()
}

class RegisterFragment : Fragment(R.layout.fragment_register), View.OnClickListener {


    private lateinit var accountAlreadyCreated: TextView
    private lateinit var registerButton: Button

//    private lateinit var username: String
//    private lateinit var email: String
//    private lateinit var phoneNumber: String
//    private lateinit var password: String

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var mDatabase: DatabaseReference
    private lateinit var currentUser: FirebaseUser
    private lateinit var ip: String
//    private lateinit var new_user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        database = Firebase.database
        mDatabase = database.getReference("user")
        if (auth.currentUser != null) { //TODO Make function of this
            Log.d("Auth", "Logged in")
            currentUser = auth.currentUser!!
//            navController.navigate(R.id.contactListFragment)
//            navController.navigate(R.id.action_register_fragment_to_log_in_fragment)  //TODO

        } else {
            Log.d("Auth", "Not logged in")
//            navController.navigate(R.id.action_register_fragment_to_log_in_fragment)//TODO

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRegisterBinding.bind(view)
        navController = Navigation.findNavController(view)

        registerButton = binding.registerButton
        accountAlreadyCreated = binding.accountAlreadyCreatedTextview



        accountAlreadyCreated.setOnClickListener(this)
        registerButton.setOnClickListener(this)
        getIP()

    }

    private fun getIP() {
        Log.i("getIP", " Called getIp")
        val queue = Volley.newRequestQueue(this.activity)

        val urlip = "http://checkip.amazonaws.com/"
        val stringRequest = StringRequest(
            Request.Method.GET, urlip,
            { response ->
                Log.i("getIp", "got respone")
                Log.i("getIp", response)

                this.ip = response.filter { !it.isWhitespace() }
            }, null
        )
        queue.add(stringRequest)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            accountAlreadyCreated.id -> navController.navigate(R.id.action_register_fragment_to_log_in_fragment)
            registerButton.id -> registerUser()

        }
    }

    private fun registerUser() {
        val username: String = binding.usernameRegister.text.toString().trim()
        val email: String = binding.emailRegister.text.toString().trim()
        val phoneNumber: String = binding.phoneRegister.text.toString().trim()
        val password: String = binding.passwordRegister.text.toString().trim()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener() { it ->
                val user = auth.currentUser
                    ?: throw Exception("Firebase connection could not be established")

                // user already exists, lets login him
                if (!it.isSuccessful) {
                    Log.d("Register", "User exists")
                    updateUI(
                        user,
                        User(username, email, phoneNumber, ip)
                    )
                } else {
                    //user doesn't exist, lets register him
                    Log.d("Register", "createUserWithEmail:success")
                    updateUI(user, User(username, email, phoneNumber, ip))
                }
            }


    }


    private fun updateUI(user: FirebaseUser?, new_user: User) {
        //TODO: info about login/registration success
        val viewModel: FirebaseUserViewModel by activityViewModels()
        viewModel.selectItem(user)
        val to_encode = new_user.username + new_user.phoneNumber
        val res = String(sha_256(to_encode).toByteArray())
        Log.e("hash", res)
        mDatabase.child(res).setValue(new_user)
    }
}






