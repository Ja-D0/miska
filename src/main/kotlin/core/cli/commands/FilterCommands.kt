package com.microtik.core.cli.commands

import com.microtik.core.api.MicrotikApiService
import com.microtik.core.api.endpoints.FirewallFilterApi
import com.microtik.core.api.exceptions.FailedRequest
import com.microtik.core.api.requestModels.FirewallFilterPut
import com.microtik.core.cli.annotations.Command
import com.microtik.core.cli.annotations.CommandType
import com.microtik.core.cli.annotations.Option

class FilterCommands: AbstractCommands() {
    override val path: String = "filter"
    override val apiService: FirewallFilterApi = MicrotikApiService.getInstance().getFirewallFilterApi()

    @Command("print", CommandType.COMMAND, "")
    fun commandPrint(): String
    {
        var result: String = ""

        val response = apiService.print().execute()

        if (response.isSuccessful && response.body() != null) {
            result = response.body()!!.joinToString("\n") { it.toString() }
        } else {
            throw FailedRequest(response.code(), response.body().toString(), response.message())
        }

        return result
    }

    @Command("add", CommandType.COMMAND, "")
    fun commandAdd(
        @Option("c", "chain", false, "Цепь")
        chain: String = "forward",
        @Option("a", "accept", false, "Действие")
        action: String = "accept"
    ): String
    {
        var result: String = ""

        val response = apiService.add(FirewallFilterPut(chain, action)).execute()

        if (response.isSuccessful && response.body() != null) {
            result = response.body()!!.toString()
        } else {
            throw FailedRequest(response.code(), response.body().toString(), response.message())
        }

        return result
    }
}