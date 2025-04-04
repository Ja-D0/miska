package com.miska

import com.miska.core.base.ApplicationImpl
import com.miska.core.base.logger.Logger
import com.miska.core.base.logger.LoggerImpl
import java.io.File

object Miska {
    lateinit var app: ApplicationImpl
    val logger: Logger = LoggerImpl()

    fun getBaseJarDir(): String {
        var str = Miska::class.java.protectionDomain.codeSource.location.toURI().toString()

        val matchResult = Regex("C:[^ ]*\\.jar").find(str)
        str = matchResult!!.value

        return File(str).parentFile.toString()
    }

    fun log(message: String, level: String) {
        logger.log(message, level)
    }

    fun http(message: String) {
        logger.log(message, "http")
    }

    fun alert(message: String) {
        logger.log(message, "alert")
    }

    fun info(message: String) {
        logger.log(message, "info")
    }

    fun error(message: String) {
        logger.log(message, "error")
    }
}
