package ru.radixit.letsplay.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(
    private val space: Int = 0,
    private val orientation: Orientation = Orientation.VERTICAL,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) > 0) { // Пропускаем отступ для первого элемента
            outRect.top = if (orientation == Orientation.VERTICAL) space else 0
            outRect.left = if (orientation == Orientation.HORIZONTAL) space else 0
        }
    }

    enum class Orientation { VERTICAL, HORIZONTAL }
}