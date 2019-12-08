package ru.skillbranch.data.interceptors

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


/**
 * @author neestell on 2019-12-08.
 */
class ClientInfoInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
            .newBuilder()
            .header("Accept", "application/vnd.anapioficeandfire+json; version=1")
            .build()
        return chain.proceed(request)
    }
}