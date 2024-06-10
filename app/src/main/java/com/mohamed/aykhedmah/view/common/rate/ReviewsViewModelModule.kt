package com.mohamed.aykhedmah.view.common.rate

import androidx.lifecycle.ViewModel
import com.mohamed.aykhedmah.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ReviewsViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ReviewsViewModel::class)
    abstract fun bindsViewModel(viewModel: ReviewsViewModel): ViewModel
}