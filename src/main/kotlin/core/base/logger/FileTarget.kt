package com.miska.core.base.logger

import com.miska.Miska
import com.miska.core.base.cli.exceptions.ApplicationException
import java.io.File
import java.io.FileWriter
import java.io.IOException

class FileTarget(
    val logFileName: String,
    val logFilePath: String,
    override val levels: List<String>,
    override val categories: List<String>
) : Target {
    private var file: File

    init {
        val baseDir = Miska.getBaseJarDir()

        val file = File(baseDir + File.separator + logFilePath + logFileName)

        if (!file.parentFile.exists()) {
            try {
                file.parentFile.mkdirs()
                println("The Directory ${file.parentFile.absolutePath} was created.")
            } catch (e: SecurityException) {
                println("Safety error when creating a directory: ${e.message}")
            }
        }

        if (!file.exists()) {
            try {
                file.createNewFile()
                println("File ${file.absoluteFile} was created.")
            } catch (e: IOException) {
                println("Error when creating a file: ${e.message}")
            }
        }

        if (file.exists()) {
            this.file = file
        } else {
            throw ApplicationException("Failed to initialize the logistics file: ${file.absolutePath}")
        }
    }

    @Synchronized
    override fun collect(message: Message) {
        try {
            val logEntry = "[${message.time}][${message.category}][${message.level.uppercase()}] ${message.message}\n"
            FileWriter(file, true).use { writer -> writer.write(logEntry) }
        } catch (e: IOException) {
            Miska.error("There was an error when writing to a file ${file.absoluteFile}")
        }
    }
}