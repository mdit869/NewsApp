package com.example.korean.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.korean.Adapters.NewsAdapter
import com.example.korean.ui.MainActivity
import com.example.korean.ulti.Contanse
import com.example.korean.ulti.Resource
import com.example.newpepper1.ui.NewsViewModel
import com.example.newsprojectpractice.R
import com.example.newsprojectpractice.databinding.FragmentLanguasBinding


class HeadlinesFragment : Fragment(R.layout.fragment_languas) {
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var retryButton: Button
    lateinit var errorText: TextView
    lateinit var itemHeadlinesError: CardView
    lateinit var binding: FragmentLanguasBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLanguasBinding.bind(view)

        itemHeadlinesError = view.findViewById(R.id.itemHeadlinesError)

        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = inflater.inflate(R.layout.item_error, null)

        retryButton = view.findViewById(R.id.retryButton)
        errorText = view.findViewById(R.id.errorText)

        newsViewModel = (activity as MainActivity).newsViewModel
        setupHeadlinesRecycler()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_headlinesFragment_to_articleFragment, bundle)
        }

        newsViewModel.headlines.observe(viewLifecycleOwner, Observer {response ->
            when(response){
                is Resource.Success<*> ->{
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let {newResponse ->
                        newsAdapter.differ.submitList(newResponse.articles.toList())
                        val totalPages = newResponse.totalResults/ Contanse.QUERY_PAGE_SIZE + 2
                        isLastPage= newsViewModel.headlinesPage == totalPages
                        if(isLastPage){
                            binding.recyclerHeadlines.setPadding(0, 0, 0, 0)
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
        itemHeadlinesError.visibility = View.INVISIBLE
        isError = false
    }

    private fun showErrorMessge(message: String){
        itemHeadlinesError.visibility = View.VISIBLE
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
                newsViewModel.getHeadlines("us")
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

    private fun setupHeadlinesRecycler(){
        newsAdapter = NewsAdapter()
        binding.recyclerHeadlines.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HeadlinesFragment.scrollListener)
        }
    }

}



