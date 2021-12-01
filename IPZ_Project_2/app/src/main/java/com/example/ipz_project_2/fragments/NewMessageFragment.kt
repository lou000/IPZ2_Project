package com.example.ipz_project_2.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.ContactViewModel
import com.example.ipz_project_2.data.contact.ContactsAdapter
import com.example.ipz_project_2.R
import java.util.ArrayList

class NewMessageFragment : Fragment(R.layout.fragment_contact_list),
    ContactsAdapter.OnItemClickListener {

    private lateinit var mContactViewModel: ContactViewModel
    private lateinit var contacts: ArrayList<Contact>
    private lateinit var mAdapter: ContactsAdapter
    private lateinit var recyclerview: RecyclerView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)

        setHasOptionsMenu(true)
        contacts = ArrayList()
        mAdapter = ContactsAdapter(contacts, this,findNavController())
        recyclerview = view.findViewById(R.id.contacts_recycler_view)
        recyclerview.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }

        mContactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)
        mContactViewModel.readAllData.observe(viewLifecycleOwner, Observer { contacts ->
            mAdapter.setData(contacts)
        })
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_message_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onItemClick(position: Int) {

    }

}