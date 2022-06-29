package com.example.carrider

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.*


class GameView(context: Context?) : SurfaceView(context),
    SurfaceHolder.Callback {
    private val thread: GameThread
    private var player: Player
    private var background:BackgroundImage
    private var projectiles: ArrayList<Projectile>? = null
    private var projectilesToRemove: ArrayList<Int>? = null
    var score: Int
    private var highScore: Int
    private val textColor: Paint
    private var scaled: Bitmap? = null
    private var isGameOver: Boolean
    private var t: Timer? = null
    private var speed: Int
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceCreated(holder: SurfaceHolder) {

        player = Player(
            BitmapFactory.decodeResource(resources,R.drawable.cabriolet), width / 2,
            2 * (height / 3)
        )
        thread.setRunning(true)
        if (thread.state == Thread.State.NEW) {
            thread.start()
        }
        projectiles = ArrayList<Projectile>()
        projectilesToRemove = ArrayList()
        t = Timer()
        t!!.scheduleAtFixedRate(object : TimerTask() {
             override fun run() {
                generateProjectile()
            }
        }, 0, speed.toLong())
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                thread.join()
                retry = false
            } catch (e: InterruptedException) {
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // move player
            if (event.x > width / 2) {
                background.yVelocity = 15
                player.xVelocity = 15
            } else {
                background.yVelocity = 15
                player.xVelocity = -15
            }

            // restart game
            if (event.x >= 0 && event.x <= width && event.y >= 3 * (height / 8) && event.y <= 4 * (height / 8) && isGameOver) {
                resetGame()
            }

            // go back to main menu
            if (event.x >= 0 && event.x <= width && event.y >= 5 * (height / 8) && event.y <= 6 * (height / 8) && isGameOver) {
                thread.setRunning(false)
                //                Intent intent = new Intent(getContext(), MenuActivity.class);
//                getContext().startActivity(intent);
            }
        }

        // stop the player from moving
        if (event.action == MotionEvent.ACTION_UP) {
            player.xVelocity = 0
        }
        return true
    }

    public override fun onDraw(canvas: Canvas?) {
//        System.out.println("Speed: " + speed);
        // prevent the player from going out of bounds
        if (player.xVelocity <= 0 && player.x <= 0) {
            player.xVelocity = 0
        } else if (player.xVelocity >= 0 && player.x >= width - 100) {
            player.xVelocity = 0
        }
        if (canvas != null) {
            updateAndDrawBackgroundImage(canvas)
            // canvas.drawColor(Color.BLUE);
            textColor.textSize = 50f
            canvas.drawText("Score: $score", 15f, 80f, textColor)
            canvas.drawText("High Score: $highScore", 15f, 130f, textColor)
            if (isGameOver) {
                textColor.textSize = 70f
                                canvas.drawText("GAME OVER", (width/ 2f) - 170f, (height / 4f) + 200f, textColor);
                val rect = Paint()
                rect.color = Color.GRAY
                canvas.drawRect(0F, (3 * (height / 7)).toFloat(),
                    width.toFloat(), (4 * (height / 7)).toFloat(), rect)
                canvas.drawText("Restart", ((width / 3)+80).toFloat(),
                    (7 * (height / 16)+180).toFloat(), textColor)
            } else {
                player.draw(canvas)
                synchronized(projectiles!!) {
                    try {
                        for (projectile in projectiles!!) {
                            projectile.draw(canvas)
                            if (projectile.y > height) {
                                val integer =
                                    Integer.valueOf(projectiles!!.indexOf(projectile))
                                projectilesToRemove!!.add(integer)
                            }
                        }
                    } catch (e: ConcurrentModificationException) {
                    }
                }
                for (integer in projectilesToRemove!!) {
                    score++
                    if (score % 10 == 0) {
                        if (speed >= 200) {
                            speed -= 100
                        }
                        t!!.cancel()
                        t = Timer()
                        t!!.scheduleAtFixedRate(object : TimerTask() {
                            override fun run() {
                                generateProjectile()
                            }
                        }, 0, speed.toLong())
                    }
                    projectiles!!.removeAt(integer)
                }
                projectilesToRemove!!.clear()
            }
            setHighScore(score)
        }
    }

    fun collisionsCheck(canvas: Canvas?) {
        val playerBitmap: Bitmap = player.bitmap
        try {
            for (projectile in projectiles!!) {
                val projectileBitmap: Bitmap = projectile.bitmap
                if (projectile.x < player.x + playerBitmap.width && projectile.x + projectileBitmap.width > player.x) {
                    if (projectile.y < player.y + playerBitmap.height && projectile.y + projectileBitmap.height > player.y) {
                        isGameOver = true
                        saveHighScore()
                    }
                }
            }
        } catch (e: ConcurrentModificationException) {
        }
    }

    fun updateAndDrawBackgroundImage(canvas:Canvas){
        background.y = background.y - background.yVelocity
        if (background.y < -background.bitmap.height){
            background.y = 0
        }
        canvas.drawBitmap(background.bitmap,background.x.toFloat(),background.y.toFloat(),null)
        if (background.y < - (background.bitmap.height - height)){
            canvas.drawBitmap(background.bitmap,background.x.toFloat(),background.y.toFloat() + background.bitmap.height,null)
        }
    }

    fun getHighScore(): Int {
        return highScore
    }

    fun setHighScore(newHighScore: Int) {
        if (newHighScore > highScore) {
            highScore = newHighScore
        }
    }

    fun saveHighScore() {
        setHighScore(score)
        val sharedPref = (context as Activity).getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("Saved HighScore", getHighScore())
        editor.apply()
    }

    fun resetGame() {
        projectiles!!.clear()
        projectilesToRemove!!.clear()
        isGameOver = false
        score = 0
        speed = 1000
        t!!.cancel()
        t = Timer()
        t!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                generateProjectile()
            }
        }, 0, speed.toLong())
    }


    fun generateProjectile() {
        val generator = Random()
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.cabriolet)
        val startingXPosition: Int = generator.nextInt(width - bitmap.width)
        val projectileBM = BitmapFactory.decodeResource(resources, R.drawable.police_car)
        val projectile = Projectile(projectileBM, startingXPosition, 0)
        projectiles!!.add(projectile)
    }

    init {
        holder.addCallback(this)
        thread = GameThread(holder, this)
        isFocusable = true
        player = Player(BitmapFactory.decodeResource(resources, R.drawable.cabriolet), 50, 50)
        background = BackgroundImage(BitmapFactory.decodeResource(resources,R.drawable.road),width,height)
        textColor = Paint()
        textColor.textSize = 20f
        textColor.color = Color.WHITE
        score = 0
        highScore = 0
        isGameOver = false
        speed = 1500
    }

}