package net.bloople.manga

import android.net.Uri
import okhttp3.HttpUrl.Companion.toHttpUrl

class MangosUrl private constructor(private val url: String) {
    constructor(url: String, username: String?, password: String?) : this(
        if(!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            url.toHttpUrl().newBuilder().username(username).password(password).build().toString()
        }
        else {
            url.toHttpUrl().toString()
        }
    )

    operator fun div(other: String) = MangosUrl(url + "/" + Uri.encode(other))

    fun toHttpUrl() = url.toHttpUrl()
    fun build() = url
}