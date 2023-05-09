package com.pawka.trellocloneapp.presentation.fragments.profile

import androidx.lifecycle.ViewModel
import com.pawka.trellocloneapp.data.UserRepositoryImpl
import com.pawka.trellocloneapp.domain.user.use_cases.GetCurrentUserDataUseCase
import com.pawka.trellocloneapp.domain.user.use_cases.SignOutUserUseCase

class SettingsViewModel: ViewModel() {

    private val repository = UserRepositoryImpl

    private val getCurrentUserDataUseCase = GetCurrentUserDataUseCase(repository)
    private val signOutUserUseCase = SignOutUserUseCase(repository)

    val currentUserLiveData = getCurrentUserDataUseCase.getCurrentUserData()

    fun signOut() {
        signOutUserUseCase.signOutUser()
    }
}