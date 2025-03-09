package com.microtik.core.base.cli

import kotlin.reflect.KFunction

/**
 *
 */
class InlineCommand(
    id: String,
    owner: CommandsListImpl,
    command: KFunction<Any?>
) : CommandImpl(id, owner, command)
