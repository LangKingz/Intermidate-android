package com.example.story.view.dashboard

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.story.data.ApiService

import com.example.story.data.Response.ListStoryItem
import com.example.story.data.RetrofitClient

class StoriesPagingSource(private val token: String) : PagingSource<Int, ListStoryItem>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val page = params.key ?: 1
        return try {
            val response = RetrofitClient.instance.getAllstories("Bearer $token", page)
            LoadResult.Page(
                data = response.listStory!!,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.listStory.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1) ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
