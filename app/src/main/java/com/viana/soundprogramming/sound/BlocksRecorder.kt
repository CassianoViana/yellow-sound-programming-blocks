package com.viana.soundprogramming.sound

import com.viana.soundprogramming.blocks.Block
import com.viana.soundprogramming.blocks.BlocksManager
import com.viana.soundprogramming.blocks.SoundBlock

class BlocksRecorder : BlocksManager.Listener {

    var waitingForRecordableBlockEnter: Boolean = false
    var recording: Boolean = false
    private val recorder: Recorder = MyMediaRecorder()
    private var previousSoundBlocks = mutableListOf<SoundBlock>()
    private val listeners = mutableListOf<Listener>()
    private var soundBlockEnteredToBeRecorded: SoundBlock? = null

    override fun updateBlocksList(blocks: List<Block>) {
        val soundBlocks: List<SoundBlock> = blocks.filterIsInstance(SoundBlock::class.java)
        if (!recording)
            if (waitingForRecordableBlockEnter) {
                getSoundBlockWhenItEnter(soundBlocks)
            }
        this.previousSoundBlocks = soundBlocks.toMutableList()
    }

    private fun getSoundBlockWhenItEnter(soundBlocks: List<SoundBlock>) {
        this.soundBlockEnteredToBeRecorded = soundBlocks.firstOrNull {
            !previousSoundBlocks.contains(it)
        }
        this.soundBlockEnteredToBeRecorded?.let {
            readyToStartRecord(it.code)
        }
    }

    private fun readyToStartRecord(code: Int) {
        listeners.forEach { it.readyToStartRecord(code) }
    }

    fun record(onRecordCompletedListener: OnRecordCompletedListener) {
        soundBlockEnteredToBeRecorded?.let {
            recording = true
            record(it.code, object : BlocksRecorder.OnRecordCompletedListener {
                override fun recordCompleted(soundId: Int) {
                    it.soundId = soundId
                    onRecordCompletedListener.recordCompleted(it.soundId)
                    recording = false
                }
            })
        }
    }

    private fun record(code: Int, onRecordCompletedListener: OnRecordCompletedListener) {
        recorder.listener = object : Recorder.Listener {
            override fun onCodeRecorded(producedSoundId: Int) {
                onRecordCompletedListener.recordCompleted(producedSoundId)
            }
        }
        this.recorder.recordSeconds(3, code)
    }

    fun addListener(listener: Listener) {
        this.listeners.add(listener)
    }

    interface Listener {
        fun readyToStartRecord(code: Int)
    }

    interface OnRecordCompletedListener {
        fun recordCompleted(soundId: Int)
    }

}