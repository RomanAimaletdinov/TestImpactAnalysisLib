package com.example.first_feature

import junit.framework.TestCase

class FirstFeatureTest : TestCase() {

    fun testGetFeature() {
        val v = FirstFeature().getFeature()
        assert(v == "getFeature")
    }

    fun testGetFeature2() {
        val v = FirstFeature().getFeature2()
        assert(v == "getFeature2")
    }
}