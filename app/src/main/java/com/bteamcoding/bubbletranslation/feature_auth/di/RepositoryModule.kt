package com.bteamcoding.bubbletranslation.feature_auth.di

import com.bteamcoding.bubbletranslation.feature_auth.data.network.AuthApi
import com.bteamcoding.bubbletranslation.feature_auth.data.remote.api.AuthApiService
import com.bteamcoding.bubbletranslation.feature_auth.data.repository.AuthRepositoryImpl
import com.bteamcoding.bubbletranslation.feature_auth.data.repository.UserRepositoryImpl
import com.bteamcoding.bubbletranslation.feature_auth.domain.repository.AuthRepository
import com.bteamcoding.bubbletranslation.feature_auth.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ) : AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ) : UserRepository
}