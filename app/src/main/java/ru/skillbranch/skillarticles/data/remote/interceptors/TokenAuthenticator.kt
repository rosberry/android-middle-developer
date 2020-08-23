package ru.skillbranch.skillarticles.data.remote.interceptors

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkManager.api
import ru.skillbranch.skillarticles.data.remote.req.RefreshReq

class TokenAuthenticator : Authenticator {

    private val prefs = PrefManager

    override fun authenticate(route: Route?, response: Response): Request? {
        return if (response.code == 401) {
            val req = RefreshReq(prefs.refreshToken)
            val res = api.refreshAccessToken(req)
                .execute()

            return if (res.isSuccessful) {
                prefs.accessToken = "Bearer ${res.body()!!.accessToken}"
                prefs.refreshToken = res.body()!!.refreshToken
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${res.body()!!.accessToken} ")
                    .build()
            } else null
        } else null
    }
}