package ru.radixit.letsplay.utils

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.Collections
private var cookiesGlobal: List<Cookie>? = null

class SessionCookieJar : CookieJar {
    override fun saveFromResponse(
        url: HttpUrl,
        cookies: List<Cookie>
    ) {
        cookiesGlobal = null

        if (url.encodedPath.endsWith("new") && cookiesGlobal == null) {
            cookiesGlobal = ArrayList(cookies)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return if (url.encodedPath.endsWith("new") && cookiesGlobal != null) {
            cookiesGlobal!!
        } else Collections.emptyList()
    }
}