package ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.teams.choice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentChoiceUsersForCreatTeamBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.ChoiceUsersAdapter


class ChoiceUsersForCreateTeamFragment : DialogFragment() {

    private var _binding: FragmentChoiceUsersForCreatTeamBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )

    }

    private val arrayListFragments = arrayListOf(
        SelectedUsersForCreateTeamFragment(),
        FriendsForCreateTeamFragment(),
        SearchUsersForCreateTeamFragment()
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChoiceUsersForCreatTeamBinding.inflate(inflater, container, false)

        val viewPager = binding.viewPager
        val adapter = ChoiceUsersAdapter(this)
        adapter.setData(arrayListFragments)
        viewPager.adapter = adapter
        TabLayoutMediator(
            binding.tabLayout, viewPager
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Выбрано"
                }

                1 -> {
                    tab.text = "Друзья"
                }

                2 -> {
                    tab.text = "Поиск"
                }
            }
        }.attach()
        onBack()
        return binding.root
    }

    private fun onBack() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().navigate(ChoiceUsersForCreateTeamFragmentDirections.actionChoiceUsersForCreateTeamFragmentToCreateTeamFragment())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}