package com.example.newsapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.newsapp.R
import com.example.newsapp.db.ArticleDatabase
import com.example.newsapp.repository.NewsRepository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val newsRepository=NewsRepository(ArticleDatabase.getInstance(this))
        val viewModelFactory=ViewModelFactory(application,newsRepository)
        viewModel=ViewModelProvider(this,viewModelFactory).get(NewsViewModel::class.java)
        bottomnavview.setupWithNavController(newsNavHostFragment.findNavController())
    }
}