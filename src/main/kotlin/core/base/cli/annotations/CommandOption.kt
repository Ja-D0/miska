package com.miska.core.base.cli.annotations

/**
 * Аннотация для определения основных параметров опции команды
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandOption(
    /**
     * Определяет короткое наименование опции
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    val shortName: String,
    /**
     * Определяет полное название опции
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    val longName: String,
    /**
     * Определяет, является ли опция обязательной для указания
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    val required: Boolean,
    /**
     * Определяет описание опции
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    val description: String
)
