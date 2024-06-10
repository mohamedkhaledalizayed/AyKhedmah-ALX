package com.mohamed.aykhedmah.di


import com.mohamed.aykhedmah.view.common.aboutapp.AboutAppScreen
import com.mohamed.aykhedmah.view.common.allservices.ServicesScreen
import com.mohamed.aykhedmah.view.common.allservices.ServicesViewModelModule
import com.mohamed.aykhedmah.view.common.base.BaseScreen
import com.mohamed.aykhedmah.view.common.base.BaseViewModelModule
import com.mohamed.aykhedmah.view.common.contactus.ContactUsScreen
import com.mohamed.aykhedmah.view.common.contactus.ContactUsViewModelModule
import com.mohamed.aykhedmah.view.common.editprofile.EditProfileScreen
import com.mohamed.aykhedmah.view.common.editprofile.EditProfileViewModelModule
import com.mohamed.aykhedmah.view.common.login.LoginScreen
import com.mohamed.aykhedmah.view.common.login.LoginViewModelModule
import com.mohamed.aykhedmah.view.common.notification.NotificationScreen
import com.mohamed.aykhedmah.view.common.notification.NotificationViewModelModule
import com.mohamed.aykhedmah.view.common.providerdetails.ProviderDetailsScreen
import com.mohamed.aykhedmah.view.common.providerdetails.ProviderDetailsViewModelModule
import com.mohamed.aykhedmah.view.common.providers.ProvidersScreen
import com.mohamed.aykhedmah.view.common.providers.ProvidersViewModelModule
import com.mohamed.aykhedmah.view.common.rate.ReviewsScreen
import com.mohamed.aykhedmah.view.common.rate.ReviewsViewModel
import com.mohamed.aykhedmah.view.common.rate.ReviewsViewModelModule
import com.mohamed.aykhedmah.view.common.search.SearchScreen
import com.mohamed.aykhedmah.view.common.splash.SplashScreen
import com.mohamed.aykhedmah.view.common.supservices.SubServicesScreen
import com.mohamed.aykhedmah.view.common.supservices.SubServicesViewModelModule
import com.mohamed.aykhedmah.view.common.terms.TermsScreen
import com.mohamed.aykhedmah.view.provider.home.ProviderHomeScreen
import com.mohamed.aykhedmah.view.provider.home.ProviderHomeViewModelModule
import com.mohamed.aykhedmah.view.provider.profile.ProviderProfileScreen
import com.mohamed.aykhedmah.view.provider.signup.SignUpProviderScreen
import com.mohamed.aykhedmah.view.user.profile.ProfileViewModelModule
import com.mohamed.aykhedmah.view.user.profile.UserProfileScreen
import com.mohamed.aykhedmah.view.user.signup.SignUpUserScreen
import com.mohamed.aykhedmah.view.user.signup.SignUpViewModelModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {


    @ContributesAndroidInjector(modules = [BaseViewModelModule::class])
    abstract fun contributeBaseScreen(): BaseScreen

    @ContributesAndroidInjector(modules = [BaseViewModelModule::class])
    abstract fun contributeSplashScreen(): SplashScreen

    @ContributesAndroidInjector(modules = [LoginViewModelModule::class])
    abstract fun contributeLoginActivity(): LoginScreen

    @ContributesAndroidInjector(modules = [ServicesViewModelModule::class])
    abstract fun contributeServicesScreen(): ServicesScreen

    @ContributesAndroidInjector(modules = [SubServicesViewModelModule::class])
    abstract fun contributeSubServicesScreen(): SubServicesScreen

    @ContributesAndroidInjector(modules = [ProvidersViewModelModule::class])
    abstract fun contributeProvidersScreen(): ProvidersScreen

    @ContributesAndroidInjector(modules = [ProviderDetailsViewModelModule::class])
    abstract fun contributeProviderDetailsScreen(): ProviderDetailsScreen

    @ContributesAndroidInjector(modules = [ReviewsViewModelModule::class])
    abstract fun contributeReviewsScreen(): ReviewsScreen

    @ContributesAndroidInjector(modules = [ProfileViewModelModule::class])
    abstract fun contributeUserProfileScreen(): UserProfileScreen

    @ContributesAndroidInjector(modules = [EditProfileViewModelModule::class])
    abstract fun contributeEditProfileScreen(): EditProfileScreen

    @ContributesAndroidInjector(modules = [NotificationViewModelModule::class])
    abstract fun contributeNotificationScreen(): NotificationScreen

    @ContributesAndroidInjector(modules = [ContactUsViewModelModule::class])
    abstract fun contributeProviderAddQuestionActivity(): ContactUsScreen

    @ContributesAndroidInjector(modules = [BaseViewModelModule::class])
    abstract fun contributeAboutAppScreen(): AboutAppScreen

    @ContributesAndroidInjector(modules = [BaseViewModelModule::class])
    abstract fun contributeTermsScreen(): TermsScreen

    @ContributesAndroidInjector(modules = [ProvidersViewModelModule::class])
    abstract fun contributeSearchScreen(): SearchScreen

    @ContributesAndroidInjector(modules = [SignUpViewModelModule::class])
    abstract fun contributeSignUpUserScreen(): SignUpUserScreen

    @ContributesAndroidInjector(modules = [ProviderHomeViewModelModule::class])
    abstract fun contributeProviderHomeScreen(): ProviderHomeScreen

    @ContributesAndroidInjector(modules = [SignUpViewModelModule::class])
    abstract fun contributeSignUpProviderScreen(): SignUpProviderScreen

    @ContributesAndroidInjector(modules = [ProfileViewModelModule::class])
    abstract fun contributeProviderProfileScreen(): ProviderProfileScreen
//
//    @ContributesAndroidInjector(modules = [EditProfileViewModelModule::class])
//    abstract fun contributeUserEditProfileScreen(): UserEditProfileScreen
//
//    @ContributesAndroidInjector(modules = [ContactUsViewModelModule::class])
//    abstract fun contributeContactUsScreen(): ContactUsScreen
//
//    @ContributesAndroidInjector(modules = [NotificationViewModelModule::class])
//    abstract fun contributeNotificationScreen(): NotificationScreen
//
//    @ContributesAndroidInjector(modules = [AdsViewModelModule::class])
//    abstract fun contributeAdsScreen(): AdsScreen
//
//    @ContributesAndroidInjector(modules = [AddAdViewModelModule::class])
//    abstract fun contributeAddAdScreen(): AddAdScreen
//
//    @ContributesAndroidInjector(modules = [SubServicesViewModelModule::class])
//    abstract fun contributeSubServicesScreen(): SubServicesScreen
//
//    @ContributesAndroidInjector(modules = [ProvidersViewModelModule::class])
//    abstract fun contributeProvidersScreen(): ProvidersScreen
//
//    @ContributesAndroidInjector(modules = [ProvidersViewModelModule::class])
//    abstract fun contributeSearchScreen(): SearchScreen
//
//    @ContributesAndroidInjector(modules = [ProfileViewModelModule::class])
//    abstract fun contributeProviderDetailsScreen(): ProviderDetailsScreen
//
//    @ContributesAndroidInjector(modules = [ReviewsViewModelModule::class])
//    abstract fun contributeReviewsScreen(): ReviewsScreen


}