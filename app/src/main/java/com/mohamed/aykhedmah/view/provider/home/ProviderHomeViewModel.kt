package com.mohamed.aykhedmah.view.provider.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mohamed.aykhedmah.data.network.response.logout.LogoutResponse
import com.mohamed.aykhedmah.data.network.response.providerdetails.ProfileResponse
import com.mohamed.aykhedmah.data.network.response.slider.SliderResponse
import com.mohamed.aykhedmah.data.repository.RemoteRepository
import com.mohamed.aykhedmah.utils.BaseSchedulerProvider
import com.mohamed.aykhedmah.utils.BaseViewModel
import com.mohamed.aykhedmah.utils.ViewState

import retrofit2.HttpException
import javax.inject.Inject


class ProviderHomeViewModel @Inject
constructor(
        baseSchedulerProvider: BaseSchedulerProvider, private val repository: RemoteRepository
) : BaseViewModel(baseSchedulerProvider) {

    fun getSlider(): LiveData<ViewState<SliderResponse>> {

        val data = MutableLiveData<ViewState<SliderResponse>>()

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
                useCase = repository.getSlider()
        )

        return data
    }

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

    fun logout(token: String, myMap: Map<String, Any>): LiveData<ViewState<LogoutResponse>> {

        val data = MutableLiveData<ViewState<LogoutResponse>>()

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
            useCase = repository.logout(token, myMap)
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