package com.microtik.core.base.cli.commandLists

import com.microtik.core.base.CommandsListImpl
import com.microtik.core.base.cli.annotations.Command
import com.microtik.core.base.cli.annotations.CommandList
import com.microtik.core.base.cli.annotations.CommandType

/**
 *
 */
@CommandList(".")
class RootCommandsList : CommandsListImpl() {

    /**
     *
     */
    @Command("config", CommandType.PATH, "")
    fun getConfigCommands(): ConfigCommandsList = ConfigCommandsList()

    /**
     *
     */
    @Command("address", CommandType.PATH, "")
    fun getAddressCommands(): AddressCommandsList = AddressCommandsList()

    /**
     *
     */
    @Command("ip", com.microtik.core.base.cli.annotations.CommandType.PATH, "")
    fun getIpCommands(): IpCommandsList = IpCommandsList()
}
