package com.example.testimpactanalysislib

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.core.CoreClass

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text = CoreClass().getFeature()
        // le
        val v = 5
        Log.d("ROMAN", "core: $text")
    }

    companion object {
        fun getStr(): String {
            Thread.sleep(5000)
            return "MainActivity"
        }
    }

}