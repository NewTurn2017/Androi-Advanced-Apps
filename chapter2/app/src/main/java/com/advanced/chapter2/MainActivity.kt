package com.advanced.chapter2

import android.Manifest.*
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.advanced.chapter2.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity(), OnTimerTickListener {


    val REQUEST_CODE = 100

    private enum class State {
        RELEASE, RECORDING, PLAYING
    }

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var filename: String = ""
    private var state: State = State.RELEASE

    var startTime: Long = 0
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            val elapsedTime = SystemClock.elapsedRealtime() - startTime
            val seconds = (elapsedTime / 1000).toInt()
            val minutes = seconds / 60
            val hour = minutes / 60
            val remainingSeconds = seconds % 60
            binding.timeTextView.text =
                String.format("%02d:%02d:%02d", hour, minutes, remainingSeconds)
            handler.postDelayed(this, 1000)
        }
    }

    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        filename = "${externalCacheDir?.absolutePath}/recording.3gp"
        timer = Timer(this)

        binding.recordButton.setOnClickListener {

            when (state) {
                State.RELEASE -> {
                    record()
                }
                State.RECORDING -> {
                    OnRecord(false)
                }
                else -> {
                    // do nothing
                }
            }
        }

        binding.playButton.setOnClickListener {

            when (state) {
                State.RELEASE -> {
                    OnPlay(true)
                }
                else -> {
                    // do nothing
                }
            }
        }

        binding.stopButton.setOnClickListener {
            when (state) {
                State.RECORDING -> {
                    OnRecord(false)
                }
                State.PLAYING -> {
                    OnPlay(false)
                }
                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun record() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                OnRecord(true)

            }

            ActivityCompat.shouldShowRequestPermissionRationale(this, permission.RECORD_AUDIO)
            -> {
                showPermissionRationalDialog()
            }
            else -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission.RECORD_AUDIO),
                    REQUEST_CODE
                )
            }
        }
    }

    private fun OnRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun OnPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startRecording() {

        startTime = SystemClock.elapsedRealtime()
        handler.post(runnable)


        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(filename)
            try {
                prepare()
            } catch (e: IOException) {
                Log.e("Record", "prepare() failed " + e.message)
            }
            start()
        }

        recorder?.maxAmplitude?.toFloat()

        binding.waveForm.clearData()
        timer.start()
        state = State.RECORDING
        binding.recordButton.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.baseline_pause_24
            )
        )
        binding.recordButton.imageTintList =
            ContextCompat.getColorStateList(this@MainActivity, R.color.black)
        binding.playButton.isEnabled = false
        binding.playButton.alpha = 0.3f
    }

    private fun stopRecording() {
        handler.removeCallbacks(runnable)
        binding.timeTextView.text = "00:00:00"

        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        timer.stop()
        state = State.RELEASE

        binding.recordButton.setImageDrawable(
            ContextCompat.getDrawable(
                this@MainActivity,
                R.drawable.baseline_fiber_manual_record_24
            )
        )
        binding.recordButton.imageTintList =
            ContextCompat.getColorStateList(this@MainActivity, android.R.color.holo_red_light)
        binding.playButton.isEnabled = true
        binding.playButton.alpha = 1.0f
    }

    private fun startPlaying() {
        state = State.PLAYING


        player = MediaPlayer().apply {
            try {
                setDataSource(filename)
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("Play", "prepare() failed " + e.message)
            }
        }

        binding.waveForm.clearWave()
        timer.start()

        player?.setOnCompletionListener {
            stopPlaying()
        }

        binding.recordButton.isEnabled = false
        binding.recordButton.alpha = 0.3f
    }

    private fun stopPlaying() {


        state = State.RELEASE
        player?.release()
        player = null

        timer.stop()

        binding.recordButton.isEnabled = true
        binding.recordButton.alpha = 1.0f
    }


    private fun showPermissionRationalDialog() {
        AlertDialog.Builder(this)
            .setMessage("이 앱은 오디오를 녹음하기 위해 마이크에 액세스해야 합니다.")
            .setPositiveButton("권한 허용하기") { _, _ ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission.RECORD_AUDIO),
                    REQUEST_CODE
                )
            }
            .setNegativeButton("취소") { _, _ -> }
            .create()
            .show()
    }

    private fun showPermissionDisAllowedDialog() {
        AlertDialog.Builder(this)
            .setMessage("녹음 권한을 켜주셔야 앱을 정상적으로 사용할 수 있습니다. 앱 설정 화면으로 진입하여 권한을 켜주세요.")
            .setPositiveButton("권한 설정하기") { _, _ ->
                goSettings()
            }
            .setNegativeButton("취소") { _, _ -> }
            .create()
            .show()
    }

    private fun goSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    OnRecord(true)
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            permission.RECORD_AUDIO
                        )
                    ) {
                        showPermissionRationalDialog()
                    } else {
                        showPermissionDisAllowedDialog()
                    }
                }
            }
        }
    }

    override fun onTick(duration: Long) {
        val millisecond = duration % 1000 / 10
        val second = (duration / 1000) % 60
        val minute = (duration / (1000 * 60)) % 60

        binding.timeTextView.text = String.format("%02d:%02d.%02d", minute, second, millisecond)

        if (state == State.PLAYING) {
            binding.waveForm.replayAmplitude(duration.toInt())
        } else if (state == State.RECORDING) {
            binding.waveForm.addAmplitude(recorder?.maxAmplitude?.toFloat() ?: 0f)
        }

    }


}