package com.mohamed.aykhedmah.view.user.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mohamed.aykhedmah.data.network.response.deleteimage.DeleteImageResponse
import com.mohamed.aykhedmah.data.network.response.editprofile.EditUserProfileResponse
import com.mohamed.aykhedmah.data.network.response.providerdetails.ProfileResponse
import com.mohamed.aykhedmah.data.network.response.settings.SettingsResponse
import com.mohamed.aykhedmah.data.repository.RemoteRepository
import com.mohamed.aykhedmah.utils.BaseSchedulerProvider
import com.mohamed.aykhedmah.utils.BaseViewModel
import com.mohamed.aykhedmah.utils.ViewState
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.HttpException
import javax.inject.Inject


class ProfileViewModel @Inject
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

    fun deleteImage(token: String, myMap: Map<String, Any>): LiveData<ViewState<DeleteImageResponse>> {

        val data = MutableLiveData<ViewState<DeleteImageResponse>>()

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
            useCase = repository.deleteImage(token, myMap)
        )

        return data
    }

    fun changeWorkDates(token: String, myMap: Map<String, Any>): LiveData<ViewState<EditUserProfileResponse>> {

        val data = MutableLiveData<ViewState<EditUserProfileResponse>>()

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
            useCase = repository.changeWorkDates(token, myMap)
        )

        return data
    }

    fun uploadProviderImage(token: String, id: RequestBody, file: MultipartBody.Part): LiveData<ViewState<EditUserProfileResponse>> {

        val data = MutableLiveData<ViewState<EditUserProfileResponse>>()

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
            useCase = repository.uploadProviderImage(token, id, file)
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