package com.example.ipz_project_2.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ipz_project_2.data.AppDatabase
import com.example.ipz_project_2.R
import com.example.ipz_project_2.data.voicemessage.VoiceMessage
import com.example.ipz_project_2.data.voicemessage.VoiceMessageAdapter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class VoiceRecordingsListFragment : Fragment() {

    private lateinit var records: ArrayList<VoiceMessage>
    private lateinit var mAdapter: VoiceMessageAdapter
    private lateinit var recyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voice_recordings_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        records = ArrayList()
        mAdapter = VoiceMessageAdapter(records)

        recyclerview = view.findViewById(R.id.voice_recordings_recycler_view)

        recyclerview.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)

        }

        fetchAll()

    }




    @SuppressLint("NotifyDataSetChanged")
    private fun fetchAll() {
//        GlobalScope.launch {
//            records.clear()
//            var queryResult: LiveData<List<VoiceMessage>>? =
//                context?.let { AppDatabase.getInstance(it).voiceMessageDao.getAll() }
//            if (queryResult != null) {
//                records.addAll(queryResult)
//            }
//            mAdapter.notifyDataSetChanged()
//        }
    }
}