package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class PlaygroundLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<PlaygroundLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: PlaygroundLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PlaygroundLoadStateViewHolder {
        return PlaygroundLoadStateViewHolder.create(parent, retry)
    }
}
