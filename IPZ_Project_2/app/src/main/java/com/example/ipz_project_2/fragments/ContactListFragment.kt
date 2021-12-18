package com.example.ipz_project_2.fragments

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ipz_project_2.R
import com.example.ipz_project_2.data.contact.Contact
import com.example.ipz_project_2.data.contact.ContactsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


const val PERMISSION_REQUEST_CONTACTS = 0

class ContactListFragment : Fragment(R.layout.fragment_contact_list),
ContactsAdapter.OnItemClickListener {

    // TODO:
    //  - All of this should work asynchronously and probably run at the start of the app
    //  - We should not read all users from database, but db functions are only in paid version
    //  - This will work but its laggy and idgaf
    private val contacts: MutableList<Contact> = mutableListOf()
    private val dbPhoneNr: MutableList<String> = mutableListOf()

    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var recyclerview: RecyclerView
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var database: DatabaseReference

        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        database = Firebase.database.reference
        database.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data1 in dataSnapshot.children) {
                    for(data2 in data1.children) {
                        if (data2.key.toString() == "phoneNumber") {
                            dbPhoneNr.add(data2.value.toString())
                            if(contacts.count()>0)
                                contacts.find { it.phoneNumber == data2.value.toString() }?.isInDatabase = true
                        }
                    }
                }
                contactsAdapter.setData(contacts.distinctBy { it.name })
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
        val view = inflater.inflate(R.layout.fragment_contact_list, container, false)
        contactsAdapter = ContactsAdapter(contacts, this, findNavController())
        recyclerview = view.findViewById(R.id.contacts_recycler_view)
        recyclerview.apply {
            adapter = contactsAdapter
            layoutManager = LinearLayoutManager(context)
        }

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { permissionGranted ->
                if (permissionGranted) {
                    //TODO: make it work async
                    loadContacts()
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
                loadContacts()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
        return view
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_new_message_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun loadContacts() {

        val resolver: ContentResolver? = activity?.contentResolver
        val cursor = resolver?.query(ContactsContract.Contacts.CONTENT_URI, null, null,
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
                                    val phoneNumValue = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                    contacts.add(Contact(name, phoneNumValue, dbPhoneNr.contains(phoneNumValue)))
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
        contactsAdapter.setData(contacts.distinctBy { it.name })
        Toast.makeText(
            activity,
            "Loaded ${contactsAdapter.itemCount} contacts.",
            Toast.LENGTH_LONG
        ).show()
    }
    override fun onItemClick(position: Int) {


    }
}



