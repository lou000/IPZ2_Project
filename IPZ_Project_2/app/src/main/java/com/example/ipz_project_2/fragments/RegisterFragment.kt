package com.example.ipz_project_2.fragments

import RSAEncoding
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ipz_project_2.R
import com.example.ipz_project_2.User
import com.example.ipz_project_2.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.math.BigInteger
import java.security.MessageDigest
import com.example.ipz_project_2.data.chatmessage.AppViewModel
import com.example.ipz_project_2.data.chatmessage.AppViewModelFactory
import com.example.ipz_project_2.data.chatmessage.ChatMessageApplication
import com.google.firebase.database.*
import java.util.*


fun sha_256(input: String): String {
    val md = MessageDigest.getInstance("SHA-1")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

class RegisterFragment : Fragment(R.layout.fragment_register), View.OnClickListener {

    data class UserFB(
        val username: String,
        val email: String,
        val phoneNumber: String,
        val publicKey: String
    )

    private lateinit var accountAlreadyCreated: TextView
    private lateinit var registerButton: Button
    private var hash: String? = null

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
    private lateinit var new_user: UserFB


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
        getIP()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentRegisterBinding.bind(view)
        navController = Navigation.findNavController(view)

        registerButton = binding.registerButton
        accountAlreadyCreated = binding.accountAlreadyCreatedTextview

        accountAlreadyCreated.setOnClickListener(this)
        registerButton.setOnClickListener(this)

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

    fun isValidPassword(password: String?): Boolean {
//        (?=.*[0-9])       # a digit must occur at least once
//        (?=.*[a-z])       # a lower case letter must occur at least once
//        (?=.*[A-Z])       # an upper case letter must occur at least once
//        (?=.*[@#$%^&+=])  # a special character must occur at least once you can replace with your special characters
//        (?=\\S+$)          # no whitespace allowed in the entire string
//        .{4,}             # anything, at least six places though
        password?.let {
            val passwordPattern =
                ".{6,}"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(password) != null
        } ?: return false
    }

    fun getUserHash(username: String, phoneNumber: String): String {
        val to_encode = username + phoneNumber
        val res = String(sha_256(to_encode).toByteArray())
        hash = res
        Log.i("hash", res)
        return res
    }

    private fun registerUser() {
        val username: String = binding.usernameRegister.text.toString().trim()
        val email: String = binding.emailRegister.text.toString().trim()
        val phoneNumber: String = binding.phoneRegister.text.toString().trim()
        val password: String = binding.passwordRegister.text.toString().trim()
        val newUserHash: String = getUserHash(username, phoneNumber)


        val keyPairGenerator = RSAEncoding()
        // Generate private and public key
        val privateKey: String = Base64.getEncoder().
        encodeToString(keyPairGenerator.privateKey.encoded)

        val publicKey:  String = Base64.getEncoder().encodeToString(keyPairGenerator.publicKey.encoded)

        new_user = UserFB(
            username,
            email,
            phoneNumber,
            publicKey
        )
        if (this.isValidPassword(password)) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener() { it ->
//                    val user = auth.currentUser
//                        ?: throw Exception("Firebase connection could not be established")
//                    var usr = User(username, email, phoneNumber, "")
//                    usr.hash = getUserHash(usr)
                    if (!it.isSuccessful) {
                        Log.d("Register", "User could not be registered")
                        navController.navigate(R.id.action_register_fragment_to_log_in_fragment)
                        Toast.makeText(requireActivity(), it.exception?.message, Toast.LENGTH_SHORT).show();
                    } else {
                        //user doesn't exist, lets register him
                        Log.d("Register", "createUserWithEmail:success")

                        updateUI(auth.currentUser, new_user, privateKey)
                    }
                }
        } else {
            Toast.makeText(
                context, "Password does not match standard",
                Toast.LENGTH_SHORT
            ).show()
            Log.d("Register", "Password does not match standard")
        }
    }


    private fun updateUI(user: FirebaseUser?, new_user: UserFB, privateKey: String) {

        val appViewModel: AppViewModel by activityViewModels() {
            AppViewModelFactory(
                (requireActivity().application as ChatMessageApplication).chatRepository,
                (requireActivity().application as ChatMessageApplication).contactRepository,
                (requireActivity().application as ChatMessageApplication).userRepository
            )
        }

        appViewModel.addUser(User(FirebaseAuth.getInstance().currentUser!!.uid,
            binding.emailRegister.text.toString().trim(),
            binding.phoneRegister.text.toString().trim(),
            privateKey))


        mDatabase.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(new_user)
        navController.navigate(R.id.action_register_fragment_to_newMessageFragment)
    }
}