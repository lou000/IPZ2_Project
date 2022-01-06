package com.example.ipz_project_2.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.ipz_project_2.FirebaseUserViewModel
import com.example.ipz_project_2.R
import com.example.ipz_project_2.User
import com.example.ipz_project_2.databinding.FragmentLogInBinding
import com.example.ipz_project_2.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LogInFragment : Fragment(R.layout.fragment_log_in), View.OnClickListener {


    private lateinit var logInButton: Button
    private lateinit var logOffButton: Button
    private lateinit var binding: FragmentLogInBinding



fun hashMailPassword(mail: String, password: String): String {
    val to_encode = mail + password
    val res = String(sha_256(to_encode).toByteArray())
    Log.i("hash", res)
    return res
}

class LogInFragment : Fragment(R.layout.fragment_log_in), View.OnClickListener {

    private lateinit var loginButton: Button
    private lateinit var binding: FragmentLogInBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    lateinit var mailPasswordHash : String

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

        binding = FragmentLogInBinding.bind(view)
        logInButton = binding.loginButtonRegisterLogIn
        logOffButton = binding.logOffButtonLogin
        logInButton.setOnClickListener(this)
        logOffButton.setOnClickListener(this)

        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLogInBinding.bind(view)
        navController = Navigation.findNavController(view)

        loginButton = binding.loginButtonRegisterLogIn
        loginButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        Log.d(TAG, "On click action")
        when (v!!.id) {

            loginButton.id -> LoginUser()

        }
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
                    val user = auth.currentUser
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
        mailPasswordHash =  hashMailPassword(mail,password)
        params["hashed"] = mailPasswordHash
        params["mail"] = mail
        Log.d("getOTP",params.toString())
        val jsonObject = JSONObject(params as Map<*, *>?)
        Log.d("getOTP",jsonObject.toString())
        val request = JsonObjectRequest(Request.Method.POST,url,jsonObject,
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

            }, {response ->
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

    private fun logIn(view:View){
        Log.d("CHATC","LOG_IN")
        val email = binding.emailEdittextLogIn.text.toString()
        val password = binding.passwordEdittextLogIn.text.toString()

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("CHATC", "signInWithEmail:success")
//                    val user = FirebaseAuth.getInstance().currentUser
//                    updateUI(user)
                    Navigation.findNavController(view).navigate(R.id.action_LogInFragment_to_newMessageFragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("CHATC", "signInWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?, new_user: User) {

//        val to_encode = new_user.username + new_user.phoneNumber
//        val res = String(sha_256(to_encode).toByteArray())
//        Log.e("hash", res)
//        mDatabase.child(res).setValue(new_user)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            logInButton.id -> logIn(v)
            logOffButton.id -> logOff()

        }
    }

    private fun logOff() {
        Log.d("CHATC","LOG_OFF")
        FirebaseAuth.getInstance().signOut()
    }

}
