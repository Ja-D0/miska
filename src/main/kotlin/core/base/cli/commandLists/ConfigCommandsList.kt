package com.microtik.core.base.cli.commandLists

import com.microtik.Microtik
import com.microtik.core.base.CommandsListImpl
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandList
import com.microtik.core.base.cli.annotations.CommandOption
import com.microtik.core.base.cli.annotations.CommandType
import com.microtik.core.spring.SpringApplication
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext

@CommandList("config")
class ConfigCommandsList : CommandsListImpl() {
    private var server: ConfigurableApplicationContext? = null

    @Command("print", CommandType.COMMAND, "")
    fun commandPrint(): String = Microtik.app.getConfig().toString()

    @Command("run-rest", CommandType.COMMAND, "Launch or stop the REST API server")
    fun commandRunRest(
        @CommandOption("v", "value", true, "On/Off")
        value: Boolean
    ): String {
        return if (value) {
            server = runApplication<SpringApplication>()
            "OK"
        } else {
            server?.stop()
            server = null
            "OK"

        }
    }
}