package com.example.korean.ui.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.korean.Adapters.NewsAdapter
import com.example.korean.ui.MainActivity
import com.example.korean.ulti.Contanse
import com.example.korean.ulti.Contanse.Companion.SEARCH_NEW_TIME_DELAY
import com.example.korean.ulti.Resource
import com.example.newpepper1.ui.NewsViewModel
import com.example.newsprojectpractice.R
import com.example.newsprojectpractice.databinding.FragmentSearchBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchFragment : Fragment(R.layout.fragment_search) {
    lateinit var newsAdapter: NewsAdapter
    lateinit var newsViewModel: NewsViewModel
    lateinit var retryButton: Button
    lateinit var errorText : TextView
    lateinit var itemSearchError: CardView
    lateinit var binding: FragmentSearchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)

        itemSearchError = view.findViewById(R.id.itemSearchError)

        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.item_error, null)

        retryButton = view.findViewById(R.id.retryButton)
        errorText = view.findViewById(R.id.errorText)

        newsViewModel = (activity as MainActivity).newsViewModel
        setupSearchRecycler()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_searchFragment_to_articleFragment, bundle)
        }
        var job: Job? = null
        binding.searchEdit.addTextChangedListener(){editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEW_TIME_DELAY)
                editable?.let{
                    if (editable.toString().isNotEmpty()){
                        newsViewModel.searchNews(editable.toString())
                    }
                }
            }
        }

        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer {response ->
            when(response){
                is Resource.Success<*> ->{
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let {newResponse ->
                        newsAdapter.differ.submitList(newResponse.articles.toList())
                        val totalPages = newResponse.totalResults/ Contanse.QUERY_PAGE_SIZE + 2
                        isLastPage= newsViewModel.searchNewsPage == totalPages
                        if(isLastPage){
                            binding.recyclerSearch.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error<*> ->{
                    hideProgressBar()
                    response.message?.let {message ->
                        Toast.makeText(activity, "Sorry error: $message", Toast.LENGTH_LONG).show()
                        showErrorMessge(message)
                    }
                }
                is Resource.Loading<*> ->{
                    showProgressBar()
                }
            }
        })

        retryButton.setOnClickListener {
            if(binding.searchEdit.text.toString().isNotEmpty()){
                newsViewModel.searchNews(binding.searchEdit.text.toString())
            }else{
                hideErrorMessage()
            }
        }
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false


    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage(){
        itemSearchError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessge(message: String){
        itemSearchError.visibility = View.VISIBLE
        errorText.text = message
        isError = true
    }

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoError = !isError
            val isNotLoadingAndNoLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Contanse.QUERY_PAGE_SIZE
            val shouldPaginate = isNoError && isNotLoadingAndNoLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                newsViewModel.searchNews(binding.searchEdit.text.toString())
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }
    private fun setupSearchRecycler(){
        newsAdapter = NewsAdapter()
        binding.recyclerSearch.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFragment.scrollListener)
        }
    }
}