package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.radixit.letsplay.presentation.global.BaseFragment

class DetailsAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private var list = emptyList<Fragment>()

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    fun setData(arrayList: ArrayList<BaseFragment>) {
        this.list = arrayList
        notifyDataSetChanged()
    }
}