package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragmentChoiceUsersForEventBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.adaptes.ChoiceUsersAdapter

@AndroidEntryPoint
class ChoiceUsersForEventFragment : DialogFragment() {

    private var _binding: FragmentChoiceUsersForEventBinding? = null
    private val binding get() = _binding!!
    private val arrayListFragments = arrayListOf(
        SelectedUsersFragment(),
        FriendsForEventFragment(),
        SearchUsersForEventFragment()
    )
    private lateinit var viewModel: CreateEventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChoiceUsersForEventBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CreateEventViewModel::class.java]
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
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}