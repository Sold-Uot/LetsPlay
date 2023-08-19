package ru.radixit.letsplay.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.radixit.letsplay.data.model.Playground
import ru.radixit.letsplay.data.network.api.PlaygroundsApi
import ru.radixit.letsplay.data.network.request.ListRequest
import javax.inject.Inject

class PlaygroundPagingSource @Inject constructor(
    private val query: String,
    private val service: PlaygroundsApi
) :
    PagingSource<Int, Playground>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Playground> {
        return try {
            val nextPage = params.key ?: 1
            val listRequest =
                ListRequest(pageIndex = nextPage.toString(), search = query, pageSize = "30")
            val response =
                service.listPlaygrounds(
                    listRequest.search,
                    listRequest.pageIndex,
                    listRequest.pageSize
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

    override fun getRefreshKey(state: PagingState<Int, Playground>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}