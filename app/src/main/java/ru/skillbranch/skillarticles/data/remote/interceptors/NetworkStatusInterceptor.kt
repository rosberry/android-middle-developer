package ru.skillbranch.skillarticles.data.remote.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import ru.skillbranch.skillarticles.data.remote.NetworkMonitor
import ru.skillbranch.skillarticles.data.remote.err.NoNetworkError

/**
 * @author mmikhailov on 20.08.2020.
 */
class NetworkStatusInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // return response or throw error
        if (!NetworkMonitor.isConnected) throw NoNetworkError()

        return chain.proceed(chain.request())
    }
}