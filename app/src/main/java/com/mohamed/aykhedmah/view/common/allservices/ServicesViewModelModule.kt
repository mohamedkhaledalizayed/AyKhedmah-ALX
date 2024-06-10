package com.mohamed.aykhedmah.view.common.allservices

import androidx.lifecycle.ViewModel
import com.mohamed.aykhedmah.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ServicesViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ServicesViewModel::class)
    abstract fun bindsViewModel(viewModel: ServicesViewModel): ViewModel
}