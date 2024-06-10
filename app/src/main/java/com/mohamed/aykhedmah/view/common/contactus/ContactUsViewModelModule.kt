package com.mohamed.aykhedmah.view.common.contactus

import androidx.lifecycle.ViewModel
import com.mohamed.aykhedmah.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ContactUsViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ContactUsViewModel::class)
    abstract fun bindsViewModel(viewModel: ContactUsViewModel): ViewModel
}