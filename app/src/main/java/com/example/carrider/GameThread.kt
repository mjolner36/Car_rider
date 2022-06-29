package com.example.carrider

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.view.SurfaceHolder


class GameThread(private val surfaceHolder: SurfaceHolder, gameView: GameView) : Thread() {

    private val gameView: GameView
    private var running = false
    fun setRunning(running: Boolean) {
        this.running = running
    }

    @SuppressLint("WrongCall")
    override fun run() {
        var canvas: Canvas?
        while (running) {
            canvas = null
            try {
                canvas = surfaceHolder.lockCanvas()
                synchronized(surfaceHolder) {
                    gameView.onDraw(canvas)
                    gameView.collisionsCheck(canvas)
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    init {
        this.gameView = gameView
    }
}