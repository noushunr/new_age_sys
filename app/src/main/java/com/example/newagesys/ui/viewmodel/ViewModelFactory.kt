package com.example.newagesys.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newagesys.data.repositories.AodpListRepositories


/*
 *Created by Noushad N on 05-06-2023
*/

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: AodpListRepositories
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AodpListViewModel(repository) as T
    }

}