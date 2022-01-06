package com.example.ipz_project_2.data.contact

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.ipz_project_2.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactViewModel(application: Application):AndroidViewModel(application) {
//
//    val readAllData: LiveData<List<Contact>>
//    private val repository: ContactRepository
//
//    init{
//        val contactsDao = AppDatabase.getInstance(application).contactsDao
//        repository = ContactRepository(contactsDao)
//        readAllData = repository.getAllContacts
//    }
//
//    fun addContact(contact: Contact){
//        viewModelScope.launch(Dispatchers.IO){
//            repository.addContact(contact)
//        }
//    }
//
//    fun updateContact(contact: Contact){
//        viewModelScope.launch(Dispatchers.IO){
//            repository.updateContact(contact)
//        }
//    }
//
//    fun deleteContact(contact: Contact){
//        viewModelScope.launch(Dispatchers.IO){
//            repository.deleteContact(contact)
//        }
//    }
//
//    fun deleteAllContacts(contact: Array<out Contact>){
//        viewModelScope.launch(Dispatchers.IO){
//            repository.deleteAllContacts(contact)
//        }
//    }

}