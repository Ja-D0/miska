package com.microtik.core.commandLists

import com.microtik.core.api.MicrotikApiService
import com.microtik.core.api.requestModels.FirewallFilterPayload
import com.microtik.core.base.cli.CommandsListImpl
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandList
import com.microtik.core.base.cli.annotations.CommandOption
import com.microtik.core.base.cli.annotations.CommandType

@CommandList("filter")
class FilterCommandsList : CommandsListImpl() {
    @Command("print", CommandType.COMMAND, "Show the elements")
    fun commandPrint(): String =
        MicrotikApiService.runRequest {
            MicrotikApiService.getInstance().getFirewallFilterApi().print().execute()
        }.joinToString("\n") { it.toString() }

    @Command("add", CommandType.COMMAND, "Create a rule")
    fun commandAdd(
        @CommandOption("c", "chain", false, "Chain")
        chain: String = "forward",
        @CommandOption("a", "accept", false, "Action")
        action: String = "accept",
        @CommandOption("sa", "src-address", false, "Source address")
        srcAddress: String? = null,
        @CommandOption("da", "dst-address", false, "Destination address")
        dstAddress: String? = null,
        @CommandOption("sal", "src-address-List", false, "Source address list")
        srcAddressList: String? = null,
        @CommandOption("dal", "dst-address-list", false, "Destination address list")
        dstAddressList: String? = null,
        @CommandOption("p", "protocol", false, "Protocol")
        protocol: String? = null,
        @CommandOption("sp", "src-port", false, "Source port")
        srcPort: String? = null,
        @CommandOption("dp", "dst-port", false, "Destination port")
        dstPort: String? = null,
        @CommandOption("port", "port", false, "Any port")
        port: String? = null,
        @CommandOption("inI", "in-interface", false, "Incoming interface")
        inInterface: String? = null,
        @CommandOption("outI", "out-interface", false, "Outgoing interface")
        outInterface: String? = null,
        @CommandOption("inIL", "in-interface-list", false, "Incoming interface list")
        inInterfaceList: String? = null,
        @CommandOption("outIL", "out-interface-list", false, "Outgoing interface list")
        outInterfaceList: String? = null,
        @CommandOption("l", "log", false, "Logging")
        log: Boolean? = null,
        @CommandOption("lp", "log-prefix", false, "Log prefix")
        logPrefix: String? = null,
    ): String =
        MicrotikApiService.runRequest {
            MicrotikApiService.getInstance().getFirewallFilterApi().add(
                FirewallFilterPayload(
                    action,
                    chain,
                    srcAddress,
                    dstAddress,
                    srcAddressList,
                    dstAddressList,
                    protocol,
                    srcPort,
                    dstPort,
                    port,
                    inInterface,
                    outInterface,
                    inInterfaceList,
                    outInterfaceList,
                    log,
                    logPrefix
                )
            ).execute()
        }.toString()

    @Command("edit", CommandType.COMMAND, "Edit the rule")
    fun commandEdit(
        @CommandOption("i", "id", true, "Rule ID")
        id: String,
        @CommandOption("c", "chain", false, "Chain")
        chain: String? = null,
        @CommandOption("a", "accept", false, "Action")
        action: String? = null,
        @CommandOption("sa", "src-address", false, "Source address")
        srcAddress: String? = null,
        @CommandOption("da", "dst-address", false, "Destination address")
        dstAddress: String? = null,
        @CommandOption("sal", "src-address-List", false, "Source address list")
        srcAddressList: String? = null,
        @CommandOption("dal", "dst-address-list", false, "Destination address list")
        dstAddressList: String? = null,
        @CommandOption("p", "protocol", false, "Protocol")
        protocol: String? = null,
        @CommandOption("sp", "src-port", false, "Source port")
        srcPort: String? = null,
        @CommandOption("dp", "dst-port", false, "Destination port")
        dstPort: String? = null,
        @CommandOption("port", "port", false, "Any port")
        port: String? = null,
        @CommandOption("inI", "in-interface", false, "Incoming interface")
        inInterface: String? = null,
        @CommandOption("outI", "out-interface", false, "Outgoing interface")
        outInterface: String? = null,
        @CommandOption("inIL", "in-interface-list", false, "Incoming interface list")
        inInterfaceList: String? = null,
        @CommandOption("outIL", "out-interface-list", false, "Outgoing interface list")
        outInterfaceList: String? = null,
        @CommandOption("l", "log", false, "Logging")
        log: Boolean? = null,
        @CommandOption("lp", "log-prefix", false, "Log prefix")
        logPrefix: String? = null,
    ): String =
        MicrotikApiService.runRequest {
            MicrotikApiService.getInstance().getFirewallFilterApi().edit(
                id, FirewallFilterPayload(
                    action,
                    chain,
                    srcAddress,
                    dstAddress,
                    srcAddressList,
                    dstAddressList,
                    protocol,
                    srcPort,
                    dstPort,
                    port,
                    inInterface,
                    outInterface,
                    inInterfaceList,
                    outInterfaceList,
                    log,
                    logPrefix
                )
            ).execute()
        }.toString()

    @Command("disable", CommandType.COMMAND, "Turn off the rule")
    fun commandDisable(
        @CommandOption("i", "id", true, "ID Rules")
        id: String
    ): String =
        MicrotikApiService.runRequest {
            MicrotikApiService.getInstance().getFirewallFilterApi().edit(id, FirewallFilterPayload(disabled = true))
                .execute()
        }.toString()

    @Command("enable", CommandType.COMMAND, "Turn on the rule")
    fun commandEnable(
        @CommandOption("i", "id", true, "ID Rules")
        id: String
    ): String =
        MicrotikApiService.runRequest {
            MicrotikApiService.getInstance().getFirewallFilterApi().edit(id, FirewallFilterPayload(disabled = false))
                .execute()
        }.toString()

    @Command("remove", CommandType.COMMAND, "Remove the element")
    fun commandRemove(
        @CommandOption("i", "id", true, "Record number")
        id: String
    ): Unit =
        MicrotikApiService.runRequest { MicrotikApiService.getInstance().getFirewallFilterApi().remove(id).execute() }
}