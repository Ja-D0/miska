package com.microtik

import com.microtik.core.base.interfaces.Application
import java.io.File

object Microtik {
    lateinit var app: Application

    fun getBaseJarDir(): String {
        var str = Microtik::class.java.protectionDomain.codeSource.location.toURI().toString()

        val matchResult = Regex("C:[^ ]*\\.jar").find(str)
        str = matchResult!!.value

        return File(str).parentFile.toString()
    }
}
