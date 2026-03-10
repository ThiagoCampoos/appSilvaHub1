package com.example.silvahub.ui.components

import com.example.silvahub.util.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}