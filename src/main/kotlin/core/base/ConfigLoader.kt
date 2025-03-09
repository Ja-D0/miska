package com.microtik.core.base

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.microtik.Microtik
import com.microtik.core.base.config.AbstractConfigLoader
import com.microtik.core.base.config.exceptions.ConfigFileNotFoundException
import com.microtik.core.base.config.exceptions.ConfigSyntaxException
import com.microtik.core.base.config.exceptions.LoadConfigException
import java.io.File

/**
 * Класс загружающий конфигурацию приложения
 *
 * @author Денис Чемерис
 * @since 0.0.1
 */
class ConfigLoader : AbstractConfigLoader() {
    companion object {
        private const val DEFAULT_CONFIGS_PATH: String = "configs/config.json"
    }

    /**
     * Загружает конфигурацию приложения. Если [configFilePath] не указан, то приложение будет пытаться
     * найти конфигурационный файл по пути по умолчанию [DEFAULT_CONFIGS_PATH]. Если файл отсутствует, приложение
     * запустится с конфигурацией по умолчанию. Если [configFilePath] указан, приложение попробует загрузить конфигурацию
     * из него.
     *
     * @param [configFilePath] путь до файла конфигурации
     * @return экземпляр [Config]
     * @throws [ConfigFileNotFoundException] если файл конфигурации по пути [configFilePath] не найден
     * @throws [ConfigSyntaxException] если файл конфигурации [configFilePath] содержит неверный формат данных
     * @throws [LoadConfigException] если у приложения не получилось загрузить файл конфигурации [configFilePath]
     * @author Денис Чемерис
     * @since 0.0.1
     */
    override fun load(configFilePath: String?): Config {
        var filePath = configFilePath
        val dir = Microtik.getBaseJarDir()

        if (filePath == null) {
            if (File(dir, DEFAULT_CONFIGS_PATH).exists()) {
                filePath = DEFAULT_CONFIGS_PATH
            } else {
                return Config()
            }
        }

        val configFile = File(dir, filePath)

        if (!configFile.exists()) {
            throw ConfigFileNotFoundException("The configuration file was not found: ${configFile.path}")
        }

        return try {
            Gson().fromJson(configFile.readText(), Config::class.java)
        } catch (exception: JsonSyntaxException) {
            throw ConfigSyntaxException("Incorrect format of the configuration file data")
        } catch (exception: Exception) {
            throw LoadConfigException("Failed to download the configuration file")
        }
    }
}
