package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import dagger.hilt.android.AndroidEntryPoint
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.PlaygInfoImageFullScreenBinding
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.inVisible
import ru.radixit.letsplay.utils.visible


@AndroidEntryPoint
class PlaygInfoFullScreenFrag : Fragment() {

    private lateinit var viewModel: PlaygroundInfoViewModel
    private lateinit var binding: PlaygInfoImageFullScreenBinding
    private val args by navArgs<PlaygInfoFullScreenFragArgs>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = PlaygInfoImageFullScreenBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[PlaygroundInfoViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        viewModel.getPlayground(args.id)
        viewModel.general.observe(viewLifecycleOwner) {
            with(binding) {
                if (args.photo.isNotEmpty()) {
                    titleReviewsCount.text = it.ratingsCount.toString()
                    if(it.ratingsCount == 0){
                        binding.ratingBarToolBar.inVisible()
                        binding.nullStartsImg.visible()
                    }else{
                        binding.ratingBarToolBar.visible()
                        binding.nullStartsImg.inVisible()
                        binding.ratingBarToolBar.rating = it.ratingsCount.toFloat()
                    }
                    viewModel.title.observe(viewLifecycleOwner) {
                        titleToolBar.text = it
                    }
                    infoFullImageRv.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    val adapter = PlaygInfoFullScreenAdapter {
                        infoRating.text = it
                    }

                    with(args.photo) {
                        adapter.setData(
                            if (this.isNotEmpty()) this else arrayOf(
                                null
                            )
                        )
                    }
                    infoFullImageRv.adapter = adapter
                    PagerSnapHelper().attachToRecyclerView(infoFullImageRv)
                } else {
                    infoRating.gone()
                    infoFullImageRv.gone()
                    emptyImage.visible()
                }
            }
        }
    }

}