package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.R
import com.viana.soundprogramming.appInstance
import topcodes.TopCode

class BlocksLibrary {

    val blocks: MutableMap<Int, Block> = mutableMapOf()

    init {
        blocks[713] = CornerBlock()
        blocks[613] = CornerBlock()
        blocks[617] = CornerBlock()
        blocks[681] = CornerBlock()

        blocks[31] = PlayBlock()
        blocks[47] = PauseBlock()
        blocks[279] = RecordBlock()
        blocks[341] = HelpBlock()

        blocks[333] = PresenceBlock(PresenceBlock.Type.CIRCLE).setHelpMessage(R.raw.circulo)
        blocks[107] = PresenceBlock(PresenceBlock.Type.STAR).setHelpMessage(R.raw.ajuda_estrela)

        blocks[61] = VolumeBlock()
        blocks[79] = SpeedBlock()

        blocks[55] = LoopBlock().setHelpMessage(R.raw.ajuda_repita)
        blocks[361] = LoopParamBlock(2).setHelpMessage(R.raw.ajuda_numero_2)
        blocks[357] = LoopParamBlock(3).setHelpMessage(R.raw.ajuda_numero_3)
        blocks[355] = LoopParamBlock(4).setHelpMessage(R.raw.ajuda_numero_4)
        blocks[345] = LoopParamBlock(5).setHelpMessage(R.raw.ajuda_numero_5)


        blocks[93] = IfBlock().setHelpMessage(R.raw.ajuda_se)

        blocks[109] = IfParamBlock(IfParamBlock.Type.HAS_CIRCLE)
        blocks[121] = IfParamBlock(IfParamBlock.Type.HAS_STAR)

        blocks[157] = SoundBlock.Builder()
                .setSoundId(R.raw.surdo)
                .setDrawable(R.drawable.button_stop, appInstance.resources)
                .build()
                .setHelpMessage(R.raw.ajuda_surdo)
        blocks[167] = SoundBlock.Builder()
                .setSoundId(R.raw.caixa)
                .setDrawable(R.drawable.button_stop, appInstance.resources)
                .build()
                .setHelpMessage(R.raw.ajuda_caixa)
        blocks[171] = SoundBlock.Builder()
                .setSoundId(R.raw.bumbo)
                .setDrawable(R.drawable.button_stop, appInstance.resources)
                .build()
                .setHelpMessage(R.raw.ajuda_bumbo)
        blocks[173] = SoundBlock.Builder()
                .setSoundId(R.raw.prato_ataque)
                .setDrawable(R.drawable.button_stop, appInstance.resources)
                .build()
                .setHelpMessage(R.raw.ajuda_prato_ataque)
        blocks[179] = SoundBlock.Builder()
                .setSoundId(R.raw.prato_conducao)
                .setDrawable(R.drawable.button_stop, appInstance.resources)
                .build()
                .setHelpMessage(R.raw.ajuda_prato_de_conducao)
        blocks[181] = SoundBlock.Builder()
                .setSoundId(R.raw.tom1)
                .setDrawable(R.drawable.button_stop, appInstance.resources)
                .build()
                .setHelpMessage(R.raw.ajuda_tom)
        blocks[185] = SoundBlock.Builder()
                .setSoundId(R.raw.chimbal)
                .setDrawable(R.drawable.button_stop, appInstance.resources)
                .build()
                .setHelpMessage(R.raw.ajuda_chimbau)

    }

    fun getTopCodeBlock(topCode: TopCode): Block? {
        val block = blocks[topCode.code]
        block?.topCode = topCode
        block?.active = true
        return block?.copy()

    }
}
