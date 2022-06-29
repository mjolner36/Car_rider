package com.example.carrider

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var gv = GameView(this)
        setContentView(gv)
        window.statusBarColor = ContextCompat.getColor(this, R.color.gray);
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val savedScore = sharedPref.getInt("Saved HighScore", 0)
        gv.setHighScore(savedScore)
    }
    override fun onBackPressed() {
        onDestroy()
        val intent = Intent(this@GameActivity,MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }


}