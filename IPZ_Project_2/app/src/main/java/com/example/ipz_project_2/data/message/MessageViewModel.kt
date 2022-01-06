package com.example.ipz_project_2.data.message

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.ipz_project_2.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MessageViewModel(application: Application):AndroidViewModel(application) {
//
//    val readAllData: LiveData<List<TextMessage>>
//    private val repository: MessageRepository
//
//    init{
//        val messageDao = AppDatabase.getDatabase(application).messageDao
//        repository = MessageRepository(messageDao)
//        readAllData = repository.getAllMessages
//    }
//
//    fun getUserMessages(userUID: String): LiveData<List<TextMessage>>{
//        return repository.getUserMessages(userUID)
//    }
//
//    fun addMessage(textMessage: TextMessage){
//        Log.d("CHATC", "add model")
//        viewModelScope.launch(Dispatchers.IO){
//            repository.addMessage(textMessage)
//        }
//    }
//
//    fun updateMessage(textMessage: TextMessage){
//        viewModelScope.launch(Dispatchers.IO){
//            repository.updateMessage(textMessage)
//        }
//    }
//
//    fun deleteMessage(textMessage: TextMessage){
//        viewModelScope.launch(Dispatchers.IO){
//            repository.deleteMessage(textMessage)
//        }
//    }
//
//    fun deleteAllMessages(textMessage: Array<out TextMessage>){
//        viewModelScope.launch(Dispatchers.IO){
//            repository.deleteAllMessages(textMessage)
//        }
//    }

}