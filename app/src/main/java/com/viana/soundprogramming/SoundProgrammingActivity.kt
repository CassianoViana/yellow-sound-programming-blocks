package com.viana.soundprogramming

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.viana.soundprogramming.blocks.*
import com.viana.soundprogramming.camera.Camera
import com.viana.soundprogramming.camera.OnEachFrameListener
import com.viana.soundprogramming.camera.ScreenUtil
import com.viana.soundprogramming.core.Music
import com.viana.soundprogramming.core.MusicBuilder
import com.viana.soundprogramming.core.MusicBuilderImpl
import com.viana.soundprogramming.exceptions.SoundProgrammingError
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
    private val helper = Helper()
    private var music: Music? = null
    private var boardBlocks = mutableListOf<Block>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_programming)
        prepareCamera()
        Speaker.instance.load()
        prepareTopCodeListeners()
        prepareBlocksManagerListeners()
        prepareTimelineListener()
        prepareStateMachineListeners()
        prepareBlocksRecorder()
        managePermissionSound(this)
        managePermissionVibrate(this)
        managePermissionDirectory(this)
        managePermissionCamera(this)
    }

    override fun onResume() {
        super.onResume()
        Speaker.instance.say(R.raw.msg_olaaa_vamos_programar_bateria)
        ScreenUtil.fullscreen(window)
        startAfterDelay(1000)
    }

    override fun onPause() {
        super.onPause()
        ScreenUtil.exitFullscreen(window)
        stopCamera()
    }

    fun onClickTest(view: View?) {
        stopCamera()
        camera.flashLightOn = !camera.flashLightOn
        camera.openCamera()
        //startActivity(Intent(this, TestActivity::class.java))
    }

    private fun prepareCamera() {
        boardSurfaceView.prepare(this, timelineView)
        camera = Camera(this, surfaceView)
        camera.onEachFrameListener = object : OnEachFrameListener {
            override fun onNewFrame(bitmap: Bitmap) {
                topCodesReader.read(bitmap)
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
        camera.flashLightOn = true
        camera.openCamera()
        boardSurfaceView.start()
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
                .addListener(helper)
                .addListener(object : BlocksManager.Listener {
                    override fun updateBlocksList(blocks: List<Block>) {
                        if (stateMachine.state == StateMachine.State.PLAYING) {
                            boardBlocks = blocks.toMutableList()
                            buildMusic()
                        }
                    }
                })
    }

    private fun prepareTimelineListener() {
        boardSurfaceView
                .timeline.addListener(object : Timeline.Listener {
            override fun onHitStart(timelineTimer: TimelineTimer) {
                ProgrammingVibrator.vibrate(10)
                music?.play()
            }
        })
    }

    private fun prepareStateMachineListeners() {
        stateMachine
                .addListener(this)
                .addListener(boardSurfaceView.timeline)
                .addListener(helper)
                .addListener(blocksRecorder)
    }

    private fun buildMusic() {
        val empty = boardBlocks.isEmpty()
        val locked = boardBlocks.any { it.javaClass == LockBlock::class.java }
        if (locked || empty)
            return
        musicBuilder.build(boardBlocks,
                boardSurfaceView,
                object : MusicBuilder.OnMusicReadyListener {
                    override fun ready(builtMusic: Music) {
                        music?.stop()
                        music = builtMusic
                        boardSurfaceView.timeline.scheduleTimer()
                    }

                    override fun error(e: SoundProgrammingError) {
                        e.printStackTrace()
                        Speaker.instance.say(e.explanationResId)
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

    override fun stateChanged(state: StateMachine.State, previous: StateMachine.State) {
        runOnUiThread {
            when (state) {
                StateMachine.State.HELPING -> Speaker.instance.say(R.raw.modo_ajuda)
                StateMachine.State.PLAYING -> {
                    music?.let {
                        if (it.sounds.isNotEmpty()) {
                            Speaker.instance.say(R.raw.modo_solta_o_som)
                        }
                    }
                }
                StateMachine.State.PAUSED -> {
                    Speaker.instance.say(R.raw.modo_parar)
                }
                StateMachine.State.RECORDING -> Speaker.instance.say(R.raw.modo_gravar)
            }
        }
    }

    override fun readyToStartRecord(code: Int) {
        Speaker.instance.say(R.raw.gravando_em_321)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                blocksRecorder.record(object : BlocksRecorder.OnRecordCompletedListener {
                    override fun recordCompleted(soundBlock: SoundBlock) {
                        blocksManager.updateBlockSoundSoundId(soundBlock.code, soundBlock.soundId)
                        Speaker.instance.say(R.raw.gravacao_muito_bem)
                    }
                })
            }
        }, 14500)
    }
}