package com.example.ipz_project_2.fragments

import RSAEncoding
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
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
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.UserContactsCrossRef
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


fun sha_256(input: String): String {
    val md = MessageDigest.getInstance("SHA-1")
    return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}

const val PERMISSION_REQUEST_CONTACTS = 0
class RegisterFragment : Fragment(R.layout.fragment_register), View.OnClickListener {

    data class UserFB(
        val username: String,
        val email: String,
        val phoneNumber: String,
        val publicKey: String
    )

    var localContacts = ArrayList<Pair<String, String>>()
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
    val appViewModel: AppViewModel by activityViewModels() {
        AppViewModelFactory(
            (requireActivity().application as ChatMessageApplication).chatRepository,
            (requireActivity().application as ChatMessageApplication).contactRepository,
            (requireActivity().application as ChatMessageApplication).userRepository
        )
    }


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

        var requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { permissionGranted ->
            if (permissionGranted) {
                //TODO: make it work async
                localContacts = loadContacts()
            } else {
                Toast.makeText(
                    activity,
                    "The app was not allowed to read your contacts",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        when (PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.checkSelfPermission(
                requireContext().applicationContext,
                Manifest.permission.READ_CONTACTS
            ) -> {
                //TODO: make it work async
                localContacts = loadContacts()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
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
        val privateKey: String = Base64.getEncoder().encodeToString(keyPairGenerator.privateKey.encoded)

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
        appViewModel.addUser(User(FirebaseAuth.getInstance().currentUser!!.uid,
            binding.emailRegister.text.toString().trim(),
            binding.phoneRegister.text.toString().trim(),
            privateKey))

        importMatchingContacts()
        mDatabase.child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(new_user)
        navController.navigate(R.id.action_register_fragment_to_newMessageFragment)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CONTACTS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {
                Toast.makeText(
                    activity,
                    "The app was not allowed to read your contacts",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun importMatchingContacts()
    {
        mDatabase.child("user")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (ds: DataSnapshot in snapshot.children) {
                            for (pair in localContacts) {
                                if (ds.child("phoneNumber").value == pair.second) {
                                    val newContact = Contact(
                                        pair.first, pair.second, ds.key.toString(),
                                        ds.child("publicKey").value.toString()
                                    )
                                    Toast.makeText(
                                        activity,
                                        "Adding contact: ${pair.first}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    appViewModel.addContact(newContact)
                                    appViewModel.getLatestId().observe(viewLifecycleOwner,
                                        Observer { it ->
                                            if (it != null) {
                                                appViewModel.joinUserContacts(
                                                    UserContactsCrossRef(
                                                        appViewModel.user2(currentUser.uid).userId,
                                                        it
                                                    )
                                                )
                                            }
                                        })
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    @SuppressLint("Range")
    private fun loadContacts() : ArrayList<Pair<String, String>>{

        val contacts = ArrayList<Pair<String, String>>()
        val resolver: ContentResolver? = activity?.contentResolver
        val cursor = resolver?.query(
            ContactsContract.Contacts.CONTENT_URI, null, null,
            null)

        if (cursor != null) {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNumber = (cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

                    if (phoneNumber > 0) {
                        val cursorPhone = resolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                        if (cursorPhone != null) {
                            if(cursorPhone.count > 0) {
                                while (cursorPhone.moveToNext()) {
                                    val phoneNumValue = cursorPhone.getString(cursorPhone.getColumnIndex(
                                        ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    contacts.add(Pair(name, phoneNumValue))
                                }
                            }
                        }
                        cursorPhone?.close()
                    }
                }
            } else {
                Toast.makeText(
                    activity,
                    "No contacts dude",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        cursor?.close()
        return contacts
    }
}




