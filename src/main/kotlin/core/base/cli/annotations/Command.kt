package com.miska.core.base.cli.annotations

/**
 * Аннотация для определения основных параметров команды
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Command(
    /**
     * Уникальный идентификатор команды
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    val id: String,

    /**
     * Указывает тип команды
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    val commandType: CommandType,

    /**
     * Описание команды для пользователя
     *
     * @author Денис Чемерис
     * @since 0.0.1
     */
    val description: String
)
