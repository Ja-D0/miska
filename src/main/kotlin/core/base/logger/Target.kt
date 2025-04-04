package com.miska.core.base.logger

interface Target {
    fun collect(message: Message)
}