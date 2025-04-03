package com.microtik.core.base.logger

interface Target {
    fun collect(message: Message)
}