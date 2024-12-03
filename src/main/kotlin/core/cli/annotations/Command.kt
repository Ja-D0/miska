package com.microtik.core.cli.annotations

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(val name: String, val commandType: CommandType, val description: String)
