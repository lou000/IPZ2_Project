package com.example.ipz_project_2.fragments

import android.app.Application
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
import com.example.ipz_project_2.R
import com.example.ipz_project_2.User
import com.example.ipz_project_2.data.AppDatabase
import com.example.ipz_project_2.data.chatmessage.AppViewModel
import com.example.ipz_project_2.data.chatmessage.AppViewModelFactory
import com.example.ipz_project_2.data.chatmessage.ChatMessageApplication
import com.example.ipz_project_2.data.chatmessage.ChatMessageRepository
import com.example.ipz_project_2.data.contact.*
import com.example.ipz_project_2.data.user.UserRepository
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class NewMessageFragment : Fragment(R.layout.fragment_new_message),
    ContactsAdapter.OnItemClickListener {


    private lateinit var contacts: ArrayList<Contact>
    private lateinit var mAdapter: ContactsAdapter
    private lateinit var recyclerview: RecyclerView
    private lateinit var appUser: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_message, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appViewModel: AppViewModel by activityViewModels() {
            AppViewModelFactory(
                (requireActivity().application as ChatMessageApplication).chatRepository,
                (requireActivity().application as ChatMessageApplication).contactRepository,
                (requireActivity().application as ChatMessageApplication).userRepository
            )
        }

        if (FirebaseAuth.getInstance().currentUser == null) {
            Navigation.findNavController(view)
                .navigate(R.id.action_newMessageFragment_to_LogInFragment)
        } else {

            appViewModel.user(FirebaseAuth.getInstance().currentUser!!.uid)
                .observe(viewLifecycleOwner, Observer { it ->
                    if (it != null) {
                        appUser = it
                        contacts = ArrayList()
                        mAdapter = ContactsAdapter(appUser.userId, appUser.privateKey, contacts, this, findNavController())
                        recyclerview = view.findViewById(R.id.new_msg_recycler_view)
                        recyclerview.apply {
                            adapter = mAdapter
                            layoutManager = LinearLayoutManager(context)
                        }

                        appViewModel.usersContacts(FirebaseAuth.getInstance().currentUser!!.uid)
                            .observe(viewLifecycleOwner, Observer { it ->

                                if (it != null) {
                                    if (it.isNotEmpty()) {
                                        var sortedContacts = it[0].contacts.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER,{ it.name }) )
                                        mAdapter.setData(sortedContacts)
                                    }
                                }
//                mAdapter.setData(contacts)
                            })
                    }
                })
        }


        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.log_off -> {
            FirebaseAuth.getInstance().signOut()
            Navigation.findNavController(view!!)
                .navigate(R.id.action_newMessageFragment_to_LogInFragment)
            true
        }
        R.id.add -> {
            val action = NewMessageFragmentDirections.actionNewMessageFragmentToAddNewContactFragment(appUser.userId)
            Navigation.findNavController(view!!).navigate(action)
            true
        }
        else -> {

            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_message_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onItemClick(position: Int) {
    }


}