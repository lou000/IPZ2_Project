package com.example.ipz_project_2.fragments

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Chronometer
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ipz_project_2.R
import com.example.ipz_project_2.data.chatmessage.*
import com.example.ipz_project_2.databinding.FragmentChatBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import android.media.MediaMetadataRetriever


private const val TYPE_TEXT_INCOMING: Int = 0
private const val TYPE_TEXT_OUTGOING: Int = 1
private const val TYPE_VOICE_INCOMING: Int = 2
private const val TYPE_VOICE_OUTGOING: Int = 3
private const val TYPE_TEXT_FIREBASE: Int = 4
private const val TYPE_VOICE_FIREBASE: Int = 5


class ChatFragment : Fragment() {


    data class FirebaseMessage(
        var type: Int,
        var message: String,
        var timestamp: Long
    ) {constructor(): this(-1,"",0L) }


    private var _binding: FragmentChatBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    private lateinit var chatMessages: List<ChatMessage>
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private lateinit var recyclerview: RecyclerView

    private var isRecording = false
    private var isRecorded = false
    private var isPaused = false
    private var timeWhenStopped: Long = 0
    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var filename = ""
    private lateinit var timer: Chronometer
    private val args by navArgs<ChatFragmentArgs>()

    val appViewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root

        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_bar).visibility =
            View.GONE

        timer = binding.recordTimerChat

        chatMessages = listOf()
        chatMessageAdapter = ChatMessageAdapter(chatMessages)
        recyclerview = binding.chatRecyclerView
        recyclerview.apply {
            adapter = chatMessageAdapter
            layoutManager = LinearLayoutManager(context)
        }



        appViewModel.getUserMessages(args.currentContact.id)
            .observe(viewLifecycleOwner, Observer { chatMessages ->
                chatMessageAdapter.setData(chatMessages)
            })

// -------------------------------------------------------------------------------------------------------- CLICK LISTENERS START
        binding.chatTextInput.setOnClickListener {
            binding.chatTextInput.requestFocus()
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(binding.chatTextInput, 0)
            binding.chatTextInput.setText("")
        }

        binding.sendButtonChat.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch { sendTextMessage() }
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
            binding.chatTextInput.setText("")
            binding.chatTextInput.clearFocus()
        }

        binding.chatConstLayout.setOnClickListener {
            hideKeyboard(view)
        }

        binding.recordButtonChat.setOnClickListener {
            hideButtons()
        }

        binding.btnRecordChat.setOnClickListener {
            when {
                isPaused -> resumeRecording()
                isRecording -> pauseRecording()
//                else -> if (checkPermission()) startRecording()
                else -> startRecording()
            }
        }



        binding.btnPlayChat.setOnClickListener {
            val mediaPlayer = MediaPlayer()
            val filePath = "$dirPath$filename.mp3"
            mediaPlayer.apply {
                setDataSource(filePath)
                prepare()
            }
            mediaPlayer.start()
        }

        binding.btnDoneChat.setOnClickListener {
            if (isRecorded) {
                sendRecording()
                binding.btnDoneChat.setImageResource(R.drawable.ic_done)
                isRecorded = false
                hideButtons()
            } else {
                stopRecording()
                binding.btnDoneChat.setImageResource(R.drawable.ic_round_send_message)
                isRecorded = true
            }
        }

        binding.btnDeleteRecChat.setOnClickListener {
            File("$dirPath$filename.mp3").delete()
            timeWhenStopped = 0;
        }

// -------------------------------------------------------------------------------------------------------- CLICK LISTENERS END
        return view
    }

    private fun playVoiceMessage(filePath: String?) {
        val mediaPlayer = MediaPlayer()
        mediaPlayer.apply {
            setDataSource(filePath)
            prepare()
        }
        mediaPlayer.start()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMessages()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_bar).visibility =
            View.VISIBLE
        _binding = null
    }


    private fun createChatMessageOutgoing(type: Int, timestamp: Long): ChatMessage? {

        val time = getTimeFromTimestamp(timestamp)
        when (type) {
            TYPE_TEXT_OUTGOING -> {
                val textMsg: String = binding.chatTextInput.text.toString()
                return ChatMessage(
                    type,
                    textMsg,
                    timestamp,
                    time,
                    args.currentContact.id,
                    null,
                    null,
                    null,
                    null
                )
            }
            TYPE_VOICE_OUTGOING -> {
                return ChatMessage(
                    type,
                    "",
                    timestamp,
                    time,
                    args.currentContact.id,
                    null,
                    filename,
                    "$dirPath$filename.mp3",
                    durationVoiceMessage(timeWhenStopped)
                )
            }
            else -> return null
        }
    }

    private fun createChatMessageIncoming(
        firebaseMessage: FirebaseMessage,
        idFrom: Long, filename: String? = null, filePath: String? = null, duration: String? = null
    ): ChatMessage? {

        when (firebaseMessage.type) {
            TYPE_TEXT_FIREBASE -> {
                return ChatMessage(
                    TYPE_TEXT_INCOMING,
                    firebaseMessage.message,
                    firebaseMessage.timestamp,
                    getTimeFromTimestamp(firebaseMessage.timestamp),
                    null,
                    idFrom,
                    null,
                    null,
                    null
                )
            }
            TYPE_VOICE_FIREBASE -> {
                if (filePath != null) {
                }
                return ChatMessage(
                    TYPE_VOICE_INCOMING,
                    firebaseMessage.message,
                    firebaseMessage.timestamp,
                    getTimeFromTimestamp(firebaseMessage.timestamp),
                    null,
                    idFrom,
                    filename,
                    filePath,
                    duration
                )
            }
            else -> return null
        }
    }


    private fun sendTextMessage() {
        val timestamp = Date().time
        val textChatMessage = createChatMessageOutgoing(TYPE_TEXT_OUTGOING, timestamp)
        val firebaseMessage =
            FirebaseMessage(TYPE_TEXT_FIREBASE, binding.chatTextInput.text.toString(), timestamp)
        val referance = FirebaseDatabase.getInstance()
            .getReference("/chats/${args.currentContact.uid}/${Firebase.auth.currentUser!!.uid}")
            .push()

        referance.setValue(firebaseMessage)
            .addOnSuccessListener {
                Log.d("CHATC", "Saved message: ${referance.key}")
            }
            .addOnFailureListener {
                Log.d("CHATC", "Write Message to Firebase FAILED")
            }
        if (textChatMessage != null) {
            appViewModel.addChatMessage(textChatMessage)
        }


    }


    private fun getMessages() {
        val reference =
            FirebaseDatabase.getInstance()
                .getReference("/chats/${Firebase.auth.currentUser!!.uid}/${args.currentContact.uid}")

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val firebaseMessage = snapshot.getValue(FirebaseMessage::class.java)

                if (firebaseMessage != null) {
                    when (firebaseMessage.type) {
                        TYPE_TEXT_FIREBASE -> {
                            val textChatMessage =
                                createChatMessageIncoming(firebaseMessage, args.currentContact.id)
                            if (textChatMessage != null) {
                                appViewModel.addChatMessage(textChatMessage)
                                snapshot.ref.removeValue()
                            }
                        }
                        else -> {
                            val storage = Firebase.storage
                            val reference = storage.getReferenceFromUrl(
                                firebaseMessage.message
                            )

                            dirPath =
                                requireActivity().getExternalFilesDir("/")?.absolutePath.toString()
                            val filename =
                                "in_${args.currentContact.uid}_${firebaseMessage.timestamp}.mp3"
                            val file: File = File(dirPath, filename)
                            reference.getFile(file).addOnSuccessListener {
                                val voiceChatMessage = createChatMessageIncoming(
                                    firebaseMessage,
                                    args.currentContact.id,
                                    filename,
                                    "$dirPath/$filename",
                                    getDuration(file)
                                )
                                if (voiceChatMessage != null) {
                                    appViewModel.addChatMessage(voiceChatMessage)
                                    snapshot.ref.removeValue()
                                }
                            }.addOnFailureListener {
                                // Handle any errors //TODO
                            }
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("SNAPSHOT", " onChildChanged   ")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("SNAPSHOT", "  onChildRemoved  ")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("SNAPSHOT", " onChildMoved(   ")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("SNAPSHOT", "   onCancelled ")
            }

        })
    }


    fun hideKeyboard(view: View) {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun hideButtons() {
        if (binding.textLayoutChat.visibility == View.GONE) {
            binding.textLayoutChat.visibility = View.VISIBLE
            binding.voiceLayoutChat.visibility = View.GONE
        } else {
            binding.textLayoutChat.visibility = View.GONE
            binding.voiceLayoutChat.visibility = View.VISIBLE
        }
    }

    private fun startRecording() {
        timer.base = SystemClock.elapsedRealtime() - timeWhenStopped
        timer.start()
        dirPath = requireActivity().getExternalFilesDir("/")?.absolutePath.toString()
        filename = "audio_record_${Date().time}"
        recorder = MediaRecorder()
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$filename.mp3")
            prepare()
            start()
        }
        binding.recordButtonChat.setBackgroundResource(R.drawable.ic_pause)
        isRecording = true
        isPaused = false
    }

    private fun pauseRecording() {
        timer.stop()
        timeWhenStopped = SystemClock.elapsedRealtime() - timer.base;
        recorder.pause()
        isPaused = true
        binding.recordButtonChat.setBackgroundResource(R.drawable.ic_record)
    }

    private fun resumeRecording() {
        timer.base = SystemClock.elapsedRealtime() - timeWhenStopped
        timer.start()
        recorder.resume()
        isPaused = false
        binding.recordButtonChat.setBackgroundResource(R.drawable.ic_pause)
    }

    private fun stopRecording() {
        //TODO button stop recording should be disabled unless start recording was activated
        timer.stop()
        recorder.stop()
        recorder.reset()
        recorder.release()
//        recorder=null
        timer.base = SystemClock.elapsedRealtime();
        isRecording = false
        isPaused = false
//        durationVoiceMessage(timeWhenStopped)
//        sendRecording()
    }


    private fun sendRecording() {

        val filePath = "$dirPath$filename.mp3"
        val storage = Firebase.storage
        val storageRef = storage.reference
        var fileToUpload = Uri.fromFile(File(filePath))
        val audioRef = storageRef.child("audios/${fileToUpload.lastPathSegment}")
        val uploadTask = audioRef.putFile(fileToUpload)

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads  //TODO
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.//TODO
        }

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            audioRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                saveVoiceMessageToDatabases(downloadUri)   //TODO separate thread??
            } else {
                // Handle failures
                // ...
            }
        }
//        LocalDateTime.now(TimeZone.getDefault().toZoneId())
//            .format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun saveVoiceMessageToDatabases(downloadUri: Uri?) {
        val filePath = "$dirPath$filename.mp3"
        var timestamp: Long = Date().time
        val duration = durationVoiceMessage(timeWhenStopped)

        createChatMessageOutgoing(TYPE_VOICE_OUTGOING, timestamp)

        val voiceChatMessage = ChatMessage(
            TYPE_VOICE_OUTGOING,
            "",
            timestamp,
            getTimeFromTimestamp(timestamp),
            args.currentContact.id,
            null,
            filename,
            filePath,
            duration
        )
        val firebaseVoiceMessage =
            FirebaseMessage(TYPE_VOICE_FIREBASE, downloadUri.toString(), timestamp)

        val reference = FirebaseDatabase.getInstance()
            .getReference("/chats/${args.currentContact.uid}/${Firebase.auth.currentUser!!.uid}")
            .push()

        reference.setValue(firebaseVoiceMessage)
            .addOnSuccessListener {
                Log.d("CHATC", "Saved message: ${reference.key}")
            }
            .addOnFailureListener {
                Log.d("CHATC", "Write VoiceMessage to Firebase FAILED")
            }
        appViewModel.addChatMessage(voiceChatMessage)

        timeWhenStopped = 0;
    }

    private fun getTimeFromTimestamp(timeStamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        calendar.timeInMillis = timeStamp
        val h = calendar.get(Calendar.HOUR_OF_DAY)
        val m = calendar.get(Calendar.MINUTE)
        return "$h:$m"
    }

    private fun durationVoiceMessage(recordingTime: Long): String {
        val minutes = recordingTime / 1000 / 60
        val seconds = recordingTime / 1000 % 60
        return "$minutes:$seconds"
    }

    private fun getDuration(file: File): String? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(file.absolutePath)
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//        return Utils.formateMilliSeccond(durationStr!!.toLong())
    }

}