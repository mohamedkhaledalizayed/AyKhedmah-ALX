package com.mohamed.aykhedmah.view.common.providers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.land.services.data.model.response.search.SearchResponse
import com.mohamed.aykhedmah.data.network.response.providers.ProvidersResponse
import com.mohamed.aykhedmah.data.network.response.services.ServicesResponse
import com.mohamed.aykhedmah.data.repository.RemoteRepository
import com.mohamed.aykhedmah.utils.BaseSchedulerProvider
import com.mohamed.aykhedmah.utils.BaseViewModel
import com.mohamed.aykhedmah.utils.ViewState


import retrofit2.HttpException
import javax.inject.Inject


class ProvidersViewModel @Inject
constructor(
        baseSchedulerProvider: BaseSchedulerProvider, private val repository: RemoteRepository
) : BaseViewModel(baseSchedulerProvider) {

    fun getProviders(id: String): LiveData<ViewState<ProvidersResponse>> {

        val data = MutableLiveData<ViewState<ProvidersResponse>>()

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
                useCase = repository.getProviders(id)
        )

        return data
    }

    fun getServices(): LiveData<ViewState<ServicesResponse>> {

        val data = MutableLiveData<ViewState<ServicesResponse>>()

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
            useCase = repository.getServices()
        )

        return data
    }

    fun search(token: String, key: String): LiveData<ViewState<SearchResponse>> {

        val data = MutableLiveData<ViewState<SearchResponse>>()

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
            useCase = repository.search(token, key)
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