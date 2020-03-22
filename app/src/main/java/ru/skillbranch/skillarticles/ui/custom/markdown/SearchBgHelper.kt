package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.Spanned

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
class SearchBgHelper(
    context: Context,
    private val focusListener: ((Int, Int) -> Unit)? = null,
    mockDrawable: Drawable? = null //for mock drawable
) {

    constructor(context: Context, focusListener: ((Int, Int) -> Unit)) : this(
        context,
        focusListener,
        null
    )

    private val padding: Int  //4dp
    private val borderWidth: Int //1dp
    private val radius: Float = //8dp

    private val secondaryColor: Int //colorSecondary
    private val alphaColor: Int = //colorSecondary with 160 alpha

    private val drawable: Drawable

    private val drawableLeft: Drawable
    private val drawableMiddle: Drawable
    private val drawableRight: Drawable

    private lateinit var render: SearchBgRender
    private val singleLineRender: SearchBgRender
    private val multiLineRender: SearchBgRender

    fun draw(canvas: Canvas, text: Spanned, layout: Layout) {
        //TODO implement me
    }
}


abstract class SearchBgRender(
    val padding: Int
) {
    abstract fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        topExtraPadding: Int = 0,
        bottomExtraPadding: Int = 0
    )

    fun getLineTop(layout: Layout, line: Int): Int {
        //TODO implement me
        return 0
    }

    fun getLineBottom(layout: Layout, line: Int): Int {
        //TODO implement me
        return 0
    }
}

class SingleLineRender(
    padding: Int,
    val drawable: Drawable
) : SearchBgRender(padding) {

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        topExtraPadding: Int,
        bottomExtraPadding: Int
    ) {
        //TODO implement me
    }

}

class MultiLineRender(
    padding: Int,
    private val drawableLeft: Drawable,
    private val drawableMiddle: Drawable,
    private val drawableRight: Drawable
) : SearchBgRender(padding) {

    override fun draw(
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int,
        topExtraPadding: Int,
        bottomExtraPadding: Int
    ) {
        //TODO implement me
    }

    private fun drawStart(
        canvas: Canvas,
        start: Int,
        top: Int,
        end: Int,
        bottom: Int
    ) {
        //TODO implement me
    }

    private fun drawEnd(
        canvas: Canvas,
        start: Int,
        top: Int,
        end: Int,
        bottom: Int
    ) {
        //TODO implement me
    }
}