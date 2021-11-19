package com.example.ipz_project_2

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class VoiceRecordingsListFragment : Fragment() {

    private lateinit var records: ArrayList<VoiceMessage>
    private lateinit var mAdapter: VoiceRecordingsAdapter
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
        mAdapter = VoiceRecordingsAdapter(records)

        recyclerview = view.findViewById(R.id.voice_recordings_recycler_view)

        recyclerview.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)

        }

        fetchAll()

    }


    @SuppressLint("NotifyDataSetChanged")
    private fun fetchAll() {
        GlobalScope.launch {
            records.clear()
            var queryResult: List<VoiceMessage>? =
                context?.let { AppDatabase.getInstance(it).voiceMessageDao.getAll() }
            if (queryResult != null) {
                records.addAll(queryResult)
            }
            mAdapter.notifyDataSetChanged()
        }
    }
}