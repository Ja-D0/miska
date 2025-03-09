package com.microtik.core.base.interfaces

/**
 *
 */
interface Command {

    /**
     *
     */
    fun runCommand(options: ArrayList<String>): Any?
}