package com.mohamed.aykhedmah.data.network


import com.land.services.data.model.response.search.SearchResponse
import com.mohamed.aykhedmah.data.network.response.addreview.AddReviewResponse
import com.mohamed.aykhedmah.data.network.response.arealist.AreasList
import com.mohamed.aykhedmah.data.network.response.cities.CitiesResponse
import com.mohamed.aykhedmah.data.network.response.citieslist.CitiesList
import com.mohamed.aykhedmah.data.network.response.contactus.ContactUsResponse
import com.mohamed.aykhedmah.data.network.response.deleteimage.DeleteImageResponse
import com.mohamed.aykhedmah.data.network.response.editprofile.EditUserProfileResponse
import com.mohamed.aykhedmah.data.network.response.increasecount.IncreaseCountResponse
import com.mohamed.aykhedmah.data.network.response.login.LoginResponse
import com.mohamed.aykhedmah.data.network.response.logout.LogoutResponse
import com.mohamed.aykhedmah.data.network.response.notificationcount.NotificationsCount
import com.mohamed.aykhedmah.data.network.response.notifications.NotificationResponse
import com.mohamed.aykhedmah.data.network.response.providerdetails.ProfileResponse
import com.mohamed.aykhedmah.data.network.response.providers.ProvidersResponse
import com.mohamed.aykhedmah.data.network.response.reviews.ReviewsResponse
import com.mohamed.aykhedmah.data.network.response.services.ServicesResponse
import com.mohamed.aykhedmah.data.network.response.settings.SettingsResponse
import com.mohamed.aykhedmah.data.network.response.signupprovider.ProviderSignUpResponse
import com.mohamed.aykhedmah.data.network.response.signupuser.UserSignUpResponse
import com.mohamed.aykhedmah.data.network.response.slider.SliderResponse
import com.mohamed.aykhedmah.data.network.response.subservices.SubServicesResponse
import com.mohamed.aykhedmah.data.network.response.updateprofile.UpdateUserProfile
import io.reactivex.Flowable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*


@JvmSuppressWildcards
interface ServiceApi {

    @GET("setting")
    fun getSettings(): Flowable<SettingsResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(@FieldMap myMap: Map<String, String>): Flowable<LoginResponse>

    @GET("slider")
    fun getSlider(): Flowable<SliderResponse>

    @GET("services")
    fun getServices(): Flowable<ServicesResponse>

    @GET("get-count-unread-notifications")
    fun getUnReadNotifications(@Header("Authorization") token: String?, @Query("user_id") id: String): Flowable<NotificationsCount>

    @FormUrlEncoded
    @POST("logout")
    fun logout(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<LogoutResponse>

    @GET("services")
    fun getSubServices(@Query("perant_id") id: String): Flowable<SubServicesResponse>

    @GET("get-providers")
    fun getProviders(@Query("sub_service_id") id: String): Flowable<ProvidersResponse>

    @GET("get-profile")
    fun getUserProfile(@Header("Authorization") token: String?, @Query("user_id") id: String): Flowable<ProfileResponse>

    @FormUrlEncoded
    @POST("create-rate")
    fun addReview(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<AddReviewResponse>

    @FormUrlEncoded
    @POST("increase-count")
    fun increaseCount(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<IncreaseCountResponse>

    @GET("get-rates")
    fun getReviews(@Header("Authorization") token: String?, @Query("provider_id") id: String): Flowable<ReviewsResponse>

    @FormUrlEncoded
    @POST("delete-provider-image")
    fun deleteImage(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<DeleteImageResponse>

    @FormUrlEncoded
    @POST("update-provider-profile")
    fun changeWorkDates(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<EditUserProfileResponse>

    @Multipart
    @POST("update-provider-profile")
    fun uploadProviderImage(
        @Header("Authorization") token: String,
        @Part("user_id") user_id: RequestBody,
        @Part image: MultipartBody.Part?
    ): Flowable<EditUserProfileResponse>

    @Multipart
    @POST("update-client-profile")
    fun updatePicture(
        @Header("Authorization") token: String,
        @Part("user_id") user_id: RequestBody,
        @Part logo: MultipartBody.Part?
    ): Flowable<EditUserProfileResponse>

    @FormUrlEncoded
    @POST("update-client-profile")
    fun updateUserProfile(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, String>): Flowable<UpdateUserProfile>

    @GET("get-notifications")
    fun getNotifications(@Header("Authorization") token: String?, @Query("user_id") id: String): Flowable<NotificationResponse>

    @FormUrlEncoded
    @POST("contact-us")
    fun contactUs(@FieldMap myMap: Map<String, String>): Flowable<ContactUsResponse>

    @GET("search")
    fun search(@Header("Authorization") token: String?, @Query("search_key") id: String): Flowable<SearchResponse>

    @GET("areas")
    fun getCities(): Flowable<CitiesResponse>

    @GET("cities")
    fun getCitiesList(@Query("governorate_id") id: String): Flowable<CitiesList>

    @GET("areas")
    fun getAreasList(@Query("city_id") id: String): Flowable<AreasList>

    @FormUrlEncoded
    @POST("register-client")
    fun signUpUser(@FieldMap myMap: Map<String, String>): Flowable<UserSignUpResponse>

    @FormUrlEncoded
    @POST("register-provider")
    fun signUpProvider(@FieldMap myMap: Map<String, String>): Flowable<ProviderSignUpResponse>

//    @FormUrlEncoded
//    @POST("register-client")
//    fun signUpUser(@FieldMap myMap: Map<String, String>): Flowable<UserSignUpResponse>
//
//    @FormUrlEncoded
//    @POST("register-provider")
//    fun signUpProvider(@FieldMap myMap: Map<String, String>): Flowable<ProviderSignUpResponse>



//    @GET("slider")
//    fun getSlider(): Flowable<SliderResponse>
//
//    @GET("areas")
//    fun getCities(): Flowable<CitiesResponse>
//
//    @GET("services")
//    fun getServices(): Flowable<ServicesResponse>
//
//    @GET("search")
//    fun search(@Header("Authorization") token: String?, @Query("search_key") id: String): Flowable<SearchResponse>
//
//    @GET("get-count-unread-notifications")
//    fun getUnReadNotifications(@Header("Authorization") token: String?, @Query("user_id") id: String): Flowable<UnReadNotificationCountResponse>
//
//    @GET("get-profile")
//    fun getUserProfile(@Header("Authorization") token: String?, @Query("user_id") id: String): Flowable<ProfileResponse>
//
//    @GET("get-rates")
//    fun getReviews(@Header("Authorization") token: String?, @Query("provider_id") id: String): Flowable<ReviewsResponse>
//
//    @FormUrlEncoded
//    @POST("update-client-profile")
//    fun updateUserProfile(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, String>): Flowable<UpdateUserProfile>
//
//    @FormUrlEncoded
//    @POST("contact-us")
//    fun contactUs(@FieldMap myMap: Map<String, String>): Flowable<ContactUsResponse>
//
//    @FormUrlEncoded
//    @POST("create-rate")
//    fun addReview(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<AddReviewResponse>
//
//    @FormUrlEncoded
//    @POST("logout")
//    fun logout(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<LogoutResponse>

    @FormUrlEncoded
    @POST("update-firebase")
    fun updateToken(@FieldMap myMap: Map<String, Any>): Flowable<String>

//    @FormUrlEncoded
//    @POST("increase-count")
//    fun increaseCount(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<EditUserProfileResponse>
//
//    @FormUrlEncoded
//    @POST("delete-provider-image")
//    fun deleteImage(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<DeleteImageResponse>
//
//  @FormUrlEncoded
//    @POST("add-provider-service")
//    fun addService(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<AddServiceResponse>
//
//    @FormUrlEncoded
//    @POST("update-provider-profile")
//    fun changeWorkDates(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<EditUserProfileResponse>
//
//    @FormUrlEncoded
//    @POST("advertisements/delete")
//    fun deleteAd(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<DeleteAd>
//
//    @FormUrlEncoded
//    @POST("questions/delete")
//    fun deleteQuestion(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<DeleteQuestion>
//
//    @FormUrlEncoded
//    @POST("questions/delete-comment")
//    fun deleteComment(@Header("Authorization") token: String?,@FieldMap myMap: Map<String, Any>): Flowable<DeleteQuestion>
//
//
//    @GET("get-notifications")
//    fun getNotifications(@Header("Authorization") token: String?, @Query("user_id") id: String): Flowable<NotificationResponse>
//
//    @Multipart
//    @POST("advertisements/create")
//    fun createAds(@Header("Authorization") token: String,
//                  @Part("user_id") user_id: RequestBody,
//                  @Part("title") title: RequestBody,
//                  @Part("content") content: RequestBody,
//                  @Part image: MultipartBody.Part?): Flowable<CreateAdResponse>
//
//    @Multipart
//    @POST("questions/create")
//    fun createQuestion(@Header("Authorization") token: String,
//                  @Part("user_id") user_id: RequestBody,
//                  @Part("content") content: RequestBody,
//                  @Part image: MultipartBody.Part?): Flowable<CreateAdResponse>
//
//    @FormUrlEncoded
//    @POST("questions/add-comment")
//    fun addComment(@Header("Authorization") token: String,@FieldMap myMap: Map<String, Any>): Flowable<AddCommentResponse>
//
//    @Multipart
//    @POST("advertisements/update")
//    fun updateAd(@Header("Authorization") token: String,
//                  @Part("id") id: RequestBody,
//                  @Part("user_id") user_id: RequestBody,
//                  @Part("title") title: RequestBody,
//                  @Part("content") content: RequestBody,
//                  @Part image: MultipartBody.Part?): Flowable<UpdateAd>
//
//    @Multipart
//    @POST("questions/update")
//    fun updateQuestion(@Header("Authorization") token: String,
//                  @Part("id") id: RequestBody,
//                  @Part("user_id") user_id: RequestBody,
//                  @Part("content") content: RequestBody,
//                  @Part image: MultipartBody.Part?): Flowable<UpdateQuesstion>
//
//
//    @GET("advertisements")
//    fun getAds(@Query("pagination") pagination: String, @Query("limit") limit: Int): Flowable<AdsResponse>
//
//    @GET("questions")
//    fun getQuestions(@Query("pagination") pagination: String, @Query("limit") limit: Int): Flowable<QuestionsResponse>
//
//    @GET("questions/one")
//    fun getComments(@Query("id") id: String): Flowable<CommentsResponse>
//
//
//    @GET("advertisements/my")
//    fun getMyAds(@Query("user_id") user_id: String,@Query("pagination") pagination: String, @Query("limit") limit: Int): Flowable<MyAds>
//
//
//    @GET("setting")
//    fun getSettings(): Flowable<SettingsResponse>
//
//
//    @GET("services")
//    fun getSubServices(@Query("perant_id") id: String): Flowable<SubServicesResponse>
//
//
//    @GET("get-providers")
//    fun getProviders(@Query("sub_service_id") id: String): Flowable<ProvidersResponse>
//
//
//    @Multipart
//    @POST("update-client-profile")
//    fun updatePicture(
//        @Header("Authorization") token: String,
//        @Part("user_id") user_id: RequestBody,
//        @Part logo: MultipartBody.Part?
//    ): Flowable<EditUserProfileResponse>
//
//    @Multipart
//    @POST("update-provider-profile")
//    fun updateProviderPicture(
//        @Header("Authorization") token: String,
//        @Part("user_id") user_id: RequestBody,
//        @Part logo: MultipartBody.Part?
//    ): Flowable<EditUserProfileResponse>
//
//    @Multipart
//    @POST("update-provider-profile")
//    fun uploadProviderImage(
//            @Header("Authorization") token: String,
//            @Part("user_id") user_id: RequestBody,
//            @Part image: MultipartBody.Part?
//    ): Flowable<EditUserProfileResponse>

//    @FormUrlEncoded
//    @POST("user/register")
//    fun signUp(@FieldMap myMap: Map<String, String>
//    ): Flowable<SignUpResponse>
//
//    @FormUrlEncoded
//    @POST("user/login")
//    fun login(@FieldMap myMap: Map<String, String>
//    ): Flowable<LoginResponse>
//
//    @GET("user/blogs")
//    fun getArticles(@Header("Authorization") token: String?
//    ): Flowable<ArticlesResponse>
//
//    @GET("user/blog")
//    fun getArticle(@Header("Authorization") token: String?, @Query("blog_id") id: Int): Flowable<ArticleResponse>
//
//    @POST("user/like-blog")
//    fun addLike(@Header("Authorization") token: String?, @Query("blog_id") id: Int): Flowable<LikeResponse>
//
//    @GET("user/faqs")
//    fun getQuestions(@Header("Authorization") token: String?
//    ): Flowable<QuestionsResponse>
//
//    @GET("user/invitations/category")
//    fun getMubadraat(@Header("Authorization") token: String?, @Query("category") category: String): Flowable<MubadraatResponse>
//
//    @GET("user/sites/type")
//    fun getWebsites(@Header("Authorization") token: String?, @Query("type") type: String, @Query("page") page: Int): Flowable<WebsiteResponse>

//    @GET("user/withdraw-requests")
//    fun getWithdrawList(@Header("Authorization") token: String?
//    ): Flowable<GetLastWithdrawResponse>
//
//    @GET("user/payment-methods")
//    fun paymentMethods(@Header("Authorization") token: String?
//    ): Flowable<PaymentMethodsResponse>
//
//    @GET("user/get-referrals")
//    fun getReferralList(@Header("Authorization") token: String?
//    ): Flowable<GetReferralsResponse>
//
//    @FormUrlEncoded
//    @POST("user/spin")
//    fun setSpin(@FieldMap myMap: Map<String, Int>, @Header("Authorization") token: String
//    ): Flowable<SetSpinResponse>
//
//    @FormUrlEncoded
//    @POST("user/withdraw")
//    fun withdraw(@FieldMap myMap: Map<String, Any>, @Header("Authorization") token: String
//    ): Flowable<WithdrawResponse>
//
//
//    @FormUrlEncoded
//    @POST("user/register")
//    fun submitList(
//        @Field("username") phone: String,
//        @Field("password") password: String
//    ): Flowable<String>

}