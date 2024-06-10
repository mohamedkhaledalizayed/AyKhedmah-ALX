package com.mohamed.aykhedmah.view.user.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mohamed.aykhedmah.data.network.response.arealist.AreasList
import com.mohamed.aykhedmah.data.network.response.cities.CitiesResponse
import com.mohamed.aykhedmah.data.network.response.citieslist.CitiesList
import com.mohamed.aykhedmah.data.network.response.services.ServicesResponse
import com.mohamed.aykhedmah.data.network.response.signupprovider.ProviderSignUpResponse
import com.mohamed.aykhedmah.data.network.response.signupuser.UserSignUpResponse
import com.mohamed.aykhedmah.data.network.response.subservices.SubServicesResponse
import com.mohamed.aykhedmah.data.repository.RemoteRepository
import com.mohamed.aykhedmah.utils.BaseSchedulerProvider
import com.mohamed.aykhedmah.utils.BaseViewModel
import com.mohamed.aykhedmah.utils.ViewState


import retrofit2.HttpException
import javax.inject.Inject


class SignUpViewModel @Inject
constructor(
        baseSchedulerProvider: BaseSchedulerProvider, private val repository: RemoteRepository
) : BaseViewModel(baseSchedulerProvider) {

    fun signUp(myMap: Map<String, String>): LiveData<ViewState<UserSignUpResponse>> {

        val data = MutableLiveData<ViewState<UserSignUpResponse>>()

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
                useCase = repository.signUpUser(myMap)
        )

        return data
    }

    fun signUpProvider(myMap: Map<String, String>): LiveData<ViewState<ProviderSignUpResponse>> {

        val data = MutableLiveData<ViewState<ProviderSignUpResponse>>()

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
            useCase = repository.signUpProvider(myMap)
        )

        return data
    }

    fun getCities(): LiveData<ViewState<CitiesResponse>> {

        val data = MutableLiveData<ViewState<CitiesResponse>>()

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
            useCase = repository.getCities()
        )

        return data
    }

    fun getCitiesList(): LiveData<ViewState<CitiesList>> {

        val data = MutableLiveData<ViewState<CitiesList>>()

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
            useCase = repository.getCitiesList()
        )

        return data
    }

    fun getAreasList(id: String): LiveData<ViewState<AreasList>> {

        val data = MutableLiveData<ViewState<AreasList>>()

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
            useCase = repository.getAreasList(id)
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