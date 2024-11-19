package com.microtik.core.config.configLoader

import com.google.gson.Gson
import com.microtik.core.config.application.Config
import java.io.File
import java.io.FileNotFoundException

class ConfigLoader: AbstractConfigLoader()
{
    companion object {
        private const val DEFAULT_CONFIGS_PATH: String = "configs/"
    }

    override fun load(configFilePath: String?): Config
    {
        val filePath = (configFilePath ?: DEFAULT_CONFIGS_PATH) + "config.json"

        val configFile: File = File(filePath)

        if (!configFile.exists()) {
            throw FileNotFoundException("Файл конфигурации не найден: $filePath")
        }

        return Gson().fromJson(configFile.readText(), Config::class.java)
    }
}