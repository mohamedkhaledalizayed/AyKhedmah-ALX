package com.mohamed.aykhedmah.view.provider.home

import androidx.lifecycle.ViewModel
import com.mohamed.aykhedmah.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ProviderHomeViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProviderHomeViewModel::class)
    abstract fun bindsViewModel(viewModel: ProviderHomeViewModel): ViewModel
}