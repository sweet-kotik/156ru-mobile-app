package com.example.a156ru

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(
        context: Context,
        glide: Glide,
        registry: Registry
    ) {
        // Регистрируем SvgDecoder для обработки InputStream → Bitmap
        registry.prepend(
            InputStream::class.java,
            Bitmap::class.java,
            SvgDecoder()
        )
    }
}