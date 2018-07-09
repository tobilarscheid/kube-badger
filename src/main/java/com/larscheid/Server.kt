package com.larscheid

import com.larscheid.json.JsonParser
import com.larscheid.kube.InClusterApiClient
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.param
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class Server {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val kubeBadger = KubeBadger(InClusterApiClient(), JsonParser())

            embeddedServer(Netty, 8080) {
                routing {
                    param("jq") {
                        get("/badge/{path...}") {
                            kubeBadger.generateBadge(
                                    call.parameters.getAll("path")?.joinToString(separator = "/", prefix = "/") ?: "",
                                    context.request.queryParameters.get("jq")!!, //existence enforced by route
                                    context.request.queryParameters.get("label"),
                                    context.request.queryParameters.get("color")
                            ).fold(
                                    success = {
                                        call.respondRedirect(it)
                                    },
                                    failure = {
                                        call.respondText(
                                                text = it.message,
                                                status = HttpStatusCode.fromValue(it.code),
                                                contentType = ContentType.Text.Any
                                        )
                                    }
                            )
                        }
                    }
                }
            }.start(wait = true)
        }
    }
}