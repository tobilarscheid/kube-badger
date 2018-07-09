package com.larscheid

import com.larscheid.json.JsonParser
import com.larscheid.kube.ApiClient
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.mock
import com.squareup.okhttp.Response
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito
import java.io.IOException


internal class KubeBadgerErrorTests {
    @Test
    fun shouldReportFailingKubeApi() {
        val erroneousResponse = mock<Response>(defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on { isSuccessful } doReturn false
            on { code() } doReturn 500
            on { body().string() } doReturn "Dummy-Error"
        }

        val result = KubeBadger(mockApi(erroneousResponse), mock()).generateBadge("", "")

        assertNull(result.component1())
        assertNotNull(result.component2())
        assertThat(result.component2()?.message, equalTo("Dummy-Error"))
        assertThat(result.component2()?.code, equalTo(500))
    }

    @Test
    fun shouldReportErroneousJqString() {
        val jsonResponse = mock<Response>(defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
            on { isSuccessful } doReturn true
            on { body().string() } doReturn "dummy-json"
        }

        val erroneousParser = mock<JsonParser> {
            on { applyJq("dummy-json", "dummy-jq") } doThrow IOException("dummy error in jq string")
        }

        val result = KubeBadger(mockApi(jsonResponse), erroneousParser).generateBadge("", "dummy-jq")

        assertNull(result.component1())
        assertNotNull(result.component2())
        assertThat(result.component2()?.message, equalTo("Error executing jq command: dummy error in jq string"))
        assertThat(result.component2()?.code, equalTo(400))
    }

    companion object {
        fun mockApi(response: Response) = mock<ApiClient> {
            on { getJson(any()) } doReturn response
        }
    }
}