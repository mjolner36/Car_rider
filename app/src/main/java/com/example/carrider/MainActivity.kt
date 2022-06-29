package com.example.carrider

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var scoreTextView = findViewById<TextView>(R.id.scoreTextView)
        scoreTextView.text = "Score: ${getSharedPreferences("GameActivity",Context.MODE_PRIVATE).getInt("Saved HighScore",0)}"
    }

    fun play(view: View){
        val intent = Intent(this@MainActivity,GameActivity::class.java)
        startActivity(intent)
    }
}