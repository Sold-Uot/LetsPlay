package ru.radixit.letsplay.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.radixit.letsplay.data.model.Chat
import ru.radixit.letsplay.data.network.api.ChatApi
import javax.inject.Inject

class ChatPagingSource @Inject constructor(
    private val service: ChatApi
) : PagingSource<Int, Chat>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Chat> {
        return try {
            val nextPage = params.key ?: 1
            val response =
                service.fetchChats()

            LoadResult.Page(
                data = response.body()!!.list,
                prevKey = if (nextPage == 1) null else nextPage.minus(1),
                nextKey = if (nextPage > response.body()!!.pages) null else nextPage.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Chat>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}
