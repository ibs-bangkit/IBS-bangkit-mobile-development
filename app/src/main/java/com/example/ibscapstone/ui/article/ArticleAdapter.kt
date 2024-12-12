package com.example.ibscapstone.ui.article

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ibscapstone.data.Article
import com.example.ibscapstone.databinding.ItemArticleBinding
import com.example.ibscapstone.ui.GlideApp

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
    private var articles = listOf<Article>()

    fun submitList(newList: List<Article>) {
        articles = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount() = articles.size

    class ArticleViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            with(binding) {
                articleTitle.text = article.title
                articleDescription.text = article.description

                // Load image using Glide
                Glide.with(binding.root.context)
                    .load(article.image_url)
                    .centerCrop()
                    .into(ivArticle)
                Log.d("ArticleAdapter", "Image URL: ${article.image_url}")
            }
        }
    }
}