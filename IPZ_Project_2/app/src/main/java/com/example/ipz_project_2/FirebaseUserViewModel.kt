package com.example.ipz_project_2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser

class FirebaseUserViewModel : ViewModel() {
    private val mutableSelectedItem = MutableLiveData<FirebaseUser?>()
    val selectedItem: MutableLiveData<FirebaseUser?> get() = mutableSelectedItem

    fun selectItem(item: FirebaseUser?) {
        mutableSelectedItem.value = item
    }
}
