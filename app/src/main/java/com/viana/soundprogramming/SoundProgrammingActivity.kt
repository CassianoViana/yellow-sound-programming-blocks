package com.viana.soundprogramming

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.blocks.SoundBlock
import com.viana.soundprogramming.blocks.TopCodesReader
import com.viana.soundprogramming.camera.Camera
import com.viana.soundprogramming.camera.OnEachFrameListener
import com.viana.soundprogramming.camera.ScreenUtil
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
    private lateinit var musicManager: MusicManager
    private val topCodesReader = TopCodesReader()
    private val blocksManager = BlocksManager()
    private val blocksRecorder = BlocksRecorder()
    private val stateMachine = StateMachine()
    private val helper = Helper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_programming)
        prepareCamera()
        prepareMusicManager()
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
        board.prepare(this, timelineView)
        camera = Camera(this, surfaceView)
        camera.onEachFrameListener = object : OnEachFrameListener {
            override fun onNewFrame(bitmap: Bitmap) {
                topCodesReader.read(bitmap)
            }
        }
    }

    private fun prepareMusicManager() {
        musicManager = MusicManager(stateMachine, board)
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
        board.start()
    }

    private fun stopCamera() {
        board.stop()
        camera.closeCamera()
    }

    private fun prepareTopCodeListeners() {
        topCodesReader.addListener(blocksManager)
    }

    private fun prepareBlocksManagerListeners() {
        blocksManager
                .addListener(board)
                .addListener(stateMachine)
                .addListener(blocksRecorder)
                .addListener(helper)
                .addListener(musicManager)
    }

    private fun prepareTimelineListener() {
        board
                .timeline.addListener(object : Timeline.Listener {
            override fun onHitStart(timelineTimer: TimelineTimer, index: Int) {
                ProgrammingVibrator.vibrate(5)
                musicManager.music?.play(index)
            }
        })
    }

    private fun prepareStateMachineListeners() {
        stateMachine
                .addListener(this)
                .addListener(board.timeline)
                .addListener(helper)
                .addListener(blocksRecorder)
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
                    musicManager.music?.let {
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
        }, 15000)
    }
}