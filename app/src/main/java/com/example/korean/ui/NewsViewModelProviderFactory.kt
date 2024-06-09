package com.example.korean.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.korean.Reponstory.NewReponsitory
import com.example.newpepper1.ui.NewsViewModel

class NewsViewModelProviderFactory(val app: Application, val newReponsitory: NewReponsitory): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NewsViewModel(app, newReponsitory) as T
    }
}