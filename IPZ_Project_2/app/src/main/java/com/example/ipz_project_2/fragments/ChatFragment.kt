package com.example.ipz_project_2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.navigation.fragment.navArgs
import com.example.ipz_project_2.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class ChatFragment : Fragment() {

    private val args by navArgs<ChatFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_bar).visibility = View.GONE
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onDestroyView() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_bar).visibility = View.VISIBLE
        super.onDestroyView()
    }
}