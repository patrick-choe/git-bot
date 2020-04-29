package com.github.patrick.bot.command

import com.github.patrick.bot.GitBot.token
import discord4j.core.event.domain.message.MessageCreateEvent
import org.json.simple.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class GitHubCommand() : Command {
    override fun execute(event: MessageCreateEvent) {
        try {
            val content = event.message.content.get()
            var args = (if (content.contains(" ")) content.substring(content.indexOf(" ") + 1) else "").split(" ").toMutableList()
            if (args.isEmpty()) return
            else {
                args.forEachIndexed { count, argument ->
                    args[count] = argument.replace("!!", " ")
                }
                when (args[0]) {
                    "repository" -> {
                        println("repository executed")
                        args = args.drop(1).toMutableList()
                        when (args[0]) {
                            "create" -> {
                                println("create executed")
                                if (args.size < 2) return
                                (URL("https://api.github.com/orgs/Team-IF/repos").openConnection() as HttpsURLConnection).run {
                                    requestMethod = "POST"
                                    doOutput = true
                                    doInput = true
                                    setRequestProperty("Authorization", "token $token")
                                    outputStream?.apply {
                                        write((JSONObject().also {
                                            it["name"] = args[1]
                                            if (args.size > 2) it["description"] = args[2]
                                            if (args.size > 3) it["homepage"] = args[3]
                                            if (args.size > 4) it["private"] = args[4].toBoolean()
                                            if (args.size > 5) it["gitignore_template"] = args[5]
                                            if (args.size > 6) it["license_template"] = args[6]
                                            if (args.size > 7) it["auto_init"] = args[7].toBoolean()
                                            if (args.size > 8) it["is_template"] = args[8].toBoolean()
                                            if (args.size > 9) it["team_id"] = args[9].toInt()
                                            if (args.size > 10) it["has_issues"] = args[10].toBoolean()
                                            if (args.size > 11) it["has_projects"] = args[11].toBoolean()
                                            if (args.size > 12) it["has_wiki"] = args[12].toBoolean()
                                            if (args.size > 13) it["allow_squash_merge"] = args[13].toBoolean()
                                            if (args.size > 14) it["allow_merge_commit"] = args[14].toBoolean()
                                            if (args.size > 15) it["allow_rebase_merge"] = args[15].toBoolean()
                                            if (args.size > 16) it["delete_branch_on_merge"] = args[16].toBoolean()
                                        }).toJSONString().toByteArray())
                                        flush()
                                        close()
                                    }
                                    inputStream.close()
                                    disconnect()

                                }
                            }
                            "remove" -> {
                                if (args.size < 2) return
                                (URL("https://api.github.com/repos/Team-IF/${args[1]}").openConnection() as HttpsURLConnection).run {
                                    requestMethod = "DELETE"
                                    doOutput = true
                                    doInput = true
                                    setRequestProperty("Authorization", "token $token")
                                    outputStream?.apply {
                                        flush()
                                        close()
                                    }
                                    inputStream.close()
                                    disconnect()
                                }
                            }
                        }
                    }
                }
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}
