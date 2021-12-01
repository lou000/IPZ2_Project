package com.example.ipz_project_2.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.ContactViewModel
import com.example.ipz_project_2.R
import com.example.ipz_project_2.databinding.FragmentAddNewContactBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class AddNewContactFragment : Fragment(R.layout.fragment_add_new_contact) {

    private lateinit var mContactViewModel: ContactViewModel
    private lateinit var navController: NavController

    private lateinit var accountAlreadyCreated: TextView
    private lateinit var addContact: Button

    private lateinit var username: String
    private lateinit var phoneNumber: String

    private lateinit var binding: FragmentAddNewContactBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var mDatabase: DatabaseReference
    private lateinit var new_contact: Contact
    private lateinit var add_button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_new_contact, container, false)
        mContactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)
        auth = Firebase.auth
        database = Firebase.database
        mDatabase = database.getReference("user")
        binding = FragmentAddNewContactBinding.bind(view)
        add_button = view.findViewById(R.id.add_new_contact_button)
        add_button.setOnClickListener { inserContactToDatabase() }

        super.onViewCreated(view, savedInstanceState)
    }


    private fun inserContactToDatabase() {
        username = binding.usernameAddNewContact.text.toString().trim()
        phoneNumber = binding.phonenumberAddNewContact.text.toString().trim()
        new_contact = Contact(username, phoneNumber)
        mContactViewModel.addContact(new_contact)

        findNavController().navigate(R.id.action_addNewContactFragment_to_newMessageFragment)
    }
}


