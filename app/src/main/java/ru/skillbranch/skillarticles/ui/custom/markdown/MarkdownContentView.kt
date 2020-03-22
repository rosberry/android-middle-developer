package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement

class MarkdownContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private lateinit var elements: List<MarkdownElement>

    //for restore
    private var ids = arrayListOf<Int>()

    var textSize  //14
    var isLoading: Boolean = true
    val padding //8dp

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //TODO implement me
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //TODO implement me
    }

    fun setContent(content: List<MarkdownElement>) {
        //TODO implement me
    }

    fun renderSearchResult(searchResult: List<Pair<Int, Int>>) {
        //TODO implement me
    }

    fun renderSearchPosition(
        searchPosition: Pair<Int, Int>?
    ) {
        //TODO implement me
    }

    fun clearSearchResult() {
        //TODO implement me
    }

    fun setCopyListener(listener: (String) -> Unit) {
        //TODO implement me
    }
}