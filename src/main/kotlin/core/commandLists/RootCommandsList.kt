package com.miska.core.commandLists

import com.miska.core.base.cli.CommandsListImpl
import com.miska.core.base.cli.annotations.Command
import com.miska.core.base.cli.annotations.CommandList
import com.miska.core.base.cli.annotations.CommandType

@CommandList("./app")
class RootCommandsList : CommandsListImpl() {
    @Command("address", CommandType.PATH, "")
    fun getAddressCommands(): AddressCommandsList = AddressCommandsList()

    @Command("ip", CommandType.PATH, "")
    fun getIpCommands(): IpCommandsList = IpCommandsList()

    @Command("system", CommandType.PATH, "")
    fun getSystemCommands(): SystemCommandsList = SystemCommandsList()
}
