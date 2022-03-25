package com.example.core

import junit.framework.TestCase

class CoreClassTest : TestCase() {

    fun testGetFeature() {
        val v = CoreClass().getFeature()
        assert(v == "CoreClass")
    }

    fun testGetFeature2() {
        val v = CoreClass().getFeature2()
        assert(v == "CoreClass2")
    }
}