package ru.skillbranch.skillarticles.data.providers

import android.net.Uri
import androidx.core.content.FileProvider

/**
 * @author mmikhailov on 30.08.2020.
 */
class ImageFileProvider : FileProvider() {
    override fun getType(uri: Uri): String? {
        return "image/jpeg"
    }
}