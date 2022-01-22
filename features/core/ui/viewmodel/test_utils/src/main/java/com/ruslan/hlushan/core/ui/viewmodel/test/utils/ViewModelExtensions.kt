package com.ruslan.hlushan.core.ui.viewmodel.test.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore

//https://stackoverflow.com/questions/54115627/how-to-ensure-viewmodeloncleared-is-called-in-an-android-unit-test
fun ViewModel.callOnCleared() {
    val thisViewModel = this

    val viewModelStore = ViewModelStore()
    val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = (thisViewModel as T)
    }
    val provider = ViewModelProvider(viewModelStore, factory)

    provider.get(thisViewModel::class.java)

    viewModelStore.clear()
}