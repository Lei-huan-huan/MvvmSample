package com.lhh.mvvmsample.data.remote

import com.lhh.mvvmsample.data.local.ServerConfigStore
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class ServerConfigInterceptor @Inject constructor(
    private val serverConfigStore: ServerConfigStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val ip = serverConfigStore.getServerIp().trim()
        val port = serverConfigStore.getServerPort()
        if (ip.isEmpty() || port !in 1..65535) {
            return chain.proceed(request)
        }

        val newUrl = request.url.newBuilder()
            .host(ip)
            .port(port)
            .build()

        val newRequest = request.newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newRequest)
    }
}
