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




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_log_in, container, false)



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentLogInBinding.bind(view)
        logInButton = binding.loginButtonRegisterLogIn
        logOffButton = binding.logOffButtonLogin
        logInButton.setOnClickListener(this)
        logOffButton.setOnClickListener(this)

        super.onViewCreated(view, savedInstanceState)


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