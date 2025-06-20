package com.bteamcoding.bubbletranslation.feature_auth.di

import com.bteamcoding.bubbletranslation.feature_auth.data.network.AuthApi
import com.bteamcoding.bubbletranslation.feature_auth.data.network.UserApi
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.api.AuthApiService
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.api.UserApiService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindAuthApi(
        impl: AuthApiService
    ) : AuthApi

    @Binds
    @Singleton
    abstract fun bindUserApi(
        impl: UserApiService
    ) : UserApi
}