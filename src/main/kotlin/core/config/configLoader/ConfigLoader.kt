package com.microtik.core.config.configLoader

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.microtik.Microtik
import com.microtik.core.config.application.Config
import com.microtik.core.config.exceptions.ConfigFileNotFoundException
import com.microtik.core.config.exceptions.ConfigSyntaxException
import com.microtik.core.config.exceptions.LoadConfigException
import java.io.File

class ConfigLoader : AbstractConfigLoader() {
    companion object {
        private const val DEFAULT_CONFIGS_PATH: String = "configs/"
    }

    override fun load(configFilePath: String?): Config {
        val dir = Microtik.getBaseJarDir()
        val filePath = (configFilePath ?: ("$dir\\" + DEFAULT_CONFIGS_PATH)) + "config.json"

        val configFile: File = File(filePath)

        if (!configFile.exists()) {
            throw ConfigFileNotFoundException("Файл конфигурации не найден: $filePath")
        }

        return try {
            Gson().fromJson(configFile.readText(), Config::class.java)
        } catch (exception: JsonSyntaxException) {
            throw ConfigSyntaxException("Неверный формат данных конфигурационного фала")
        } catch (exception: Exception) {
            throw LoadConfigException("Не удалось загрузить файл конфигурации")
        }
    }
}
