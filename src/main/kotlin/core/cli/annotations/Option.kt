package com.microtik.core.cli.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Option(
    val shortName: String,
    val longName: String,
    val required: Boolean,
    val description: String
)
