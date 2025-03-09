package com.microtik.core.base.cli.interfaces

/**
 *
 */
interface Command {

    /**
     *
     */
    fun runCommand(options: ArrayList<String>): Any?
}