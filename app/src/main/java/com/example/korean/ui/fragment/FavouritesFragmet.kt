package com.example.korean.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.korean.Adapters.NewsAdapter
import com.example.korean.ui.MainActivity
import com.example.newpepper1.ui.NewsViewModel
import com.example.newsprojectpractice.R
import com.example.newsprojectpractice.databinding.FragmentFavoriteBinding
import com.google.android.material.snackbar.Snackbar


class FavouritesFragmet : Fragment(R.layout.fragment_favorite) {
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentFavoriteBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoriteBinding.bind(view)



        newsViewModel = (activity as MainActivity).newsViewModel
        setupFavoriteRecycler()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(R.id.action_favouritesFragment_to_articleFragment, bundle)
        }
        val itemTouHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return  true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)
                Snackbar.make(view, "Removed from favorite", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo"){
                        newsViewModel.addToFavourites(article)
                    }
                    show()
                }
            }
        }
            ItemTouchHelper(itemTouHelperCallback).apply {
                attachToRecyclerView(binding.recyclerFavourites)
            }
            newsViewModel.getFavouriteNews().observe(viewLifecycleOwner, Observer { articles ->
                newsAdapter.differ.submitList(articles)
            })


    }

    private fun setupFavoriteRecycler(){
        newsAdapter = NewsAdapter()
        binding.recyclerFavourites.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}