package com.bizify.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bizify.data.repositories.AodpListRepositories


/*
 *Created by Noushad N on 05-06-2023
*/

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: AodpListRepositories
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }

}