package com.example.carrider

import android.graphics.Bitmap
import android.graphics.Canvas


class Player(var bitmap: Bitmap, var x: Int, val y: Int) {
    var xVelocity = 0

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, x.toFloat(), y.toFloat(), null)
        x += xVelocity
    }
}