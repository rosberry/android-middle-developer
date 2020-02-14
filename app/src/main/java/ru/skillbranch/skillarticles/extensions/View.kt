package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins

/**
 * @author mmikhailov on 2020-02-14.
 */
fun View.setMarginOptionally(
        start: Int = marginStart,
        top: Int = marginTop,
        end: Int = marginEnd,
        bottom: Int = marginBottom
) {
    this.updateLayoutParams<ViewGroup.MarginLayoutParams> { updateMargins(start, top, end, bottom) }
}