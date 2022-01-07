package com.example.ipz_project_2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ipz_project_2.databinding.FragmentLogInBinding
import com.example.ipz_project_2.databinding.FragmentOtpcodeBinding
import com.example.ipz_project_2.fragments.ChatFragmentArgs
import com.example.ipz_project_2.fragments.LogInFragment
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import kotlin.math.log


class otpcode : Fragment(), View.OnClickListener {


    private lateinit var confirmButton: Button
    private lateinit var codeText: EditText
    private lateinit var binding: FragmentOtpcodeBinding
    private lateinit var navController: NavController

    private val args by navArgs<otpcodeArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otpcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentOtpcodeBinding.bind(view)
        navController = Navigation.findNavController(view)

        confirmButton = binding.otpButton
        codeText = binding.editTextOTPCode
        confirmButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            confirmButton.id -> verifyOTP()

        }
    }

    private fun verifyOTP() {
        Log.i("verifyOTP", " Called verifyOTP")
        val queue = Volley.newRequestQueue(this.activity)
        val url =
            "http://zenek.pythonanywhere.com/token?otp=" + codeText.text.toString().trim() + "&hashed=" +
                    args.hash

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                // Process the json
                try {
                    UpdateUI()
                } catch (e: Exception) {
                    Toast.makeText(
                        context, "Something went wrong while sending otp request, Try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }, {response->
                Log.e("getOTP", "Something went wrong,")
                val body = String(response.networkResponse.data)
                Log.d("getOTP", body)
                Toast.makeText(
                    context, "Could not verify OTP code, try again",
                    Toast.LENGTH_SHORT
                ).show()
            })
        queue.add(request)
    }

    fun UpdateUI() {
        Log.d("OTPCode","UpdateUI")

        navController.navigate(R.id.action_otpcode_to_newMessageFragment)
    }

    companion object {
    }
}