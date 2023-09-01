package ru.radixit.letsplay.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.radixit.letsplay.data.model.CreatedBy
import ru.radixit.letsplay.data.model.Event
import ru.radixit.letsplay.domain.repository.ProfileRepository
import javax.inject.Inject

class EventPagingSource @Inject constructor(
    private val repository: ProfileRepository,
    private val request: Int
) :
    PagingSource<Int, Event>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Event> {
        return try {
            val nextPage = params.key ?: 1
            val response =
                repository.eventsList(
                    pageIndex = nextPage.toString(),
                    request = request,
                    pageSize = "30",
                    filter = "active"
                )


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