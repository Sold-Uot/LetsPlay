package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_notifications.view.*
import ru.radixit.letsplay.databinding.DialogExitProfileRedesBinding
import ru.radixit.letsplay.databinding.DialogReportRedesBinding
import ru.radixit.letsplay.databinding.ReviewFullScreenRedesFragBinding
import ru.radixit.letsplay.presentation.ui.MainActivity
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.fragments.ReviewsAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.UserProfileViewModel
import ru.radixit.letsplay.utils.*

@AndroidEntryPoint
class ReviewFullScreenRedesFrag : Fragment() {
    private lateinit var binding: ReviewFullScreenRedesFragBinding
    private lateinit var viewModel: PlaygroundInfoViewModel
    private val args by navArgs<ReviewFullScreenRedesFragArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ReviewFullScreenRedesFragBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity())[PlaygroundInfoViewModel::class.java]
        viewModel.getPlayground(args.id)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[PlaygroundInfoViewModel::class.java]
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        settingRecyclerView()
        ratingRB()
    }

    private fun ratingRB() {

        binding.progBar1.setProgress(70)
        viewModel.general.observe(viewLifecycleOwner) {
            if (it.ratingsCount == 0) {
                binding.nullStartsImg.visible()
                binding.ratingBarSite.inVisible()
            } else {
                binding.nullStartsImg.inVisible()
                binding.ratingBarSite.visible()
                binding.ratingBarSite.rating = it.ratingsCount.toFloat()
            }
            binding.titleReviewsCount.text = it.ratingsCount.toString()
        }
    }

    private fun settingRecyclerView() {
        val reviewsRecyclerView = binding.siteReviewsRv
        reviewsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        val adapter = ReviewsAdapter()
        viewModel.commentList()
        viewModel.comments.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.setData(it)
                binding.emptyReviewsLinLay.gone()
            } else {
                binding.emptyReviewsLinLay.visible()
            }
        }
        adapter.selectItem {
            showDialogReport(it)
        }
        reviewsRecyclerView.adapter = adapter
        reviewsRecyclerView.addItemDecoration(SpaceItemDecoration(50))
    }

    private fun showDialogReport(id: String) {
        val viewModelReport = ViewModelProvider(requireActivity())[UserProfileViewModel::class.java]
        val customDialog = Dialog(requireActivity())
        val bindingDialog = DialogReportRedesBinding.inflate(layoutInflater)
        customDialog.setContentView(bindingDialog.root)
        val checkBoxs = arrayListOf(
            bindingDialog.optionOne,
            bindingDialog.optionTwo,
            bindingDialog.optionThree,
            bindingDialog.optionFour,
            bindingDialog.optionFive
        )
        checkBoxs.forEachIndexed { index, radioButton ->
            radioButton.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                    if (p0?.id == bindingDialog.optionFive.id){
                        if(p1){
                            bindingDialog.searchFields.isVisible = index == checkBoxs.lastIndex
                        }else{
                            bindingDialog.searchFields.gone()
                        }
                    }
                    bindingDialog.searchFields.isVisible = index == checkBoxs.lastIndex
                    bindingDialog.buttonReportTv.setOnClickListener {
                        if (index == checkBoxs.lastIndex) {
                            if (bindingDialog.searchFields.text.toString().isNotEmpty()) {
                                viewModelReport.report(
                                    id,
                                    if (index == checkBoxs.lastIndex) 0 else index + 1,
                                    radioButton.text.toString()
                                )
                            } else {
                                bindingDialog.searchFields.error = "Пусто"
                            }
                        } else {
                            viewModelReport.report(
                                args.id,
                                if (index == checkBoxs.lastIndex) 0 else index + 1
                            )
                        }
                    }
                }
            })
        }
        viewModelReport.textMessage.observe(viewLifecycleOwner) {
            bindingDialog.reportContainer.showSnackBar(it)
        }
        viewModelReport.success.observe(viewLifecycleOwner) {
            if (it) {
                customDialog.dismiss()
            }
        }
        customDialog.window?.setLayout(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customDialog.show()
    }
}