package com.example.ipz_project_2.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.ipz_project_2.R
import com.example.ipz_project_2.databinding.FragmentLogInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.android.volley.Response
import org.json.JSONObject
import com.example.ipz_project_2.fragments.LogInFragmentDirections
import android.app.AlarmManager

import android.app.PendingIntent
import android.content.Context

import android.content.Intent
import androidx.navigation.fragment.findNavController
import kotlin.system.exitProcess
import com.example.ipz_project_2.MainActivity
import com.example.ipz_project_2.data.AppDatabase


fun hashMailPassword(mail: String, password: String): String {
    val to_encode = mail + password
    val res = String(sha_256(to_encode).toByteArray())
    Log.i("hash", res)
    return res
}

class LogInFragment : Fragment(R.layout.fragment_log_in), View.OnClickListener {

    private lateinit var loginButton: Button
//    private lateinit var logoutButton: Button
    private lateinit var binding: FragmentLogInBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    lateinit var mailPasswordHash: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLogInBinding.bind(view)
        navController = Navigation.findNavController(view)

        loginButton = binding.loginButtonRegisterLogIn
        loginButton.setOnClickListener(this)
        binding.backToRegisterSpan.setOnClickListener(this)
        binding.forgotPasswordSpan.setOnClickListener(this)
//        logoutButton = binding.logOffButtonLogin
//        logoutButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        Log.d(TAG, "On click action")
        when (v!!.id) {

            loginButton.id -> LoginUser()
            binding.backToRegisterSpan.id -> findNavController().navigate(R.id.action_LogInFragment_to_register_fragment)
            binding.forgotPasswordSpan.id -> findNavController().navigate(R.id.action_LogInFragment_to_forgotPasswordFragment)
//            logoutButton.id -> LogOutUser()

        }
    }

    fun LogOutUser() {
        Log.d(TAG, "Log out button clicked")
        FirebaseAuth.getInstance().signOut()
    }


    fun LoginUser() {
        val email: String = binding.emailEdittextLogIn.text.toString().trim()
        val password: String = binding.passwordEdittextLogIn.text.toString().trim()
        Log.d(TAG, "Login button clicked")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
//                    navController.navigate(LogInFragmentDirections.actionLogInFragmentToNewMessageFragment())
                    getOTP(email, password)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    private fun getOTP(mail: String, password: String) {
        Log.i("getOTP", " Called getOTP")
        val queue = Volley.newRequestQueue(this.activity)


        val url = "http://zenek.pythonanywhere.com/token"
        val params = HashMap<String, String>()
        mailPasswordHash = hashMailPassword(mail, password)
        params["hashed"] = mailPasswordHash
        params["mail"] = mail
        Log.d("getOTP", params.toString())
        val jsonObject = JSONObject(params as Map<*, *>?)
        Log.d("getOTP", jsonObject.toString())
        val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
            { response ->
                // Process the json
                try {
                    Log.d("getOTP", "Response received")
                    Log.d("getOTP", response.toString())
                    updateUI()
                } catch (e: Exception) {
                    Toast.makeText(
                        context, "Something went wrong while sending otp request, Try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }, { response ->
                Log.e("getOTP", "Something went wrong,")
                val body = String(response.networkResponse.data)
                Log.d("getOTP", body)
                // Error in request
//                textView.text = "Volley error: $it"
                Toast.makeText(
                    context, "Something went wrong while sending otp request, Try again.",
                    Toast.LENGTH_SHORT
                ).show()
            })
        queue.add(request)
    }

    private fun updateUI() {
        //TODO: change to fragment with otp code input
        val action = LogInFragmentDirections.actionLogInFragmentToOtpcode(hash = mailPasswordHash)
        navController.navigate(action)
    }

    companion object {
        private const val TAG = "LOGIN"

    }
}

