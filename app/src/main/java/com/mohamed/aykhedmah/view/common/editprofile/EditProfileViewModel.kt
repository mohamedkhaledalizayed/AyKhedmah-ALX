package com.mohamed.aykhedmah.view.common.editprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mohamed.aykhedmah.data.network.response.editprofile.EditUserProfileResponse
import com.mohamed.aykhedmah.data.network.response.updateprofile.UpdateUserProfile
import com.mohamed.aykhedmah.data.repository.RemoteRepository
import com.mohamed.aykhedmah.utils.BaseSchedulerProvider
import com.mohamed.aykhedmah.utils.BaseViewModel
import com.mohamed.aykhedmah.utils.ViewState
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.HttpException
import javax.inject.Inject


class EditProfileViewModel @Inject
constructor(
    baseSchedulerProvider: BaseSchedulerProvider, private val repository: RemoteRepository
) : BaseViewModel(baseSchedulerProvider) {

    fun editUserProfile(token: String, myMap: Map<String, String>): LiveData<ViewState<UpdateUserProfile>> {

        val data = MutableLiveData<ViewState<UpdateUserProfile>>()

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
                useCase = repository.editUserProfile(token, myMap)
        )

        return data
    }


    fun updateProfile(token: String, id: RequestBody, file: MultipartBody.Part): LiveData<ViewState<EditUserProfileResponse>> {

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
            useCase = repository.updateProfile(token, id, file)
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