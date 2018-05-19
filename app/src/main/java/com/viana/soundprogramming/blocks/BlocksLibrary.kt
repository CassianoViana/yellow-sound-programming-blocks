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
        blocks[361] = LoopParamBlock(2)
        blocks[357] = LoopParamBlock(3)
        blocks[355] = LoopParamBlock(4)
        blocks[345] = LoopParamBlock(5)

        blocks[61] = VolumeBlock()
        blocks[79] = SpeedBlock()

        blocks[93] = IfBlock()

        blocks[109] = IfParamBlock(IfParamBlock.Type.HAS_CIRCLE)
        blocks[117] = IfParamBlock(IfParamBlock.Type.HAS_SQUARE)
        blocks[121] = IfParamBlock(IfParamBlock.Type.HAS_STAR)

        blocks[333] = PresenceBlock(PresenceBlock.Type.CIRCLE)
        blocks[339] = PresenceBlock(PresenceBlock.Type.SQUARE)
        blocks[107] = PresenceBlock(PresenceBlock.Type.STAR)

        blocks[279] = RecordBlock()

        blocks[157] = SoundBlock.Builder().setSoundId(R.raw.surdo).build()
        blocks[167] = SoundBlock.Builder().setSoundId(R.raw.caixa).build()
        blocks[171] = SoundBlock.Builder().setSoundId(R.raw.bumbo).build()
        blocks[173] = SoundBlock.Builder().setSoundId(R.raw.prato_ataque).build()
        blocks[179] = SoundBlock.Builder().setSoundId(R.raw.prato_conducao).build()
        blocks[181] = SoundBlock.Builder().setSoundId(R.raw.tom1).build()
        blocks[185] = SoundBlock.Builder().setSoundId(R.raw.chimbal).build()

    }

    fun getTopCodeBlock(topCode: TopCode): Block? {
        val block = blocks[topCode.code]
        block?.topCode = topCode
        block?.active = true
        return block?.copy()

    }
}
