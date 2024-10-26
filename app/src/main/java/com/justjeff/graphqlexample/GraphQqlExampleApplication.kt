package com.justjeff.graphqlexample

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GraphQqlExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // No-op
    }
}