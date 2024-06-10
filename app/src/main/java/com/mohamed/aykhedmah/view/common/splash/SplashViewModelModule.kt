package com.mohamed.aykhedmah.view.common.splash

import androidx.lifecycle.ViewModel
import com.mohamed.aykhedmah.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SplashViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindsViewModel(viewModel: SplashViewModel): ViewModel
}