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
        action: String = "accept",
        @Option("sa", "src-address", false, "Адрес источника")
        srcAddress: String? = null,
        @Option("da", "dst-address", false, "Адрес назначения")
        dstAddress: String? = null,
        @Option("sal", "src-address-List", false, "Список адресов источника")
        srcAddressList: String? = null,
        @Option("dal", "dst-address-list", false, "Список адресов назначения")
        dstAddressList: String? = null,
        @Option("p", "protocol", false, "Протокол")
        protocol: String? = null,
        @Option("sp", "src-port", false, "Порт источника")
        srcPort: String? = null,
        @Option("dp", "dst-port", false, "Порт назначения")
        dstPort: String? = null,
        @Option("port", "port", false, "Любой порт")
        port: String? = null,
        @Option("inI", "in-interface", false, "Входной интерфейс")
        inInterface: String? = null,
        @Option("outI", "out-interface", false, "Выходной интерфейс")
        outInterface: String? = null,
        @Option("inIL", "in-interface-list", false, "Список входных интерфейсов")
        inInterfaceList: String? = null,
        @Option("outIL", "out-interface-list", false, "Список выходных интерфейсов")
        outInterfaceList: String? = null,
        @Option("l", "log", false, "Логирование")
        log: Boolean? = null,
        @Option("lp", "log-prefix", false, "Префикс лога")
        logPrefix: String? = null,
    ): String
    {
        var result: String = ""

        val response = apiService.add(
            FirewallFilterPut(action, chain,  srcAddress, dstAddress, srcAddressList, dstAddressList, protocol, srcPort,
                dstPort, port, inInterface, outInterface, inInterfaceList, outInterfaceList, log, logPrefix
            )
        ).execute()

        if (response.isSuccessful && response.body() != null) {
            result = response.body()!!.toString()
        } else {
            throw FailedRequest(response.code(), response.body().toString(), response.message())
        }

        return result
    }

    @Command("remove", CommandType.COMMAND, "Удалить адрес")
    fun commandRemove(
        @Option("i", "id", true, "Номер записи")
        id: String
    ): String
    {
        val response = apiService.remove(id).execute()

        if (!response.isSuccessful) {
            println(response.message())
            throw FailedRequest(response.code(), response.body().toString(), response.message())
        }

        return ""
    }
}