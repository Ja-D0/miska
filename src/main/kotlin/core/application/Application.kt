package com.microtik.core.application

interface Application {
    fun run(): Unit
    fun processCommand(command: String?): Unit
    fun start(): Unit
    fun stop(): Unit
}
