package ru.radixit.letsplay.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.radixit.letsplay.data.model.Notification
import ru.radixit.letsplay.data.network.api.NotificationApi
import ru.radixit.letsplay.data.network.request.ListRequest
import javax.inject.Inject

class NotificationPagingSource @Inject constructor(
    private val service: NotificationApi
) : PagingSource<Int, Notification>() {
    override fun getRefreshKey(state: PagingState<Int, Notification>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notification> {
        return try {
            val nextPage = params.key ?: 1
            val request =
                ListRequest(pageIndex = nextPage.toString(), pageSize = "15")
            val response = service.listNotifications(request.pageSize!!, request.pageIndex!!)

            LoadResult.Page(
                data = response.body()!!.list,
                prevKey = if (nextPage == 1) null else nextPage.minus(1),
                nextKey = if (nextPage > response.body()!!.pages) null else nextPage.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}