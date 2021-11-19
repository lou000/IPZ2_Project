package com.example.ipz_project_2

import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

const val ANDROID_PERMISSION_REQUEST_CODE__READ_CONTACTS = 100
const val ANDROID_PERMISSION_REQUEST_CODE__RECORD_AUDIO = 200

private var rootView: View? = null

class VoiceRecordingsFragment : Fragment(), View.OnClickListener {

    private var param1: String? = null
    private var param2: String? = null
    private var permissions =
        arrayOf(android.Manifest.permission.READ_CONTACTS, android.Manifest.permission.RECORD_AUDIO)
    private var permissionGrantedRecording = false
    private var permissionGrantedReadContacts = false

    private lateinit var recorder: MediaRecorder
    private var dirPath = ""
    private var filename = ""
    private var isRecording = false
    private var isPaused = false
    private lateinit var recordButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var listButton: ImageButton
    private lateinit var doneButton: ImageButton
    private lateinit var timer: Chronometer
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var timeWhenStopped: Long = 0
    private lateinit var navController: NavController

//    private var user = Contact("Maciek","123 456 789", "maciek@gmail.com") //TODO When Fragment Communication Interface will be implemented Contact object will be passed here ane username extracted


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }


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
                    permissions[1]
                )
            } == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    permissions[1]
                )
            } == true -> {
                rootView?.let {
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
                requestPermissionLauncher.launch(permissions[1])
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        rootView = inflater.inflate(R.layout.fragment_voice_recordings, container, false)
        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VoiceRecordingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionGrantedReadContacts = context?.let {
            ContextCompat.checkSelfPermission(
                it,
                permissions[0]
            )
        } == PackageManager.PERMISSION_GRANTED

        recordButton = view.findViewById(R.id.btnRecord)
        deleteButton = view.findViewById(R.id.btnDeleteRec)
        listButton = view.findViewById(R.id.btnList)
        doneButton = view.findViewById(R.id.btnDone)
        timer = view.findViewById(R.id.record_timer)

        recordButton.setOnClickListener(this)
        deleteButton.setOnClickListener(this)
        listButton.setOnClickListener(this)
        doneButton.setOnClickListener(this)

        navController = Navigation.findNavController(view)

    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnRecord -> when {
                isPaused -> resumeRecording()
                isRecording -> pauseRecording()
                else -> if (checkPermission()) startRecording()
            }
            R.id.btnDeleteRec -> {
                File("$dirPath$filename.mp3").delete()
                timeWhenStopped = 0;
            }
            R.id.btnList -> {
                navController.navigate(R.id.action_voiceRecordingsFragment_to_voiceRecordingsListFragment)
            }
            R.id.btnDone -> stopRecording()
        }
    }

    private fun pauseRecording() {
        timer.stop()
        timeWhenStopped = SystemClock.elapsedRealtime() - timer.base;
        recorder.pause()
        isPaused = true
        recordButton = rootView?.findViewById(R.id.btnRecord) as ImageButton
        recordButton?.setBackgroundResource(R.drawable.ic_record)

    }

    private fun resumeRecording() {
        timer.base = SystemClock.elapsedRealtime() - timeWhenStopped
        timer.start()
        recorder.resume()
        isPaused = false
        recordButton = rootView?.findViewById(R.id.btnRecord) as ImageButton
        recordButton?.setBackgroundResource(R.drawable.ic_pause)
    }


    private fun startRecording() {

        var formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        Log.d("TIMESTAMP", "  $formattedDate")
        if(isExternalStorageReadable()) Log.d("EXTERNAL","IS READABLE")
        else Log.d("EXTERNAL","IS NOT READABLE")
        if(isExternalStorageWritable()) Log.d("EXTERNAL","IS WRITABLE")
        else Log.d("EXTERNAL","IS NOT WRITABLE")

        timer.base = SystemClock.elapsedRealtime() - timeWhenStopped
        timer.start()
        recorder = MediaRecorder()
//        dirPath = "${externalCacheDir?.absolutePath}/"
        dirPath = activity?.getExternalFilesDir("/")?.absolutePath.toString()

        var simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        var date: String = simpleDateFormat.format(Date())
        filename = "audio_record_$date"

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$filename.mp3")

//            try {
//                prepare()
//            } catch (e: IOException) {
//            e.printStackTrace()
//            }
            prepare()
            start()
        }
        recordButton.setBackgroundResource(R.drawable.ic_pause)
        isRecording = true
        isPaused = false
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
        Log.d("DURATION","$timeWhenStopped")
        durationVoiceMessage(timeWhenStopped)
        Log.d("DURATION2","${durationVoiceMessage(timeWhenStopped)}")
        sendRecording()
    }

    private fun sendRecording() {

        var filePath = "$dirPath$filename.mp3"
        var timestamp: Long = Date().time
        var duration = durationVoiceMessage(timeWhenStopped)

        var date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

//        var voiceMessage = VoiceMessage(filename,filePath,timestamp,duration,user,"Outgoing name","Incoming name", date)

        //TODO send message to Fragment Recordings List for storage
        timeWhenStopped = 0;

        var voiceMessage = VoiceMessage(filename,filePath,timestamp,duration,123123,34535,date)

        GlobalScope.launch {
            context?.let { AppDatabase.getInstance(it).voiceMessageDao.insert(voiceMessage) }
        }
//        GlobalScope.launch {
//            context?.let { AppDatabase.getInstance(it).voiceMessageDao.getAll() }
//        }
    }


    private fun checkPermission(): Boolean {
        when {
            context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    permissions[1]
                )
            } == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                return true
            }
            activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(
                    it,
                    permissions[1]
                )
            } == true -> {
                rootView?.let {
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
                requestPermissionLauncher.launch(permissions[1])
                return false
            }
        }
    }

    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }


// Checks if a volume containing external storage is available to at least read.

    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    private fun durationVoiceMessage(recordingTime: Long): String{

        val minutes = recordingTime / 1000 / 60
        val seconds = recordingTime / 1000 % 60


      return "$minutes:$seconds"

    }
}

