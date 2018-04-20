package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.R
import com.viana.soundprogramming.sound.SoundManager

class BlocksLibrary {

    private val blocks: MutableMap<Int, Block> = mutableMapOf()

    init {
        blocks[31] = PlayBlock()
        blocks[47] = StopBlock()
        blocks[87] = BeginBlock()
        blocks[91] = EndBlock()

        blocks[55] = LoopBlock()
        blocks[59] = LoopSpinnerBlock()

        blocks[61] = VolumeBlock()
        blocks[79] = SpeedBlock()

        blocks[93] = IfBlock()
        blocks[103] = HasStartBlock()
        blocks[107] = StarBlock()
        blocks[109] = HasCatBlock()
        blocks[115] = CatBlock(SoundManager.instance.load(R.raw.cat1))
        blocks[117] = HasDogBlock()
        blocks[121] = DogBlock(SoundManager.instance.load(R.raw.dog))

        blocks[279] = ExplanationBlock()

        blocks[173] = ModuleBlock(2)
        blocks[345] = ModuleBlock(3)

        blocks[157] = SoundBlock(SoundManager.instance.load(R.raw.guittar1))
        blocks[167] = SoundBlock(SoundManager.instance.load(R.raw.guittar2))
        blocks[171] = SoundBlock(SoundManager.instance.load(R.raw.guittar3))
        //blocks[173] = SoundBlock(SoundManager.instance.load(R.raw.guittar4))
        blocks[179] = SoundBlock(SoundManager.instance.load(R.raw.guittar5))
        blocks[181] = SoundBlock(SoundManager.instance.load(R.raw.guittar6))
        blocks[185] = SoundBlock(SoundManager.instance.load(R.raw.guittar7))
        blocks[199] = SoundBlock(SoundManager.instance.load(R.raw.guittar8))
        blocks[203] = SoundBlock(SoundManager.instance.load(R.raw.guittar9))

        blocks[205] = SoundBlock(SoundManager.instance.load(R.raw.drum1))
        blocks[211] = SoundBlock(SoundManager.instance.load(R.raw.drum2))
        blocks[213] = SoundBlock(SoundManager.instance.load(R.raw.snaredrum101))
        blocks[217] = SoundBlock(SoundManager.instance.load(R.raw.snaredrum102))
        blocks[227] = SoundBlock(SoundManager.instance.load(R.raw.snaredrum51))
        blocks[229] = SoundBlock(SoundManager.instance.load(R.raw.snaredrum52))
        blocks[233] = SoundBlock(SoundManager.instance.load(R.raw.conga1))
        blocks[241] = SoundBlock(SoundManager.instance.load(R.raw.conga2))
        blocks[271] = SoundBlock(SoundManager.instance.load(R.raw.tambourine1))

    }

    fun get(code: Int): Block? = blocks[code]

}
