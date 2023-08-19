package ru.radixit.letsplay.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.data.network.api.EventApi
import ru.radixit.letsplay.data.network.request.ListRequest
import javax.inject.Inject

class GlobalEventPagingSource @Inject constructor(
    private val query: String,
    private val service: EventApi
) : PagingSource<Int, Event>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Event> {
        return try {
            val nextPage = params.key ?: 1
            val request =
                ListRequest(pageIndex = nextPage.toString(), search = query, pageSize = "15")
            val response =
                service.listEvents(request.pageIndex, request.search, request.pageSize)

            LoadResult.Page(
                data = response.body()!!.list,
                prevKey = if (nextPage == 1) null else nextPage.minus(1),
                nextKey = if (nextPage > response.body()!!.pages) null else nextPage.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Event>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}