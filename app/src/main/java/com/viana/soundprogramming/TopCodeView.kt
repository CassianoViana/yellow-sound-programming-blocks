package com.viana.soundprogramming

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import topcodes.TopCode

class TopCodeView : View {

    private var topCode: TopCode

    constructor(topCode: TopCode, context: Context) : super(context) {
        this.topCode = topCode
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.setARGB(255, 200, 200, 200)

        canvas?.drawCircle(topCode.centerX, topCode.centerY, topCode.diameter / 2, paint)
        topCode.draw(canvas)

        paint.setARGB(255,255,0,0)
        canvas?.drawCircle(0f,0f,10f, paint)
        paint.setARGB(255,0,255,0)
        canvas?.drawCircle(0f,100f,10f, paint)
        paint.setARGB(255,0,0,255)
        canvas?.drawCircle(100f,0f,10f, paint)
    }

}
