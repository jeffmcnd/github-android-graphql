package com.justjeff.graphqlexample

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideAuthInterceptorOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideApolloClient(client: OkHttpClient): ApolloClient =
        ApolloClient.Builder()
            .serverUrl("https://api.github.com/graphql")
            .okHttpClient(client)
            .build()
}