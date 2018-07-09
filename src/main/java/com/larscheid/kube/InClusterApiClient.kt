package com.larscheid.kube

import com.squareup.okhttp.Response
import io.ktor.http.HttpMethod
import io.kubernetes.client.util.Config

class InClusterApiClient() : ApiClient {
    private val client = Config.fromCluster()

    override fun getJson(kubeApiPath: String): Response =
            client.buildCall(
                    kubeApiPath,
                    HttpMethod.Get.value,
                    null,
                    null,
                    null,
                    mutableMapOf(),
                    null,
                    listOf<String>("BearerToken").toTypedArray(),
                    null)
                    .execute()
}