package com.example.ipz_project_2.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ipz_project_2.R
import com.example.ipz_project_2.data.chatmessage.AppViewModel
import com.example.ipz_project_2.data.contact.*
import com.example.ipz_project_2.databinding.FragmentAddNewContactBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.math.BigInteger
import java.security.MessageDigest

fun sha_256_(input: String): String {
    val md = MessageDigest.getInstance("SHA-1")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

class AddNewContactFragment : Fragment(R.layout.fragment_add_new_contact) {


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
    private val args by navArgs<AddNewContactFragmentArgs>()

//    private lateinit var appUser: User


    val appViewModel: AppViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_new_contact, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        navController = Navigation.findNavController(view)
        auth = Firebase.auth
        database = Firebase.database
        mDatabase = database.reference
        binding = FragmentAddNewContactBinding.bind(view)
        add_button = view.findViewById(R.id.add_new_contact_button)
        add_button.setOnClickListener { insertContactToDatabase() }

        super.onViewCreated(view, savedInstanceState)
    }

    class FirebaseContact(email: String, ip: String, phoneNumber: String, username: String)

    private fun insertContactToDatabase() {
        username = binding.usernameAddNewContact.text.toString().trim()
        phoneNumber = binding.phonenumberAddNewContact.text.toString().trim()

        mDatabase.child("user")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (ds: DataSnapshot in snapshot.children) {
                            if (ds.child("phoneNumber").value == phoneNumber) {
                                new_contact =
                                    Contact(username, phoneNumber, ds.key.toString())
                                appViewModel.addContact(new_contact)
                                appViewModel.getLatestId().observe(viewLifecycleOwner,
                                    Observer { it ->
                                        if (it != null) {
                                            appViewModel.joinUserContacts(
                                                UserContactsCrossRef(
                                                    args.userID,
                                                    it
                                                )
                                            )
                                            findNavController().navigate(R.id.action_addNewContactFragment_to_newMessageFragment)
                                        }

                                    })
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}


