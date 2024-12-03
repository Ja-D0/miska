package com.microtik

import com.microtik.core.application.AbstractApplication
import java.io.File

object Microtik {
    lateinit var app: AbstractApplication

    fun getBaseJarDir(): String = File(this::class.java.protectionDomain.codeSource.location.toURI()).parentFile.toString()
}