package com.miska.core.commandLists

import com.miska.core.base.cli.CommandsListImpl
import com.miska.core.base.cli.annotations.Command
import com.miska.core.base.cli.annotations.CommandList
import com.miska.core.base.cli.annotations.CommandType

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
    @Command("ip", CommandType.PATH, "")
    fun getIpCommands(): IpCommandsList = IpCommandsList()
}
