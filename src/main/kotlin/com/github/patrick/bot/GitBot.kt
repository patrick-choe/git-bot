package com.github.patrick.bot

import com.github.patrick.bot.command.Command
import com.github.patrick.bot.command.GitHubCommand
import discord4j.core.DiscordClient
import discord4j.core.DiscordClientBuilder
import discord4j.core.event.domain.message.MessageCreateEvent

object GitBot {
    private var commands = HashMap<String, Command>()
    private var botClient: DiscordClient ?= null
	lateinit var token: String

    @JvmStatic
    fun main(args: Array<String>) {
        commands["github"] = GitHubCommand()

        val discordClient = DiscordClientBuilder(args[0]).build().also { client ->
            Thread { client.eventDispatcher.on(MessageCreateEvent::class.java).subscribe { event ->
                val message = event.message
                val author = message.author
                Thread {
                    if (author.orElse(null) != null && message.content.isPresent)
                        if (message.attachments.isNullOrEmpty())
                            for ((key, value) in commands)
                                if (message.content.get().startsWith("!$key", true)) {
                                    Thread { value.execute(event) }.start()
                                    break
                                }
                }.start()
            } }.start()
        }
        botClient = discordClient
        discordClient.login().block()
    }
}