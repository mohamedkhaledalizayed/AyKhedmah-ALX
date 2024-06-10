package com.mohamed.aykhedmah.view.common.supservices

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mohamed.aykhedmah.data.network.response.subservices.SubServicesResponse
import com.mohamed.aykhedmah.data.repository.RemoteRepository
import com.mohamed.aykhedmah.utils.BaseSchedulerProvider
import com.mohamed.aykhedmah.utils.BaseViewModel
import com.mohamed.aykhedmah.utils.ViewState


import retrofit2.HttpException
import javax.inject.Inject


class SubServicesViewModel @Inject
constructor(
        baseSchedulerProvider: BaseSchedulerProvider, private val repository: RemoteRepository
) : BaseViewModel(baseSchedulerProvider) {

    fun getSubServices(id: String): LiveData<ViewState<SubServicesResponse>> {

        val data = MutableLiveData<ViewState<SubServicesResponse>>()

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
                useCase = repository.getSubServices(id)
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