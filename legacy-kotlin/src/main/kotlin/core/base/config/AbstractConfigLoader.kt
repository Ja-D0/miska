package com.miska.core.base.config

/**
 * Класс загружающий конфигурацию приложения
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
abstract class AbstractConfigLoader {

    /**
     * Загружает конфигурацию приложения.
     *
     * @param [configFilePath] путь до файла конфигурации
     * @return экземпляр [AbstractConfig]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    abstract fun load(configFilePath: String?): AbstractConfig
}
