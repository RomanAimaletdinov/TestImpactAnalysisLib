package com.example.testimpactanalysislib

import junit.framework.TestCase

class MainActivityTest : TestCase() {

    fun testGetStr() {
        assert(MainActivity.getStr() == "MainActivity")
    }
}