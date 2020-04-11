package ru.skillbranch.skillarticles.viewmodels.base

import androidx.lifecycle.SavedStateHandle

interface IViewModelState {
    fun save(outState: SavedStateHandle) {
        // empty default implementaion
    }
    fun restore(savedState: SavedStateHandle) : IViewModelState {
        // empty default implementaion
        return this
    }
}