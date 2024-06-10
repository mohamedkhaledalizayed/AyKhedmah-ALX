package com.mohamed.aykhedmah.view.common.supservices

import androidx.lifecycle.ViewModel
import com.mohamed.aykhedmah.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SubServicesViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SubServicesViewModel::class)
    abstract fun bindsViewModel(viewModel: SubServicesViewModel): ViewModel
}