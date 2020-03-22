package ru.skillbranch.skillarticles.ui.custom.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.Spannable
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.nio.charset.Charset
import java.security.MessageDigest
import kotlin.math.hypot


@SuppressLint("ViewConstructor")
class MarkdownImageView private constructor(
    context: Context,
    fontSize: Float
) : ViewGroup(context, null, 0), IMarkdownView {

    override var fontSize: Float = fontSize

    override val spannableContent: Spannable

    //views
    private lateinit var imageUrl: String
    private lateinit var imageTitle: CharSequence

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val iv_image: ImageView
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val tv_title: MarkdownTextView
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var tv_alt: TextView? = null

    @Px
    private val titleTopMargin: Int //8dp
    @Px
    private val titlePadding: Int //56dp
    @Px
    private val cornerRadius: Float //4dp
    @ColorInt
    private val colorSurface: Int //colorSurface
    @ColorInt
    private val colorOnSurface: Int //colorOnSurface
    @ColorInt
    private val colorOnBackground: Int //colorOnBackground
    @ColorInt
    private var lineColor: Int //R.color.color_divider

    //for draw object allocation
    private var linePositionY: Float = 0f
    private val linePaint

    init {
        //TODO implement me
    }

    constructor(
        context: Context,
        fontSize: Float,
        url: String,
        title: CharSequence,
        alt: String?
    ) : this(context, fontSize) {
        //TODO implement me
    }


    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //TODO implement me
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //TODO implement me
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        //TODO implement me
    }

    private fun animateShowAlt() {
        tv_alt?.isVisible = true
        val endRadius = hypot(tv_alt?.width?.toFloat() ?: 0f, tv_alt?.height?.toFloat() ?: 0f)
        val va = ViewAnimationUtils.createCircularReveal(
            tv_alt,
            tv_alt?.width ?: 0,
            tv_alt?.height ?: 0,
            0f,
            endRadius
        )
        va.start()
    }

    private fun animateHideAlt() {
        val endRadius = hypot(tv_alt?.width?.toFloat() ?: 0f, tv_alt?.height?.toFloat() ?: 0f)
        val va = ViewAnimationUtils.createCircularReveal(
            tv_alt,
            tv_alt?.width ?: 0,
            tv_alt?.height ?: 0,
            endRadius,
            0f
        )
        va.doOnEnd { tv_alt?.isVisible = false }
        va.start()
    }
}

class AspectRatioResizeTransform : BitmapTransformation() {
    private val ID =
        "ru.skillbranch.skillarticles.glide.AspectRatioResizeTransform" //any unique string
    private val ID_BYTES = ID.toByteArray(Charset.forName("UTF-8"))
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        //TODO implement me
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        //TODO implement me
    }

    override fun equals(other: Any?): Boolean {
        //TODO implement me
    }

    override fun hashCode(): Int {
        //TODO implement me
    }
}