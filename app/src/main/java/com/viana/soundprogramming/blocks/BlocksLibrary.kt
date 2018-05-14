package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.R
import topcodes.TopCode

class BlocksLibrary {

    private val blocks: MutableMap<Int, Block> = mutableMapOf()

    init {
        blocks[31] = PlayBlock()
        blocks[47] = PauseBlock()
        blocks[87] = BeginBlock()
        blocks[91] = EndBlock()

        blocks[55] = LoopBlock()
        blocks[59] = LoopSpinnerBlock()

        blocks[61] = VolumeBlock()
        blocks[79] = SpeedBlock()

        blocks[93] = IfBlock()
        blocks[103] = HasStartBlock()
        blocks[107] = StarBlock()

        blocks[279] = RecordBlock()

        blocks[205] = SoundBlock.Builder().setSoundId(R.raw.drum1).build()
        blocks[211] = SoundBlock.Builder().setSoundId(R.raw.drum2).build()
        blocks[213] = SoundBlock.Builder().setSoundId(R.raw.snaredrum101).build()
        blocks[217] = SoundBlock.Builder().setSoundId(R.raw.snaredrum102).build()
        blocks[227] = SoundBlock.Builder().setSoundId(R.raw.snaredrum51).build()
        blocks[229] = SoundBlock.Builder().setSoundId(R.raw.snaredrum52).build()
        blocks[233] = SoundBlock.Builder().setSoundId(R.raw.conga1).build()
        blocks[241] = SoundBlock.Builder().setSoundId(R.raw.conga2).build()
        blocks[271] = SoundBlock.Builder().setSoundId(R.raw.tambourine1).build()

    }

    fun getTopCodeBlock(topCode: TopCode): Block? {
        val block = blocks[topCode.code]
        block?.topCode = topCode
        return block
    }
}
