package com.miska.core.base.logger

interface Target {
    val levels: List<String>
    val categories: List<String>

    fun collect(message: Message)
}