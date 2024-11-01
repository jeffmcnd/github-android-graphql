package com.justjeff.graphqlexample.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.justjeff.graphqlexample.data.model.GitHubRepositoryEntity

@Database(entities = [GitHubRepositoryEntity::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gitHubRepositoryDao(): GitHubRepositoryDao
}