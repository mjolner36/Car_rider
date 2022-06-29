package com.example.carrider

import android.graphics.Bitmap
import android.graphics.Canvas


class Projectile(var bitmap: Bitmap, var x: Int, var y: Int) {

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, x.toFloat(), y.toFloat(), null)
        y += 10
    }
}