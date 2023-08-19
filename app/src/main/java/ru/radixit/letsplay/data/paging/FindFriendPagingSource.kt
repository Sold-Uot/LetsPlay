package ru.radixit.letsplay.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.radixit.letsplay.data.model.User
import ru.radixit.letsplay.data.network.api.UserApi
import ru.radixit.letsplay.data.network.request.ListRequest
import javax.inject.Inject

class FindFriendPagingSource @Inject constructor(
    private val query: String,
    private val service: UserApi
) : PagingSource<Int, User>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val nextPage = params.key ?: 1
            val request = ListRequest(search = query, pageSize = "5", pageIndex = "1")
            val response =
                request.search?.let {
                    request.pageSize?.let { it1 ->
                        request.pageIndex?.let { it2 ->
                            service.listUsers(
                                search = it,
                                pageSize = it1,
                                pageIndex = it2
                            )
                        }
                    }
                }

            LoadResult.Page(
                data = response?.body()!!.list,
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