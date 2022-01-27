package com.example.ipz_project_2.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ipz_project_2.MessageListAdapter
import com.example.ipz_project_2.R
import com.example.ipz_project_2.SwipeToDeleteCallback
import com.example.ipz_project_2.User
import com.example.ipz_project_2.data.chatmessage.AdapterMessage
import com.example.ipz_project_2.data.chatmessage.AppViewModel
import com.example.ipz_project_2.data.chatmessage.ChatMessage
import com.example.ipz_project_2.databinding.FragmentVoiceRecordingsListBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

import java.io.File
import java.util.*


private const val TYPE_TEXT_INCOMING: Int = 0
private const val TYPE_TEXT_OUTGOING: Int = 1
private const val TYPE_VOICE_INCOMING: Int = 2
private const val TYPE_VOICE_OUTGOING: Int = 3

class VoiceRecordingsListFragment : Fragment() {


    private var _binding: FragmentVoiceRecordingsListBinding? = null
    private val binding get() = _binding!!

    private lateinit var chatMessages: MutableList<AdapterMessage>
    private lateinit var messageListAdapter: MessageListAdapter
    private lateinit var recyclerview: RecyclerView
    private lateinit var appUser: User

    val appViewModel: AppViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentVoiceRecordingsListBinding.inflate(inflater, container, false)
        val view = binding.root

        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_bar).visibility =
            View.GONE

        appViewModel.user(FirebaseAuth.getInstance().currentUser!!.uid)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->
                if (it != null) {
                    appUser = it

                    chatMessages = mutableListOf()
                    messageListAdapter = MessageListAdapter(chatMessages)

                    recyclerview = binding.voiceRecordingsRecyclerView
                    recyclerview.apply {
                        adapter = messageListAdapter
                        layoutManager = LinearLayoutManager(context)

                    }


                    appViewModel.msgs(appUser.userId)
                        .observe(viewLifecycleOwner, androidx.lifecycle.Observer { chatMessages ->
                            messageListAdapter.setData(chatMessages)
                            recyclerview.scrollToPosition(messageListAdapter.itemCount - 1)
                        })

                    val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            val adapter = recyclerview.adapter as MessageListAdapter
                            val data = adapter.removeAt(viewHolder.adapterPosition)
                            appViewModel.delMsg(data.first)
                            if (data.third == 2 || data.third == 3) {
                                val file = File(data.second)
                                file.delete()
                            }
                        }
                    }
                    val itemTouchHelper = ItemTouchHelper(swipeHandler)
                    itemTouchHelper.attachToRecyclerView(recyclerview)

                }
            })



        return view
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_bar).visibility =
            View.VISIBLE
        _binding = null
    }

}