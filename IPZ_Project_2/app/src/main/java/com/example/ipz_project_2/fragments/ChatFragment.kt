package com.example.ipz_project_2.fragments

import RSAEncoding
import android.content.Context
import android.content.pm.PackageManager
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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.ipz_project_2.SwipeToDeleteCallback
import com.example.ipz_project_2.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

const val ANDROID_PERMISSION_REQUEST_CODE__RECORD_AUDIO = 200
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
    ) {
        constructor() : this(-1, "", 0L)
    }


    private var _binding: FragmentChatBinding? = null


    private val binding get() = _binding!!
    private var permissions =
        arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private var permissionGrantedRecording = false

    private lateinit var chatMessages: MutableList<ChatMessage>
    private lateinit var chatMessageAdapter: ChatMessageAdapter
    private lateinit var recyclerview: RecyclerView
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var isRecording = false
    private var isRecorded = false
    private var isPaused = false
    private var timeWhenStopped: Long = 0
    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var filename = ""
    private lateinit var timer: Chronometer
    private val args by navArgs<ChatFragmentArgs>()
    private var userUID: String? = null
//    private var currentUser: User? = null
    private lateinit var childListener: ChildEventListener

    val appViewModel: AppViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()
//                recyclerview.smoothScrollToPosition(chatMessageAdapter.itemCount - 1)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        getMessages()

        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val view = binding.root

        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav_bar).visibility =
            View.GONE

        timer = binding.recordTimerChat

        chatMessages = mutableListOf()
        chatMessageAdapter = ChatMessageAdapter(chatMessages)

        recyclerview = binding.chatRecyclerView
        recyclerview.apply {
            adapter = chatMessageAdapter
            layoutManager = LinearLayoutManager(context)

        }
        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recyclerview.adapter as ChatMessageAdapter
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

        appViewModel.getUserMessages(args.userID,args.currentContact.contactId)
            .observe(viewLifecycleOwner, Observer { chatMessages ->
                chatMessageAdapter.setData(chatMessages)
                recyclerview.scrollToPosition(chatMessageAdapter.itemCount - 1)
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
            timeWhenStopped = 0
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
            val filePath = "$dirPath/$filename.mp3"
            mediaPlayer.apply {
                setDataSource(filePath)
                prepare()
            }
            binding

            mediaPlayer.start()


        }

        binding.btnDoneChat.setOnClickListener {
            if (isRecorded) {
                sendRecording(timeWhenStopped)
                binding.btnDoneChat.setImageResource(R.drawable.ic_done)
                isRecorded = false
                timeWhenStopped = 0
                hideButtons()
            } else {
                stopRecording()
                binding.btnDoneChat.setImageResource(R.drawable.ic_round_send_message)
                isRecorded = true
            }
        }

        binding.btnDeleteRecChat.setOnClickListener {

            if (isRecorded) {
                File("$dirPath/$filename.mp3").delete()
                binding.btnDoneChat.setImageResource(R.drawable.ic_done)
                isRecorded = false
                timeWhenStopped = 0
                hideButtons()
            } else {
                stopRecording()
                File("$dirPath/$filename.mp3").delete()
                isRecorded = false
                timeWhenStopped = 0
                hideButtons()
            }




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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { permissionGrantedRecording ->
                if (permissionGrantedRecording) {
                    Log.d(
                        "PermissionRecord",
                        "Permisison is (if true)" + permissionGrantedRecording
                    )

                } else {
                    Log.d(
                        "PermissionRecord",
                        "Permisison is (if false) request shoul lauch " + permissionGrantedRecording
                    )
                    Toast.makeText(
                        activity,
                        "The app was not allowed to read your contacts",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        when {
            context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    permissions[0]
                )
            } == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    permissions[0]
                )
            } == true -> {
                view?.let {
                    Snackbar.make(
                        it,
                        getString(R.string.permission_required),
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("DISMISS", View.OnClickListener {
                        // executed when DISMISS is clicked
                        System.out.println("Snackbar Set Action - OnClick.")
                    }).show()
                }

            }
            else -> {
                requestPermissionLauncher.launch(permissions[0])
            }
        }
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


    private fun createChatMessageOutgoing(type: Int, timestamp: Long): ChatMessage? {

        val currentUserUID = FirebaseAuth.getInstance().currentUser?.uid

        val time = getTimeFromTimestamp(timestamp)
        when (type) {
            TYPE_TEXT_OUTGOING -> {
                val textMsg: String = binding.chatTextInput.text.toString()
                return ChatMessage(
                    type,
                    textMsg,
                    timestamp,
                    time,
                    args.currentContact.contactId,
                    args.userID,
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
                    args.currentContact.contactId,
                    args.userID,
                    filename,
                    "$dirPath/$filename.mp3",
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
                val decodedMsg = RSAEncoding.decryptMessage(firebaseMessage.message, args.privateKey)
                return ChatMessage(
                    TYPE_TEXT_INCOMING,
                    decodedMsg,
                    firebaseMessage.timestamp,
                    getTimeFromTimestamp(firebaseMessage.timestamp),
                    idFrom,
                    args.userID,
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
                    idFrom,
                    args.userID,
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
        Log.d("FUCK", "${args.currentContact.publicKey}")
        val firebaseMessage =
            FirebaseMessage(TYPE_TEXT_FIREBASE,
                RSAEncoding.encryptMessage(binding.chatTextInput.text.toString(), args.currentContact.publicKey), timestamp)
        val referance = FirebaseDatabase.getInstance()
            .getReference("/chats/${args.currentContact.contactUid}/${FirebaseAuth.getInstance().currentUser?.uid}")
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
                .getReference("/chats/${FirebaseAuth.getInstance().currentUser?.uid}/${args.currentContact.contactUid}")

        childListener = reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val firebaseMessage = snapshot.getValue(FirebaseMessage::class.java)

                if (firebaseMessage != null) {
                    when (firebaseMessage.type) {
                        TYPE_TEXT_FIREBASE -> {
                            val textChatMessage =
                                createChatMessageIncoming(firebaseMessage, args.currentContact.contactId)
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
                                "in_${args.currentContact.contactUid}_${firebaseMessage.timestamp}.mp3"
                            val file: File = File(dirPath, filename)
                            reference.getFile(file).addOnSuccessListener {
                                val voiceChatMessage = createChatMessageIncoming(
                                    firebaseMessage,
                                    args.currentContact.contactId,
                                    filename,
                                    "$dirPath/$filename",
                                    getDuration(file)
                                )
                                if (voiceChatMessage != null) {
                                    appViewModel.addChatMessage(voiceChatMessage)
                                    snapshot.ref.removeValue()
                                    reference.delete()
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
            setOutputFile("$dirPath/$filename.mp3")
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


    private fun sendRecording(timer: Long) {

        val filePath = "$dirPath/$filename.mp3"
        val storage = Firebase.storage
        val storageRef = storage.reference
        var fileToUpload = Uri.fromFile(File(filePath))
        val audioRef =
            storageRef.child("audios/${args.currentContact.contactUid}/${fileToUpload.lastPathSegment}")
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
                saveVoiceMessageToDatabases(downloadUri,timer)   //TODO separate thread??
            } else {
                // Handle failures
                // ...
            }
        }
//        LocalDateTime.now(TimeZone.getDefault().toZoneId())
//            .format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    private fun saveVoiceMessageToDatabases(downloadUri: Uri?,timer: Long) {
        val filePath = "$dirPath/$filename.mp3"
        var timestamp: Long = Date().time

        val duration = durationVoiceMessage(timer)

        createChatMessageOutgoing(TYPE_VOICE_OUTGOING, timestamp)

        val voiceChatMessage = ChatMessage(
            TYPE_VOICE_OUTGOING,
            "",
            timestamp,
            getTimeFromTimestamp(timestamp),
            args.currentContact.contactId,
            args.userID,
            filename,
            filePath,
            duration
        )

        val firebaseVoiceMessage =
            FirebaseMessage(TYPE_VOICE_FIREBASE, downloadUri.toString(), timestamp)

        val reference = FirebaseDatabase.getInstance()
            .getReference("/chats/${args.currentContact.contactUid}/${FirebaseAuth.getInstance().currentUser?.uid}")
            .push()

        reference.setValue(firebaseVoiceMessage)
            .addOnSuccessListener {
                Log.d("CHATC", "Saved message: ${reference.key}")
            }
            .addOnFailureListener {
                Log.d("CHATC", "Write VoiceMessage to Firebase FAILED")
            }
        appViewModel.addChatMessage(voiceChatMessage)

//        timeWhenStopped = 0;
    }

    private fun getTimeFromTimestamp(timeStamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getDefault()
        calendar.timeInMillis = timeStamp
        val h = calendar.get(Calendar.HOUR_OF_DAY)
        val m = calendar.get(Calendar.MINUTE)
        val hStr = if (h < 10) "0$h" else "$h"
        val mStr = if (m < 10) "0$m" else "$m"
        return "$hStr:$mStr"
    }

    private fun durationVoiceMessage(recordingTime: Long): String {
        val minutes = recordingTime / 1000 / 60
        val seconds = recordingTime / 1000 % 60
        val mStr = if (minutes < 10) "0$minutes" else "$minutes"
        val sStr = if (seconds < 10) "0$seconds" else "$seconds"
        return "$mStr:$sStr"
    }

    private fun getDuration(file: File): String? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(file.absolutePath)
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.let {
            durationVoiceMessage(it.toLong())
        }
//        return Utils.formateMilliSeccond(durationStr!!.toLong())
    }

    private fun checkPermission(): Boolean {
        when {
            context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    permissions[0]
                )
            } == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                return true
            }
            activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    permissions[0]
                )
            } == true -> {
                view?.let {
                    Snackbar.make(
                        it,
                        getString(R.string.permission_required),
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("DISMISS", View.OnClickListener {
                        // executed when DISMISS is clicked
                        System.out.println("Snackbar Set Action - OnClick.")
                    }).show()
                }
                return false
            }
            else -> {
                requestPermissionLauncher.launch(permissions[0])
                return false
            }
        }
    }


    override fun onPause() {
        super.onPause()

        val reference =
            FirebaseDatabase.getInstance()
                .getReference("/chats/${FirebaseAuth.getInstance().currentUser?.uid}/${args.currentContact.contactUid}")

        reference.removeEventListener(childListener)
    }

}