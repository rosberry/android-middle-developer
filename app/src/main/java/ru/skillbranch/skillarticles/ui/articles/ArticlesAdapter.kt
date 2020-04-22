package ru.skillbranch.skillarticles.ui.articles

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.skillarticles.data.ArticleItemData
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.ui.custom.ArticleItemView

class ArticlesAdapter(private val listener: (ArticleItemData) -> Unit) :
        ListAdapter<ArticleItemData, ArticleVH>(ArticleDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleVH {
        val view = ArticleItemView(parent.context)
//        val containerView = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleVH(view)
    }

    override fun onBindViewHolder(holder: ArticleVH, position: Int) {
        holder.bind(getItem(position), listener)
    }
}

class ArticleDiffCallback : DiffUtil.ItemCallback<ArticleItemData>() {
    override fun areItemsTheSame(oldItem: ArticleItemData, newItem: ArticleItemData): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: ArticleItemData, newItem: ArticleItemData): Boolean = oldItem == newItem
}

class ArticleVH(val containerView: View) : RecyclerView.ViewHolder(containerView) {
    fun bind(
            item: ArticleItemData,
            listener: (ArticleItemData) -> Unit
    ) {
        (containerView as ArticleItemView).bind(item)
        val posterSize = containerView.context.dpToIntPx(64)
        val cornerRadius = containerView.context.dpToIntPx(8)
        val categorySize = containerView.context.dpToIntPx(40)

        /*  Glide.with(containerView.context)
               .load(item.poster)
               .transform(CenterCrop(), RoundedCorners(cornerRadius))
               .override(posterSize)
               .into(iv_poster)

           Glide.with(containerView.context)
               .load(item.categoryIcon)
               .transform(CenterCrop(), RoundedCorners(cornerRadius))
               .override(categorySize)
               .into(iv_category)*/

//        tv_date.text = item.date.format()
//        tv_author.text = item.author
//        tv_title.text = item.title
//        tv_description.text = item.description
//        tv_likes_count.text = "${item.likeCount}"
//        tv_comments_count.text = "${item.commentCount}"
//        tv_read_duration.text = "${item.readDuration} min read"*/

        itemView.setOnClickListener { listener(item) }

    }

}