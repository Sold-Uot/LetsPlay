package ru.radixit.letsplay.presentation.ui.fragments.tabs.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragChatRedesBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.chat.tab_layout_frags.FragEventsChatRedes
import ru.radixit.letsplay.presentation.ui.fragments.tabs.chat.tab_layout_frags.FragPlayersChatRedes
import ru.radixit.letsplay.presentation.ui.fragments.tabs.chat.tab_layout_frags.FragTeamsChatRedes
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.EventsAdapter
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible


@AndroidEntryPoint
class FragChatRedes : Fragment() {
    private lateinit var binding: FragChatRedesBinding
    private var positionMain: Int? = null

    private val arrayListFragments =
        arrayListOf(FragPlayersChatRedes(), FragTeamsChatRedes(), FragEventsChatRedes())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragChatRedesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        settingView()
    }

    private fun settingView() {
        settingTabLay()
        settingRV()
    }

    private fun settingRV() {
        with(binding) {
            searchPlayers.setOnSearchClickListener {
                val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
                params.startToStart = ConstraintSet.PARENT_ID
                searchPlayers.layoutParams = params
                titleListFriendsTv.gone()
            }

            searchPlayers.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewLifecycleOwner.lifecycleScope.launch {
                        positionMain?.let {
                            (arrayListFragments[it] as FragPlayersChatRedes).updateSearchResults(binding.searchPlayers.query.toString());
                            determitateFrag(it,binding.searchPlayers.query.toString())
                        }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })

            searchPlayers.setOnCloseListener(object : SearchView.OnCloseListener {
                override fun onClose(): Boolean {
                    val params = searchPlayers.layoutParams as ConstraintLayout.LayoutParams
                    params.startToStart = ConstraintLayout.LayoutParams.UNSET
                    searchPlayers.layoutParams = params
                    titleListFriendsTv.visible()
                    return false
                }
            })
        }
    }

    private fun settingTabLay() {
        with(binding) {
            val viewPager = binding.viewPager
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    try{
                        Log.d("currentFragment","position = ${arrayListFragments[position]}")
                        positionMain = position
                        determitateFrag(position, binding.searchPlayers.query.toString())
                    }catch (e:Exception){
                        Log.d("currentFragment","e = ${e}")
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })


            val adapter = EventsAdapter(this@FragChatRedes)
            adapter.setData(arrayListFragments)
            viewPager.adapter = adapter
            viewPager.isUserInputEnabled = false
            TabLayoutMediator(
                binding.tabLayout, viewPager
            ) { tab, position ->
                binding.searchPlayers.setQuery("",false)
                when (position) {
                    0 -> {
                        tab.text = getString(R.string.players)
                    }
                    1 -> {
                        tab.text = getString(R.string.teams)
                    }
                    2 -> {
                        tab.text = "События"
                    }
                }
            }.attach()
        }
    }

    private fun determitateFrag(position: Int, searchText: String) {
        when (position) {
            0 -> {
                (arrayListFragments[position] as FragPlayersChatRedes).updateSearchResults(searchText);
            }
            1 -> {
                (arrayListFragments[position] as FragTeamsChatRedes).updateSearchResults(searchText);
            }
            2 -> {
                (arrayListFragments[position] as FragEventsChatRedes).updateSearchResults(searchText);
            }
        }
    }
}