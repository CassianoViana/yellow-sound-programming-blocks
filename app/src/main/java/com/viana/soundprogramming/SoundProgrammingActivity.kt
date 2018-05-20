package com.viana.soundprogramming

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.blocks.SoundBlock
import com.viana.soundprogramming.blocks.TopCodesReader
import com.viana.soundprogramming.camera.Camera
import com.viana.soundprogramming.camera.OnEachFrameListener
import com.viana.soundprogramming.camera.OnOpenCameraListener
import com.viana.soundprogramming.camera.ScreenUtil
import com.viana.soundprogramming.core.Music
import com.viana.soundprogramming.core.MusicBuilder
import com.viana.soundprogramming.core.MusicBuilderImpl
import com.viana.soundprogramming.exceptions.SoundSyntaxError
import com.viana.soundprogramming.sound.BlocksRecorder
import com.viana.soundprogramming.sound.Speaker
import com.viana.soundprogramming.timeline.Timeline
import com.viana.soundprogramming.timeline.TimelineTimer
import com.viana.soundprogramming.util.managePermissionCamera
import com.viana.soundprogramming.util.managePermissionDirectory
import com.viana.soundprogramming.util.managePermissionSound
import com.viana.soundprogramming.util.managePermissionVibrate
import com.viana.soundprogramming.vibration.ProgrammingVibrator
import kotlinx.android.synthetic.main.activity_sound_programming.*
import java.util.*


const val REQUEST_CODE_CAMERA_PERMISSION = 100
const val REQUEST_CODE_RECORD_PERMISSION = 200
const val REQUEST_CODE_WRITE_EXTERNAL_PERMISSION = 300
const val REQUEST_CODE_VIBRATE_PERMISSION = 400

class SoundProgrammingActivity : AppCompatActivity(), StateMachine.Listener, BlocksRecorder.Listener {

    private lateinit var camera: Camera
    private val topCodesReader = TopCodesReader()
    private val blocksManager = BlocksManager()
    private val musicBuilder = MusicBuilderImpl()
    private val blocksRecorder = BlocksRecorder()
    private val stateMachine = StateMachine()
    private var music: Music? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_programming)
        prepareCamera()
        prepareTopCodeListeners()
        prepareBlocksManagerListeners()
        prepareBlocksRecorder()
        Speaker.instance.load()
        managePermissionSound(this)
        managePermissionVibrate(this)
        managePermissionDirectory(this)
        managePermissionCamera(this)
    }

    override fun onResume() {
        super.onResume()
        ScreenUtil.fullscreen(window)
        startAfterDelay(1000)
    }

    override fun onPause() {
        super.onPause()
        ScreenUtil.exitFullscreen(window)
        stopCamera()
    }

    fun onClickStartStop(view: View) {
        ProgrammingVibrator.vibrate(30)
        if (!camera.isCameraOpen) startCamera() else stopCamera()
    }

    fun onClickTest(view: View?) {
        startActivity(Intent(this, TestActivity::class.java))
    }

    private fun prepareCamera() {
        boardSurfaceView.prepare(this, timelineView)
        camera = Camera(this, surfaceView)
        camera.onEachFrameListener = object : OnEachFrameListener {
            override fun newFrame(bitmap: Bitmap) {
                topCodesReader.read(bitmap)
            }
        }
        camera.onOpenCameraListener = object : OnOpenCameraListener {
            override fun cameraOpened() {
                Speaker.instance.say(R.raw.vamos_programar)
                boardSurfaceView.start()
            }
        }
    }

    private fun startAfterDelay(delay: Long) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread({
                    startCamera()
                })
            }
        }, delay)
    }

    private fun startCamera() {
        camera.flashLightOn = false
        camera.openCamera()
    }

    private fun stopCamera() {
        boardSurfaceView.stop()
        camera.closeCamera()
    }

    private fun prepareTopCodeListeners() {
        topCodesReader.addListener(blocksManager)
    }

    private fun prepareBlocksManagerListeners() {
        blocksManager
                .addListener(boardSurfaceView)
                .addListener(stateMachine)
                .addListener(blocksRecorder)
                .addListener(object : BlocksManager.Listener {
                    override fun updateBlocksList(blocks: List<Block>) {
                        musicBuilder.build(
                                blocks,
                                boardSurfaceView,
                                object : MusicBuilder.OnMusicReadyListener {
                                    override fun ready(music: Music) {
                                        this@SoundProgrammingActivity.music?.stop()
                                        this@SoundProgrammingActivity.music = music
                                        music.play()
                                    }

                                    override fun error(e: SoundSyntaxError) {
                                        Speaker.instance.say(e.explanationResId)
                                    }
                                }
                        )
                    }
                })
        stateMachine
                .addListener(this)
                .addListener(boardSurfaceView.timeline)
        boardSurfaceView
                .timeline?.addListener(object : Timeline.Listener {
            override fun onHitStart(timelineTimer: TimelineTimer) {
                /*ProgrammingVifbrator.vibrate(10)
                music?.play()*/
            }
        })
    }

    private fun prepareBlocksRecorder() {
        blocksRecorder.addListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            startAfterDelay(100)
    }

    override fun stateChanged(state: StateMachine.State) {
        when (state) {
            StateMachine.State.PLAYING -> {
                Speaker.instance.say(R.raw.a_musica_comecou)
                btnStartStop.background = ContextCompat
                        .getDrawable(this, R.drawable.button_stop)
                btnStartStop.setText(R.string.stop)
                blocksRecorder.waitingForRecordableBlockApproximation = false
            }
            StateMachine.State.PAUSED -> {
                Speaker.instance.say(R.raw.a_musica_foi_interrompida)
                btnStartStop.background = ContextCompat
                        .getDrawable(this, R.drawable.button_start)
                btnStartStop.setText(R.string.start)
                blocksRecorder.waitingForRecordableBlockApproximation = false
            }
            StateMachine.State.RECORDING -> {
                Speaker.instance.say(R.raw.modo_gravacao)
                blocksRecorder.waitingForRecordableBlockApproximation = true
            }
            else -> {
            }
        }
    }

    override fun readyToStartRecord(code: Int) {
        Speaker.instance.say(R.raw.pronto_a_peca_foi_selecionada)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                blocksRecorder.record(object : BlocksRecorder.OnRecordCompletedListener {
                    override fun recordCompleted(soundBlock: SoundBlock) {
                        Speaker.instance.say(R.raw.a_peca_foi_gravada)
                    }
                })
            }
        }, 7500)
    }
}