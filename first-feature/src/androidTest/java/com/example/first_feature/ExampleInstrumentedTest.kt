package com.example.first_feature

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        Thread.sleep(5000)
        println("FIRST_FEATURE: TEST1")
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.first_feature.test", appContext.packageName)
    }

    @Test
    fun useAppContext2() {
        // Context of the app under test.
        Thread.sleep(5000)
        println("FIRST_FEATURE: TEST2")
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.first_feature.test", appContext.packageName)
    }
}