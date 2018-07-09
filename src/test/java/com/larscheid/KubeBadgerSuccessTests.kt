package com.larscheid

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import com.larscheid.KubeBadgerErrorTests.Companion.mockApi
import com.larscheid.json.JsonParser
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.squareup.okhttp.Response
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import org.mockito.Mockito

@RunWith(Parameterized::class)
class KubeBadgerSuccessTests(private val apiPath: String,
                             private val jqResult: String,
                             private val label: String?,
                             private val color: String?,
                             private val expected: String) {

    companion object {
        @JvmStatic
        @Parameters
        fun data(): Iterable<Array<String?>> = listOf(
                //api path, jq result, label, color, expected result
                listOf("api/dummy", "jq_result", null, null, "https://img.shields.io/badge/api/dummy-jq_result-grey.svg").toTypedArray(),
                listOf("api/dummy", "jq_result", "my_label", null, "https://img.shields.io/badge/my_label-jq_result-grey.svg").toTypedArray(),
                listOf("api/dummy", "jq_result", null, "green", "https://img.shields.io/badge/api/dummy-jq_result-green.svg").toTypedArray(),
                listOf("api/needs-sanitization", "result-needs-sanitization", null, null, "https://img.shields.io/badge/api/needs--sanitization-result--needs--sanitization-grey.svg").toTypedArray(),
                listOf("api/dummy", "jq_result", "label-needs-sanitization", null, "https://img.shields.io/badge/label--needs--sanitization-jq_result-grey.svg").toTypedArray()
        ).asIterable()
    }



    @Test
    fun shouldGenerateBadgeURL() {
        val jsonResponse = mock<Response>(defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on { isSuccessful } doReturn true
            on { body().string() } doReturn "dummy-json"
        }

        val successfulParser = mock<JsonParser> {
            on { applyJq("dummy-json", "dummy-jq") } doReturn listOf<JsonNode>(TextNode(jqResult))
        }

        val result = KubeBadger(mockApi(jsonResponse), successfulParser).generateBadge(apiPath, "dummy-jq", label, color)

        assertNull(result.component2())
        assertNotNull(result.component1())
        assertThat(result.component1(), equalTo(expected))
    }
}