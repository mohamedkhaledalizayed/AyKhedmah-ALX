package com.mohamed.aykhedmah.view.common.editprofile

import androidx.lifecycle.ViewModel
import com.mohamed.aykhedmah.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class EditProfileViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(EditProfileViewModel::class)
    abstract fun bindsViewModel(viewModel: EditProfileViewModel): ViewModel
}