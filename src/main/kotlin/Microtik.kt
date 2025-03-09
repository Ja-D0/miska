package com.microtik

import com.microtik.core.base.ApplicationImpl
import java.io.File

object Microtik {
    lateinit var app: ApplicationImpl

    fun getBaseJarDir(): String =
        File(Microtik::class.java.protectionDomain.codeSource.location.toURI()).parentFile.path
}
