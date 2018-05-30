package com.viana.soundprogramming.exceptions

import com.viana.soundprogramming.R

open class SoundProgrammingError(var explanationResId: Int) : Throwable()
open class SoundSyntaxError(resId: Int) : SoundProgrammingError(resId)
class BaseOutOfCornersError : SoundProgrammingError(R.raw.alerta_tabuleiro_nao_esta_centralizado)
class LoopBlockNeedsParamError : SoundSyntaxError(R.raw.alerta_falta_parametro_repita)
class IfBlockNeedsParamError : SoundSyntaxError(R.raw.alerta_falta_parametro_se)
