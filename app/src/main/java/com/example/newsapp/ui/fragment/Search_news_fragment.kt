package com.example.newsapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapter.NewsAdapter
import com.example.newsapp.ui.MainActivity
import com.example.newsapp.ui.NewsViewModel
import com.example.newsapp.util.Resource
import kotlinx.android.synthetic.main.fragment_breaking_news_fragment.*
import kotlinx.android.synthetic.main.fragment_search_news_fragment.paginationProgressBar
import kotlinx.android.synthetic.main.fragment_search_news_fragment.*
import kotlinx.coroutines.*
import java.util.*


class Search_news_fragment : Fragment(R.layout.fragment_search_news_fragment) {


    lateinit var newsAdapter: NewsAdapter
    lateinit var viewModel: NewsViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as MainActivity).viewModel
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_search_news_fragment_to_artFragment,bundle)
        }
        var job:Job?=null
        etSearch.addTextChangedListener{editable->
            job?.cancel()
            job= MainScope().launch {
                delay(500)
            }
            if(editable.toString().isNotEmpty()){
                viewModel.getSearching(editable.toString())
            }
        }

        viewModel.searchingNews.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            when(it){
                is Resource.Success->{
                    hideProgressBar()
                    it.data?.let {newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages=newsResponse.totalResults/20 + 2
                        isLastPage=viewModel.searching_page==totalPages
                        if(isLastPage){
                            rvBreakingNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error->{
                    hideProgressBar()
                    it.message?.let {
                        Toast.makeText(activity,"An error occured: $it", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading->{
                    showProgressBar()

                }
 
            }

})



    }
    fun hideProgressBar(){
        paginationProgressBar.visibility=View.INVISIBLE
        isLoading=false
    }
    fun showProgressBar(){
        paginationProgressBar.visibility=View.VISIBLE
        isLoading=true
    }


    var isLoading=false
    var isLastPage=false
    var isScrolling=false

    val scrollListener=object: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager=recyclerView.layoutManager as LinearLayoutManager
            val fistvisibleItemPosition=layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount=layoutManager.childCount
            val totalItemCount=layoutManager.itemCount

            val isNotLoadingAndLastPage=!isLoading && !isLastPage
            val isAtLastItem=fistvisibleItemPosition+visibleItemCount>= totalItemCount
            val isNotAtBegginning=fistvisibleItemPosition>=0
            val isTotalMoreThanVisible=totalItemCount>=20

            val shouldPaginate=isNotLoadingAndLastPage && isAtLastItem && isNotAtBegginning && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){

                viewModel.getBreaking(etSearch.text.toString())
                isScrolling=false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling=true

            }
        }
    }

    fun setupRecyclerView(){
        newsAdapter= NewsAdapter()
        rvSearchNews.apply {
            adapter=newsAdapter
            layoutManager= LinearLayoutManager(activity)
            addOnScrollListener(this@Search_news_fragment.scrollListener)
        }

    }
}