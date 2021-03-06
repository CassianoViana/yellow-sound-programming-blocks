package com.viana.soundprogramming.blocks

import com.viana.soundprogramming.R
import com.viana.soundprogramming.appInstance
import topcodes.TopCode

class BlocksLibrary {

    val blocks: MutableMap<Int, Block> = mutableMapOf()

    init {
        blocks[87] = LockBlock()

        blocks[713] = CornerBlock().setPositions(CornerBlock.Corner.TOP, CornerBlock.Corner.LEFT)
        blocks[617] = CornerBlock().setPositions(CornerBlock.Corner.TOP, CornerBlock.Corner.RIGHT)
        blocks[613] = CornerBlock().setPositions(CornerBlock.Corner.BOTTOM, CornerBlock.Corner.LEFT)
        blocks[681] = CornerBlock().setPositions(CornerBlock.Corner.BOTTOM, CornerBlock.Corner.RIGHT)

        blocks[31] = PlayBlock()
        blocks[47] = PauseBlock()
        blocks[279] = RecordBlock()
        blocks[341] = HelpBlock()

        blocks[117] = ModuleBlock()

        blocks[333] = PresenceBlock(PresenceBlock.Type.CIRCLE).setHelpMessage(R.raw.ajuda_circulo)
        blocks[107] = PresenceBlock(PresenceBlock.Type.STAR).setHelpMessage(R.raw.ajuda_estrela)

        blocks[61] = VolumeBlock()
        blocks[79] = SpeedBlock()

        blocks[55] = LoopBlock().setHelpMessage(R.raw.ajuda_peca_repita)
        blocks[361] = LoopParamBlock(2).setHelpMessage(R.raw.ajuda_numero_2)
        blocks[357] = LoopParamBlock(3).setHelpMessage(R.raw.ajuda_numero_3)
        blocks[355] = LoopParamBlock(4).setHelpMessage(R.raw.ajuda_numero_4)
        blocks[345] = LoopParamBlock(5).setHelpMessage(R.raw.ajuda_numero_5)


        blocks[93] = IfBlock().setHelpMessage(R.raw.ajuda_se)

        blocks[109] = IfParamBlock(PresenceBlock.Type.CIRCLE)
        blocks[121] = IfParamBlock(PresenceBlock.Type.STAR)

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
                .setHelpMessage(R.raw.ajuda_prato_conducao)
        blocks[181] = SoundBlock.Builder()
                .setSoundId(R.raw.tom1)
                .setDrawable(R.drawable.button_stop, appInstance.resources)
                .build()
                .setHelpMessage(R.raw.ajuda_tom)
        blocks[185] = SoundBlock.Builder()
                .setSoundId(R.raw.chimbal)
                .setDrawable(R.drawable.button_stop, appInstance.resources)
                .build()
                .setHelpMessage(R.raw.ajuda_chimbal)
        blocks[91] = SoundBlock.Builder()
                .setSoundId(R.raw.fx_17)
                .setDrawable(R.drawable.button_stop, appInstance.resources)
                .build()

        blocks[59] = SoundBlock.Builder().setSoundId(R.raw.none).setDrawable(R.drawable.button_stop, appInstance.resources).build().setHelpMessage(R.raw.essa_e_uma_peca_curinga)
        blocks[103] = SoundBlock.Builder().setSoundId(R.raw.none).setDrawable(R.drawable.button_stop, appInstance.resources).build().setHelpMessage(R.raw.essa_e_uma_peca_curinga)
        blocks[115] = SoundBlock.Builder().setSoundId(R.raw.none).setDrawable(R.drawable.button_stop, appInstance.resources).build().setHelpMessage(R.raw.essa_e_uma_peca_curinga)
        blocks[143] = SoundBlock.Builder().setSoundId(R.raw.none).setDrawable(R.drawable.button_stop, appInstance.resources).build().setHelpMessage(R.raw.essa_e_uma_peca_curinga)
        blocks[151] = SoundBlock.Builder().setSoundId(R.raw.none).setDrawable(R.drawable.button_stop, appInstance.resources).build().setHelpMessage(R.raw.essa_e_uma_peca_curinga)
    }

    fun getTopCodeBlock(topCode: TopCode): Block? {
        val block = blocks[topCode.code]
        block?.topCode = topCode
        block?.active = true
        return block?.copy()

    }
}
