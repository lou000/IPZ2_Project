package com.example.ipz_project_2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation


class RegisterFragment : Fragment(), View.OnClickListener {


    private lateinit var accountAlreadyCreated: TextView
    private lateinit var password: TextView
    private lateinit var registerButton: Button
    private lateinit var email: TextView
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_register, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)


        accountAlreadyCreated = view.findViewById(R.id.account_already_created_textview)
        registerButton = view.findViewById(R.id.register_button_register)
        email = view.findViewById(R.id.email_edittext_register)

        accountAlreadyCreated.setOnClickListener(this)
        registerButton.setOnClickListener(this)

        Log.d("RegisterFragment", "Email is: " + email.text.toString())



    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.account_already_created_textview -> navController.navigate(R.id.action_register_fragment_to_log_in_fragment)
            R.id.register_button_register -> navController.navigate(R.id.action_register_fragment_to_fragment_contact_list) //TODO COS TU JEST NIE TKA BO NIE DZIALA

        }
    }
}