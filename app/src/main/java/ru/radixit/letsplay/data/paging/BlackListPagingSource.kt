package ru.radixit.letsplay.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.api.ProfileApi
import javax.inject.Inject

class BlackListPagingSource @Inject constructor(
    private val query: String,
    private val service: ProfileApi
) :
    PagingSource<Int, User>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val nextPage = params.key ?: 1
            val response = service.blackList(
                pageIndex = nextPage.toString(),
                search = query,
                pageSize = "30"
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

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}