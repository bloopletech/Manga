package net.bloople.manga

import android.net.Uri
import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import android.util.Base64
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import okhttp3.Request

open class MangosUrl(private val url: String, private val credential: String? = null) : Parcelable {
    constructor(url: String, username: String?, password: String?) : this(
        url,
        if(username != null && password != null) {
            Base64.encodeToString("$username:$password".toByteArray(), Base64.NO_WRAP)
        }
        else {
            null
        }
    )

    operator fun div(other: String): MangosUrl {
        return MangosUrl(url + "/" + Uri.encode(other), credential)
    }

    fun toOkHttpRequest(): Request {
        val builder = Request.Builder().url(url)
        if(credential != null) builder.header("Authorization", "Basic $credential")
        return builder.build()
    }

    fun loadInto(builder: ImageRequest.Builder): ImageRequest.Builder {
        return builder.apply {
            data(url)
            if(credential != null) {
                httpHeaders(NetworkHeaders.Builder().add("Authorization", "Basic $credential").build())
            }
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
        dest.writeString(credential)
    }

    protected constructor(input: Parcel) : this(input.readString()!!, input.readString())

    companion object {
        @JvmField
        val CREATOR: Creator<MangosUrl> = object : Creator<MangosUrl> {
            override fun createFromParcel(source: Parcel): MangosUrl {
                return MangosUrl(source)
            }

            override fun newArray(size: Int): Array<MangosUrl?> {
                return arrayOfNulls(size)
            }
        }
    }
}