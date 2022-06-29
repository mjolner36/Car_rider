package com.example.carrider

import android.graphics.Bitmap
import android.graphics.Canvas

class BackgroundImage(var bitmap: Bitmap,val x: Int, var y: Int) {
    var yVelocity = 0


    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, x.toFloat(), y.toFloat(), null)
        y += yVelocity
    }

}