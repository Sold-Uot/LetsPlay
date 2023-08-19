package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfileAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private var list = emptyList<Fragment>()

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    fun setData(arrayList: ArrayList<Fragment>) {
        this.list = arrayList
        notifyDataSetChanged()
    }
}