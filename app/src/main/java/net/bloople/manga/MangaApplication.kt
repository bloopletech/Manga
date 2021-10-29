package net.bloople.manga

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.ImageDecoderDecoder

class MangaApplication : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext).componentRegistry {
            add(ImageDecoderDecoder(applicationContext))
        }.build()
    }
}