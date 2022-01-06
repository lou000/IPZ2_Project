package com.example.ipz_project_2.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.ContactViewModel
import com.example.ipz_project_2.data.contact.ContactsAdapter
import com.example.ipz_project_2.R
import com.example.ipz_project_2.data.AppDatabase
import com.example.ipz_project_2.data.chatmessage.AppViewModel
import com.example.ipz_project_2.data.chatmessage.AppViewModelFactory
import com.example.ipz_project_2.data.chatmessage.ChatMessageRepository
import com.example.ipz_project_2.data.contact.ContactRepository
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class NewMessageFragment : Fragment(R.layout.fragment_new_message),
    ContactsAdapter.OnItemClickListener {

    private val appViewModel: AppViewModel by activityViewModels()
    {
        AppViewModelFactory(
            ChatMessageRepository(AppDatabase.getDatabase(requireContext()).chatMessageDao()),
            ContactRepository(AppDatabase.getDatabase(requireContext()).contactsDao())
        )
    }

    private lateinit var contacts: ArrayList<Contact>
    private lateinit var mAdapter: ContactsAdapter
    private lateinit var recyclerview: RecyclerView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            val view = inflater.inflate(R.layout.fragment_new_message, container, false)

        setHasOptionsMenu(true)
        contacts = ArrayList()
        mAdapter = ContactsAdapter(contacts, this,findNavController())
        recyclerview = view.findViewById(R.id.new_msg_recycler_view)
        recyclerview.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }


        appViewModel.allContacts.observe(viewLifecycleOwner, Observer { contacts ->
            mAdapter.setData(contacts)
        })
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if(FirebaseAuth.getInstance().currentUser == null){
            Navigation.findNavController(view).navigate(R.id.action_newMessageFragment_to_LogInFragment)
        }
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_message_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onItemClick(position: Int) {

    }



}