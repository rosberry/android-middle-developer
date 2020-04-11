package ru.skillbranch.skillarticles.viewmodels

import androidx.lifecycle.SavedStateHandle
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState

/**
 * @author mmikhailov on 11.04.2020.
 */
class RootViewModel(handle: SavedStateHandle) : BaseViewModel<RootState>(handle, RootState()) {
}

data class RootState(val isAuth: Boolean = false) : IViewModelState