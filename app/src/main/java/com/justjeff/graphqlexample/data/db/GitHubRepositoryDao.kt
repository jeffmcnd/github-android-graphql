package com.justjeff.graphqlexample.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.justjeff.graphqlexample.data.model.GitHubRepositoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GitHubRepositoryDao {
    @Query("SELECT * FROM repositories")
    fun getAll(): Flow<List<GitHubRepositoryEntity>>

    @Query("SELECT * FROM repositories WHERE name = :name AND owner = :owner")
    fun findByNameAndOwner(name: String, owner: String): Flow<GitHubRepositoryEntity?>

    @Insert
    fun insert(vararg repos: GitHubRepositoryEntity)

    @Query("DELETE FROM repositories WHERE name = :name AND owner = :owner")
    fun deleteByNameAndOwner(name: String, owner: String): Int
}