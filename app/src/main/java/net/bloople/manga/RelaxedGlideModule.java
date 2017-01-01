package net.bloople.manga;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.squareup.okhttp.OkHttpClient;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by i on 2/01/2017.
 */

public class RelaxedGlideModule implements GlideModule {
    public static final int TIMEOUT_SECONDS = 60;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        client.setReadTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        client.setWriteTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(client);

        glide.register(GlideUrl.class, InputStream.class, factory);
    }
}
