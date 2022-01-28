package com.example.ipz_project_2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.ipz_project_2.R
import com.example.ipz_project_2.databinding.FragmentChatBinding
import com.example.ipz_project_2.databinding.FragmentForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth


class ForgotPasswordFragment : Fragment() {


    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val view = binding.root

        val submitButton = binding.forgotPasswordSubmitButton
        submitButton.setOnClickListener {
            val email = binding.forgotPasswordEmailEdittext.text.toString().trim(){it <= ' '}
            if (email.isEmpty()){
                Toast.makeText(requireActivity(), "Please enter email address.", Toast.LENGTH_LONG).show();
            }
            else{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    Toast.makeText(requireActivity(), "Email sent successfully to reset your password.", Toast.LENGTH_LONG).show();
                    findNavController().navigate(R.id.action_forgotPasswordFragment_to_LogInFragment)
                }
                        else{
                    Toast.makeText(requireActivity(), task.exception?.message, Toast.LENGTH_LONG).show();
                }
                    }
            }
        }

//        Toast.makeText(requireActivity(), it.exception?.message, Toast.LENGTH_SHORT).show();

        return view
    }

}
