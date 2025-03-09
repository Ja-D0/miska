package com.microtik.core.base.interfaces

/**
 * Интерфейс предоставляет объекту возможность загружать конфигурационный файл
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
interface Configurable {

    /**
     * Загружает файл конфигурации по пути [configFilePath]
     *
     * @param configFilePath путь до файла конфигурации
     * @author Денис Чемерис
     * @since 0.0.1
     */
    fun loadConfig(configFilePath: String?): Unit
}
