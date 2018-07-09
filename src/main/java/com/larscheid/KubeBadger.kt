package com.larscheid

import com.fasterxml.jackson.databind.JsonNode
import com.github.kittinunf.result.Result
import com.larscheid.json.JsonParser
import com.larscheid.kube.ApiClient
import java.io.IOException

class KubeBadger(private val apiClient: ApiClient, private val jsonParser: JsonParser) {
    fun generateBadge(kubeApiPath: String, jq: String, label: String? = null, color: String? = null) : Result<String, KubeBadgerError> {
        val kubeApiResponse = apiClient.getJson(kubeApiPath)

        if (!kubeApiResponse.isSuccessful) {
            return Result.error(KubeBadgerError(kubeApiResponse.code(), kubeApiResponse.body().string()))
        }

        val result = try {
            jsonParser.applyJq(kubeApiResponse.body().string(), jq)
        } catch (e: IOException) {
            return Result.error(KubeBadgerError(400, "Error executing jq command: ${e.message}"))
        }

        return Result.Success("https://img.shields.io/badge/${(label ?: kubeApiPath).sanitize()}-${result.niceString().sanitize()}-${color ?: "grey"}.svg")
    }
}

data class KubeBadgerError(val code: Int, override val message: String): Exception(message)

private fun List<JsonNode>.niceString(): String {
    if (size == 1) {
        return this[0].asText()
    } else {
        return this.toString()
    }
}

private fun String.sanitize(): String {
    return this.replace("-","--")
}