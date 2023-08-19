package ru.radixit.letsplay.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import ru.radixit.letsplay.utils.UnsafeOkHttpClient.unsafeOkHttpClient
import java.io.InputStream


@GlideModule
class UnsafeOkHttpGlideModule : com.bumptech.glide.module.GlideModule {
    override fun applyOptions(context: Context, builder: GlideBuilder) {}
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = unsafeOkHttpClient
        registry.replace(GlideUrl::class.java, InputStream::class.java,
            OkHttpUrlLoader.Factory(client))
    }
}