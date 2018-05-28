package com.viana.soundprogramming.sound

import com.viana.soundprogramming.R
import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.blocks.SoundBlock
import java.util.*

class BlocksRecorder : BlocksManager.Listener {

    var waitingForRecordableBlockApproximation: Boolean = false
    var recording: Boolean = false
    var busy: Boolean = false
        set(value) {
            field = value
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    field = false
                }
            }, 10000)
        }
    private val recorder: Recorder = MyAudioRecorder()
    private val listeners = mutableListOf<Listener>()
    private var soundBlockEnteredToBeRecorded: SoundBlock? = null

    override fun updateBlocksList(blocks: List<Block>) {
        if (busy || !waitingForRecordableBlockApproximation) return
        blocks.firstOrNull {
            it.diameter > 120
        }?.apply {
            busy = true
            if (this !is SoundBlock) {
                Speaker.instance.say(R.raw.essa_peca_nao_pode_ser_gravada)
            } else if (!recording) {
                if (soundBlockEnteredToBeRecorded != null) {
                    if (soundBlockEnteredToBeRecorded?.code == this.code)
                        return
                }
                soundBlockEnteredToBeRecorded = this
                readyToStartRecord(code)
            }
        }
    }

    private fun readyToStartRecord(code: Int) {
        listeners.forEach { it.readyToStartRecord(code) }
    }

    fun record(onRecordCompletedListener: OnRecordCompletedListener) {
        soundBlockEnteredToBeRecorded?.let {
            recorder.apply {
                listener = object : Recorder.Listener {
                    override fun onCodeRecorded(producedSoundId: Int, recordedFilePath: String) {
                        recording = false
                        it.soundId = producedSoundId
                        /*it.soundStream = BufferedInputStream(FileInputStream(recordedFilePath))
                        it.soundShortArray = readShorts(FileInputStream(recordedFilePath))*/
                        onRecordCompletedListener.recordCompleted(it)
                        freeBlockToBeRecordedAgainAfter(10000)
                    }
                }
                recordSeconds(2, it.code)
            }
        }
    }

    private fun freeBlockToBeRecordedAgainAfter(i: Long) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                soundBlockEnteredToBeRecorded = null
            }
        }, i)
    }

    fun addListener(listener: Listener) {
        this.listeners.add(listener)
    }

    interface Listener {
        fun readyToStartRecord(code: Int)
    }

    interface OnRecordCompletedListener {
        fun recordCompleted(soundBlock: SoundBlock)
    }

}