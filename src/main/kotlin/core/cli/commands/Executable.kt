package com.microtik.core.cli.commands

interface Executable {
    fun execute(command: String): Any?
}