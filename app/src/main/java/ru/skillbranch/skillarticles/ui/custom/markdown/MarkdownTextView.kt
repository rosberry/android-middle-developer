package ru.skillbranch.skillarticles.ui.custom.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.text.Spannable
import android.widget.TextView

@SuppressLint("ViewConstructor")
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
class MarkdownTextView constructor(
    context: Context,
    fontSize: Float,
    mockHelper: SearchBgHelper? = null //for mock
) : TextView(context, null, 0), IMarkdownView {

    constructor(context: Context, fontSize: Float) : this(context, fontSize, null) 

    override var fontSize: Float

    override val spannableContent: Spannable

    private val color  //colorOnBackground
    private val focusRect = Rect()

    private val searchBgHelper = SearchBgHelper(context) { top, bottom ->
        //TODO implement me
    }


    override fun onDraw(canvas: Canvas) {
        //TODO implement me
        super.onDraw(canvas)
    }
}