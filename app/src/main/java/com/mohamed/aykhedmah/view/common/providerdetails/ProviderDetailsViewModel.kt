package com.mohamed.aykhedmah.view.common.providerdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mohamed.aykhedmah.data.network.response.addreview.AddReviewResponse
import com.mohamed.aykhedmah.data.network.response.increasecount.IncreaseCountResponse
import com.mohamed.aykhedmah.data.network.response.providerdetails.ProfileResponse
import com.mohamed.aykhedmah.data.network.response.providers.ProvidersResponse
import com.mohamed.aykhedmah.data.network.response.services.ServicesResponse
import com.mohamed.aykhedmah.data.repository.RemoteRepository
import com.mohamed.aykhedmah.utils.BaseSchedulerProvider
import com.mohamed.aykhedmah.utils.BaseViewModel
import com.mohamed.aykhedmah.utils.ViewState


import retrofit2.HttpException
import javax.inject.Inject


class ProviderDetailsViewModel @Inject
constructor(
        baseSchedulerProvider: BaseSchedulerProvider, private val repository: RemoteRepository
) : BaseViewModel(baseSchedulerProvider) {

    fun getUserProfile(token: String, id: String): LiveData<ViewState<ProfileResponse>> {

        val data = MutableLiveData<ViewState<ProfileResponse>>()

        execute(
            loadingConsumer = {
                data.postValue(
                    ViewState.loading()
                )
            },
            successConsumer = { response ->
                response?.let {
                    data.postValue(
                        ViewState.success(it)
                    )
                }
            },
            throwableConsumer = { throwable ->
                data.postValue(
                    ViewState.error(handleError(throwable))
                )
            },
            useCase = repository.getUserProfile(token, id)
        )

        return data
    }

    fun addReview(token: String, myMap: Map<String, Any>): LiveData<ViewState<AddReviewResponse>> {

        val data = MutableLiveData<ViewState<AddReviewResponse>>()

        execute(
            loadingConsumer = {
                data.postValue(
                    ViewState.loading()
                )
            },
            successConsumer = { response ->
                response?.let {
                    data.postValue(
                        ViewState.success(it)
                    )
                }
            },
            throwableConsumer = { throwable ->
                data.postValue(
                    ViewState.error(handleError(throwable))
                )
            },
            useCase = repository.addReview(token, myMap)
        )

        return data
    }

    fun increaseCount(token: String, myMap: Map<String, Any>): LiveData<ViewState<IncreaseCountResponse>> {

        val data = MutableLiveData<ViewState<IncreaseCountResponse>>()

        execute(
            loadingConsumer = {
                data.postValue(
                    ViewState.loading()
                )
            },
            successConsumer = { response ->
                response?.let {
                    data.postValue(
                        ViewState.success(it)
                    )
                }
            },
            throwableConsumer = { throwable ->
                data.postValue(
                    ViewState.error(handleError(throwable))
                )
            },
            useCase = repository.increaseCount(token, myMap)
        )

        return data
    }

    private fun handleError(throwable: Throwable): String {
        return if (throwable is HttpException) {
            when (throwable.code()) {
                400 -> {
                    "Wrong Username or Password"
                }
                403 -> {
                    "You should login"
                }
                500 -> {
                    "Something went wrong"
                }
                else -> {
                    throwable.message!!
                }
            }
        } else {
            throwable.message!!
        }
    }

}