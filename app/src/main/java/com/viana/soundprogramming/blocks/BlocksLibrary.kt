package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.R

class BlocksLibrary {

    private val blocks: MutableMap<Int, Block> = mutableMapOf()

    init {
        blocks[59] = SoundBlock(R.raw.clap1)
        blocks[61] = SoundBlock(R.raw.clap2)
        blocks[79] = SoundBlock(R.raw.clap3)
        blocks[339] = SoundBlock(R.raw.clap4)
        blocks[341] = SoundBlock(R.raw.clap5)
        blocks[355] = SoundBlock(R.raw.clap6)
        blocks[345] = SoundBlock(R.raw.clap7)
        blocks[295] = SoundBlock(R.raw.cat1)
        blocks[357] = SoundBlock(R.raw.cat2)
        blocks[217] = BeginBlock()
    }

    fun get(code: Int): Block? = blocks[code]

}
