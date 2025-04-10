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

    fun log(message: String, level: String, category: String = "*") {
        logger.log(message, level, category)
    }

    fun http(message: String, category: String = "*") {
        log(message, "http", category)
    }

    fun alert(message: String, category: String = "*") {
        log(message, "alert", category)
    }

    fun info(message: String, category: String = "*") {
        log(message, "info", category)
    }

    fun error(message: String, category: String = "*") {
        log(message, "error", category)
    }
}
