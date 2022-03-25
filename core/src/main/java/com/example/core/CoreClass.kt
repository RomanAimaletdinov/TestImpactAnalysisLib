package com.example.core

import android.util.Log

class CoreClass {

    fun getFeature(): String {
        Thread.sleep(5000)
        // ch
        return "CoreClass"
    }

    fun getFeature2(): String {
        Thread.sleep(5000)
        return "CoreClass2"
    }
}