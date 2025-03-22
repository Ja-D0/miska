package com.microtik.core.base.cli

import kotlin.reflect.KFunction

/**
 *  Класс, реализующий поведение встроенной в [CommandsListImpl] команды
 *
 *  @author Виктория Яковлева
 *  @since 0.0.1
 */
class InlineCommand(
    id: String,
    owner: CommandsListImpl,
    command: KFunction<Any?>
) : CommandImpl(id, owner, command)
