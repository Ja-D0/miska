package com.miska.core.commandLists

import com.miska.Miska
import com.miska.core.base.cli.CommandsListImpl
import com.miska.core.base.cli.annotations.Command
import com.miska.core.base.cli.annotations.CommandList
import com.miska.core.base.cli.annotations.CommandType

@CommandList("server")
class ServerCommandsList : CommandsListImpl() {
    @Command(
        "start-analyze-alert-server",
        CommandType.COMMAND,
        "Launch the REST API server for analyze alerts"
    )
    fun commandRunAnalyzeAlertApi(): String = Miska.app.startAnalyzeAlertServer()

    @Command(
        "stop-analyze-alert-server",
        CommandType.COMMAND,
        "Stop the REST API server for analyze alerts"
    )
    fun commandStopAnalyzeAlertApi(): String = Miska.app.stopAnalyzeAlertServer()

    @Command(
        "reload-rules",
        CommandType.COMMAND,
        "Reloads a set of rules for comparison"
    )
    fun commandReloadRules() = Miska.app.reloadRulesForAnalyzeAlerts()
}