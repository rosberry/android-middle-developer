package ru.skillbranch.skillarticles.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_category_dialog.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel

class ChoseCategoryDialog : DialogFragment() {
    private val viewModel: ArticlesViewModel by activityViewModels()
    private val selectedCategories = mutableListOf<String>()
    private val args: ChoseCategoryDialogArgs by navArgs()
    private val categoryAdapter = CategoryAdapter { categoryId: String, isChecked: Boolean ->
        toggleCategory(categoryId, isChecked)
        Log.e("ChoseCategoryDialog", "$categoryId: $isChecked $selectedCategories")
    }

    private fun toggleCategory(categoryId: String, isChecked: Boolean) {
        if (isChecked) selectedCategories.add(categoryId)
        else selectedCategories.remove(categoryId)
        val categories = args.categories.toList()
        val categoryItems = categories.map { it.toCategoryDataItem(selectedCategories.contains(it.categoryId)) }
        categoryAdapter.submitList(categoryItems)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val categories = args.categories.toList()
        selectedCategories.clear()
        selectedCategories.addAll(savedInstanceState?.getStringArray("checked") ?: args.selectedCategories)
        val categoryItems = categories.map { it.toCategoryDataItem(selectedCategories.contains(it.categoryId)) }

        categoryAdapter.submitList(categoryItems)

        val listView =
                layoutInflater.inflate(R.layout.fragment_choose_category_dialog, null) as RecyclerView
        with(listView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }
        return AlertDialog.Builder(requireContext())
            .setTitle("Chose category")
            .setPositiveButton("Apply") { _, _ ->
                viewModel.applyCategories(selectedCategories)
            }
            .setNegativeButton("Reset") { _, _ ->
                viewModel.applyCategories(emptyList())
            }
            .setView(listView)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArray("checked", selectedCategories.toTypedArray())
        super.onSaveInstanceState(outState)
    }

}

class CategoryAdapter(private val listener: (String, Boolean) -> Unit) :
        ListAdapter<CategoryDataItem, CategoryVH>(CategoryDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH = CategoryVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category_dialog, parent, false),
            listener
    )

    override fun onBindViewHolder(holder: CategoryVH, position: Int) {
        holder.bind(getItem(position))
    }
}

class CategoryVH(override val containerView: View, val listener: (String, Boolean) -> Unit) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(item: CategoryDataItem) {
        ch_select.isChecked = item.isChecked
        Glide.with(containerView.context)
            .load(item.icon)
            .apply(RequestOptions.circleCropTransform())
            .override(iv_icon.width)
            .into(iv_icon)
        tv_category.text = item.title
        tv_count.text = "${item.articlesCount}"
        ch_select.setOnCheckedChangeListener { _, checked -> listener(item.categoryId, checked) }
        itemView.setOnClickListener { ch_select.toggle() }
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryDataItem>() {
    override fun areItemsTheSame(oldItem: CategoryDataItem, newItem: CategoryDataItem): Boolean =
            oldItem.categoryId == newItem.categoryId

    override fun areContentsTheSame(oldItem: CategoryDataItem, newItem: CategoryDataItem): Boolean =
            oldItem == newItem
}

data class CategoryDataItem(
        val categoryId: String,
        val icon: String,
        val title: String,
        val articlesCount: Int = 0,
        val isChecked: Boolean = false
)
