package com.example.ipz_project_2.fragments

import RSAEncoding
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
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
import org.json.JSONObject
import java.util.*

import androidx.navigation.fragment.findNavController
import com.example.ipz_project_2.User
import com.example.ipz_project_2.data.chatmessage.AppViewModel
import com.example.ipz_project_2.data.chatmessage.AppViewModelFactory
import com.example.ipz_project_2.data.chatmessage.ChatMessageApplication
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.UserContactsCrossRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun hashMailPassword(mail: String, password: String): String {
    val to_encode = mail + password
    return String(sha_256(to_encode).toByteArray())
}

class LogInFragment : Fragment(R.layout.fragment_log_in), View.OnClickListener {

    private lateinit var loginButton: Button
    private lateinit var binding: FragmentLogInBinding
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    lateinit var mailPasswordHash: String

    val appViewModel: AppViewModel by activityViewModels()

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
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            loginButton.id -> LoginUser()
            binding.backToRegisterSpan.id -> findNavController().navigate(R.id.action_LogInFragment_to_register_fragment)
            binding.forgotPasswordSpan.id -> findNavController().navigate(R.id.action_LogInFragment_to_forgotPasswordFragment)
        }
    }


    fun LoginUser() {
        val email: String = binding.emailEdittextLogIn.text.toString().trim()
        val password: String = binding.passwordEdittextLogIn.text.toString().trim()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    verifyUser()
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
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

    private fun verifyUser() {
        val db = Firebase.database.reference
        val appViewModel: AppViewModel by activityViewModels() {
            AppViewModelFactory(
                (requireActivity().application as ChatMessageApplication).chatRepository,
                (requireActivity().application as ChatMessageApplication).contactRepository,
                (requireActivity().application as ChatMessageApplication).userRepository
            )
        }


        val uid = auth.currentUser?.uid
        if (uid != null) {
            appViewModel.user(uid).observe(viewLifecycleOwner, Observer { user ->
                if (user == null) {
                    CoroutineScope(Dispatchers.IO).launch {
                    db.child("user").child("$uid")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    Log.e("TESTINF", "$snapshot")
                                    val email: String =
                                        binding.emailEdittextLogIn.text.toString().trim()
                                    val keyPairGenerator = RSAEncoding()
                                    val privateKey: String = Base64.getEncoder()
                                        .encodeToString(keyPairGenerator.privateKey.encoded)
                                    val publicKey: String = Base64.getEncoder()
                                        .encodeToString(keyPairGenerator.publicKey.encoded)
                                    appViewModel.addUser(
                                        User(
                                            uid,
                                            email,
                                            snapshot.child("phoneNumber").value as String,
                                            privateKey
                                        )
                                    )
                                    db.child("user").child("$uid")
                                        .child("publicKey").setValue(
                                            publicKey
                                        )
                                    data class UpdateStruct(val publicKey: String)
                                    db.child("updates")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .setValue(UpdateStruct(publicKey))
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                }}
            })
        }
    }

//    private fun updateContactsPublicKey(publicKey: String, appViewModel: AppViewModel) {
//
//        data class UpdateStruct(val publicKey: String)
//        val db = Firebase.database.reference
//        appViewModel.usersContacts(FirebaseAuth.getInstance().currentUser!!.uid)
//            .observe(viewLifecycleOwner, { it ->
//                if (it != null) {
//                    if (it.isNotEmpty()) {
//                        for (contact in it[0].contacts){
//                            db.child("updates").child(contact.contactUid).child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(UpdateStruct(publicKey))
//                        }
//                    }
//                }
//            })
//        }


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
        val action = LogInFragmentDirections.actionLogInFragmentToOtpcode(hash = mailPasswordHash)
        navController.navigate(action)
    }

    companion object {
        private const val TAG = "LOGIN"
    }
}

