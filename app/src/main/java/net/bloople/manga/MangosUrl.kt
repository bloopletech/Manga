package net.bloople.manga

import android.net.Uri
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl

open class MangosUrl(private val url: String, private val credential: Credential?) {
    data class Credential(val username: String, val password: String)

    constructor(url: String, username: String?, password: String?) : this(
        url,
        if(!username.isNullOrEmpty() && !password.isNullOrEmpty()) Credential(username, password) else null
    )

    operator fun div(other: String): MangosUrl {
        return MangosUrl(url + "/" + Uri.encode(other), credential)
    }

    fun toHttpUrl(): HttpUrl {
        val httpUrl = url.toHttpUrl()
        credential?.let { return httpUrl.newBuilder().username(it.username).password(it.password).build() }
        return httpUrl
    }

    override fun toString() = toHttpUrl().toString()
}