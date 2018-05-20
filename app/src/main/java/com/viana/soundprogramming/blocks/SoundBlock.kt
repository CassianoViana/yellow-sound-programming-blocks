package com.viana.soundprogramming.blocks

import android.graphics.Canvas
import android.graphics.Color
import com.viana.soundprogramming.appInstance
import com.viana.soundprogramming.sound.SoundManager
import com.viana.soundprogramming.util.readShorts
import java.io.InputStream

open class SoundBlock : ControllableBlock() {

    var soundId: Int? = null
    var soundStream: InputStream? = null
    var soundShortArray: ShortArray? = null

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        paint.color = Color.BLUE
        paint.alpha = 100
        canvas?.drawRect(rect, paint)
    }

    override fun copy(): Block {
        val block = SoundBlock()
        fillWithProperties(block)
        return block
    }

    override fun fillWithProperties(block: Block) {
        (block as SoundBlock).soundId = soundId
        block.soundStream = soundStream
        block.soundShortArray = soundShortArray
        super.fillWithProperties(block)
    }

    class Builder {
        private var soundResourceId: Int? = null
        fun setSoundId(soundResourceId: Int): Builder {
            this.soundResourceId = soundResourceId
            return this
        }

        fun build(): SoundBlock {
            soundResourceId?.let {
                val soundBlock = SoundBlock()
                soundBlock.soundId = SoundManager.instance.load(it)
                soundBlock.soundStream = appInstance.resources.openRawResource(it)
                soundBlock.soundShortArray = readShorts(appInstance.resources.openRawResource(it))
                return soundBlock
            }
            throw IllegalStateException("Its necessary inform the soundResourceId")
        }
    }
}