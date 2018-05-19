package com.viana.soundprogramming.exceptions

import com.viana.soundprogramming.R

open class SoundSyntaxError(resId: Int) : SoundProgrammingException(resId)
class LoopBlockNeedsParamError: SoundSyntaxError(R.raw.falta_informar_parametro_repita)
class TwoNumberBlocksConcatenatedError : SoundSyntaxError(R.raw.blocos_de_numero_seguidos)
