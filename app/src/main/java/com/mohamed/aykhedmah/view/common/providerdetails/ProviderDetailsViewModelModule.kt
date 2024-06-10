package com.mohamed.aykhedmah.view.common.providerdetails

import androidx.lifecycle.ViewModel
import com.mohamed.aykhedmah.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ProviderDetailsViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProviderDetailsViewModel::class)
    abstract fun bindsViewModel(viewModel: ProviderDetailsViewModel): ViewModel
}