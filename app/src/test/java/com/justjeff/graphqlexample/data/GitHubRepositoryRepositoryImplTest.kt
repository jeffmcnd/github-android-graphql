package com.justjeff.graphqlexample.data

import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mobilenativefoundation.store.store5.StoreReadRequest

class GitHubRepositoryRepositoryImplTest {
    private val store = mockk<GitHubRepositoryStore>(relaxed = true)
    private val params = GitHubRepositoryParams("test", "test")

    @Test
    fun `getRepository - Uses correct request`() = runTest {
        val subject = getSubject()
        val request = StoreReadRequest.cached(params, refresh = false)
        subject.getRepository(params)
        verify { store.stream(request) }
    }

    private fun getSubject(): GitHubRepositoryRepository = GitHubRepositoryRepositoryImpl(store)
}