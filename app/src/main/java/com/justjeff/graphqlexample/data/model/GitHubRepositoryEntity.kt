package com.justjeff.graphqlexample.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity("repositories", primaryKeys = ["name", "owner"])
data class GitHubRepositoryEntity(
    @ColumnInfo("name") val name: String,
    @ColumnInfo("owner") val owner: String,
    @ColumnInfo("desc") val description: String?,
)