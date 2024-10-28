package com.justjeff.graphqlexample

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(ViewModelComponent::class)
class CoroutinesModule {
    @ViewModelScoped
    @Provides
    fun provideViewModelScope(): CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
}