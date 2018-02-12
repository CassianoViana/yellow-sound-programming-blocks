package com.viana.soundprogramming

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import topcodes.TopCode

class TopCodesView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {

    var topCodes: MutableList<TopCode> = mutableListOf()


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.setARGB(255, 255, 0, 0)
        canvas?.drawCircle(0f, 0f, 10f, paint)
        paint.setARGB(255, 0, 255, 0)
        canvas?.drawCircle(0f, 100f, 10f, paint)
        paint.setARGB(255, 0, 0, 255)
        canvas?.drawCircle(100f, 0f, 10f, paint)
        //canvas?.rotate(90F)
        topCodes.forEach {
            it.draw(canvas)
        }
    }

}
