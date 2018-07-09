package com.larscheid.json

import com.fasterxml.jackson.databind.ObjectMapper
import net.thisptr.jackson.jq.JsonQuery
import java.io.IOException

class JsonParser {
    private val mapper = ObjectMapper()

    @Throws(IOException::class)
    fun applyJq(json: String, jq: String) = JsonQuery.compile(jq).apply(mapper.readTree(json))
}