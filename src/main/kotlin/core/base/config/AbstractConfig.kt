package com.microtik.core.base.config

import com.google.gson.annotations.SerializedName
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

abstract class AbstractConfig {
    override fun toString(): String {
        return this::class.simpleName!!.lowercase() + ":\n" + toStringWithIndentation()
    }

    private fun toStringWithIndentation(level: Int = 1): String {
        val properties = this::class.declaredMemberProperties
        val tabSpaces = " ".repeat(2)

        return properties.joinToString("\n") { prop ->
            val value = prop.getter.call(this@AbstractConfig)
            val name = prop.javaField!!.getAnnotation(SerializedName::class.java)?.value ?: prop.name

            if (value is AbstractConfig) {
                "${tabSpaces.repeat(level)}- $name:\n${value.toStringWithIndentation(level + 1)}"
            } else {
                "${tabSpaces.repeat(level)}- $name=$value"
            }

        }
    }
}
