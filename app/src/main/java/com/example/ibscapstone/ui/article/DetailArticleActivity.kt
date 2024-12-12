package com.example.ibscapstone.ui.article

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.ibscapstone.data.Article
import com.example.ibscapstone.databinding.ActivityDetailArticleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            // Remove the default title
            setDisplayShowTitleEnabled(false)
        }

        // Get article from intent
        val article = intent.getParcelableExtra<Article>(EXTRA_ARTICLE)
        article?.let { displayArticle(it) }

        // Set up back button click listener
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun displayArticle(article: Article) {
        with(binding) {
            // Set toolbar title
            toolbarTitle.text = article.title

            // Load image
            Glide.with(this@DetailArticleActivity)
                .load(article.image_url)
                .centerCrop()
                .into(ivDetailArticle)

            // Set text
            tvDetailTitle.text = article.title
            tvDetailContent.text = article.content
        }
    }

    companion object {
        const val EXTRA_ARTICLE = "extra_article"
    }
}