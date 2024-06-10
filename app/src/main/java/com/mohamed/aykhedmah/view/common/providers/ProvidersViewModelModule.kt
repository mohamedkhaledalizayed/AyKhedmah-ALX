package com.mohamed.aykhedmah.view.common.providers

import androidx.lifecycle.ViewModel
import com.mohamed.aykhedmah.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ProvidersViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProvidersViewModel::class)
    abstract fun bindsViewModel(viewModel: ProvidersViewModel): ViewModel
}