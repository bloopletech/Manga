package net.bloople.manga

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import okhttp3.OkHttpClient

class MangaApplication : Application(), SingletonImageLoader.Factory {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory({ okHttpClient }))
            }
            .build()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
            private set

        val okHttpClient = OkHttpClient.Builder().addInterceptor(HttpAuthInterceptor()).build()
    }
}