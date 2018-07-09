package com.larscheid.kube

import com.squareup.okhttp.Response

interface ApiClient {
    fun getJson(kubeApiPath: String): Response
}