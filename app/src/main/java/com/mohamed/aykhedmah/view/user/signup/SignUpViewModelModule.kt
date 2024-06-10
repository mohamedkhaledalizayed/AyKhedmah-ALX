package com.mohamed.aykhedmah.view.user.signup

import androidx.lifecycle.ViewModel
import com.mohamed.aykhedmah.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SignUpViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SignUpViewModel::class)
    abstract fun bindsViewModel(viewModel: SignUpViewModel): ViewModel
}