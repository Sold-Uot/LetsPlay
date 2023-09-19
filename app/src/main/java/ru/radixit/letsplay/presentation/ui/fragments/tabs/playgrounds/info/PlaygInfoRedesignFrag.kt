package ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.maps.android.ui.IconGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_pointer.*
import kotlinx.android.synthetic.main.item_playgrounds_recyclerview.*
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.DialogReportRedesBinding
import ru.radixit.letsplay.databinding.FragmentPlaygroundInfoRedesignBinding
import ru.radixit.letsplay.databinding.ReviewsDialogBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.adapter.ShowTrafficAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.fragments.ReviewsAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.UserProfileViewModel
import ru.radixit.letsplay.utils.*
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.util.*
import kotlin.math.abs


@AndroidEntryPoint
class PlaygInfoRedesignFrag : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentPlaygroundInfoRedesignBinding? = null
    private val binding get() = _binding!!

    /*
        private lateinit var viewModel: PlaygroundInfoViewModel
    */
    private val args by navArgs<PlaygInfoRedesignFragArgs>()
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var client: FusedLocationProviderClient
    private val viewModel by viewModels<PlaygroundInfoViewModel>()
    private var position = 1
    private var latLng: LatLng? = null
    private val id by lazy {
        args.id
    }

    override fun onMapReady(googleMap: GoogleMap) {
        latLng?.let { latLng ->
            val position = latLng
            val iconGenerator = IconGenerator(context)
            val inflatedView =
                View.inflate(context, R.layout.custom_point_map_redes, null)
            iconGenerator.setContentView(inflatedView)
            iconGenerator.setBackground(null)
            googleMap.addMarker(
                MarkerOptions().position(position).icon(
                    BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
                ).snippet(String.format("%.1f", distance) + " " + args.id)
            )

            googleMap.setOnCameraMoveStartedListener {

            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12F))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaygroundInfoRedesignBinding.inflate(inflater, container, false)
//        viewModel = ViewModelProvider(requireActivity())[PlaygroundInfoViewModel::class.java]
        mapFragment =
            (childFragmentManager.findFragmentById(R.id.map_frag) as SupportMapFragment?)!!
        args.latLng?.let {
            latLng = it
            mapFragment.getMapAsync(this)
        } ?: run {
            viewModel.maps()
            viewModel.maps.observe(viewLifecycleOwner) {
                for (i in it.geocodes) {
                    if (i.id == id.toInt()) {
                        latLng = LatLng(i.lat.toDouble(), i.lng.toDouble())
                        mapFragment.getMapAsync(this)
                    }
                }
            }
        }

        viewModel.getPlayground(args.id)
        return binding.root
    }

    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    abstract class AppBarStateChangeListener : OnOffsetChangedListener {
        enum class State {
            EXPANDED, COLLAPSED, IDLE
        }

        private var mCurrentState = State.IDLE
        override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
            mCurrentState = if (i == 0) {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout, State.EXPANDED)
                }
                State.EXPANDED
            } else if (Math.abs(i) >= appBarLayout.totalScrollRange) {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout, State.COLLAPSED)
                }
                State.COLLAPSED
            } else {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout, State.IDLE)
                }
                State.IDLE
            }
        }

        abstract fun onStateChanged(appBarLayout: AppBarLayout?, state: State?)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.include.swipeToRefresh.setOnRefreshListener {
            setupData()
        }
        setupData()
        settingAppBar()
        onBack()

        client = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun settingAppBar() {
        binding.appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (abs(verticalOffset) - appBarLayout!!.totalScrollRange == 0) {
                binding.linearLayout.visible()
            } else {
                binding.linearLayout.gone()
            }
        }
    }

    private fun setupProgressBar() {
        viewModel.progress.observe(viewLifecycleOwner) {
            binding.infoProgressBar.loadingProgressLayout.isVisible = it
        }
    }

    private fun setupData() {
        setupProgressBar()
        setupRecyclerView()
        settingRecyclerView()
        setupAnimationForElements()
        clickListeners()

        with(viewModel) {
            with(binding) {
                val slots = PhoneNumberUnderscoreSlotsParser().parseSlots("+7 (___) ___-__-__")
                val mask = MaskImpl.createTerminated(slots)
                mask.isHideHardcodedHead = true
                mask.toUnformattedString()
                val formatWatcher: FormatWatcher = MaskFormatWatcher(
                    mask
                )
                formatWatcher.installOn(include.numberPhoneContent)
                title.observe(viewLifecycleOwner) {
                    include.contentFieldAddress.text = it
                    contentFieldAddressMain.text = it
                }
                getPlaygroundForMap(args.id.toInt())
                playgroundForMap.observe(viewLifecycleOwner) {
                    include.bottomSheetHours.text = it.freeHours
                }
                more.observe(viewLifecycleOwner) {
                    with(include) {
                        it.ball?.let {
                            ballsValueTv.text = if (it.isDigitsOnly()) {
                                "Есть (+${it}₽)"
                            } else {
                                it
                            }
                        }
                        it.shower?.let {
                            showersValueTv.text = if (it.isDigitsOnly()) {
                                "Есть (+${it}₽)"
                            } else {
                                it
                            }
                        }
                        it.changingRooms?.let {
                            changingRoomsValueTv.text = if (it.isDigitsOnly()) {
                                "Есть (+${it}₽)"
                            } else {
                                it
                            }
                        }
                        it.lighting?.let {
                            lightingValueTv.text = if (it.isDigitsOnly()) {
                                "Есть (+${it}₽)"
                            } else {
                                it
                            }
                        }
                        it.manishki?.let {
                            shirtFrontsValueTv.text = if (it.isDigitsOnly()) {
                                "Есть (+${it}₽)"
                            } else {
                                it
                            }
                        }
                        fieldSizeValueTv.text = it.fieldSize
                        paymentCardValueTv.text = it.paymentByTransfer
                        studdedCleatsValueTv.text = it.studdedCleats
                    }
                }

                if (include.swipeToRefresh.isRefreshing) {
                    include.swipeToRefresh.isRefreshing = false
                }
            }
        }
        viewModel.getPlayground(args.id)
    }

    private fun settingRecyclerView() {
        val reviewsRecyclerView = binding.include.siteReviewsRv
        reviewsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        val adapter = ReviewsAdapter()
        viewModel.commentList()
        viewModel.comments.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.setData(it)
                binding.include.siteReviewsRv.visible()
                binding.include.emptyReviewsLinLay.gone()
            } else {
                binding.include.siteReviewsRv.inVisible()
                binding.include.emptyReviewsLinLay.visible()
            }
        }
        adapter.selectItem {
            showDialogReport(it)
        }
        reviewsRecyclerView.adapter = adapter
        reviewsRecyclerView.addItemDecoration(SpaceItemDecoration(50))
    }

    private fun showDialogReviews() {
        val reviewsDialogBinding = ReviewsDialogBinding.inflate(layoutInflater)
        val customDialog = Dialog(requireActivity())

        with(customDialog) {

            setContentView(reviewsDialogBinding.root)
            window?.setLayout(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            show()
            reviewsDialogBinding.buttonReviewsTv.setOnClickListener {
                Log.e("rating" ,                    reviewsDialogBinding.ratingReviewsStar.rating.toInt().toString()
                )
                viewModel.comment(
                    id = args.id,
                    reviewsDialogBinding.ratingReviewsStar.rating.toInt(),
                    reviewsDialogBinding.reviewsTv.text.toString()
                )
                viewModel.progressButton.observe(viewLifecycleOwner){
                    if (it == false){
                        dismiss()
                    }
                    else{
                        reviewsDialogBinding.buttonReviewsTv.gone()
                        reviewsDialogBinding.progressBar4.visible()
                    }
                }

            }




        }

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
                    if (p0?.id == bindingDialog.optionFive.id) {
                        if (p1) {
                            bindingDialog.searchFields.visible()
                        } else {
                            bindingDialog.searchFields.gone()
                        }
                    }
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

    private fun clickListeners() {
        with(binding.include) {
            phoneInfo.setOnClickListener {
                val intent =
                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:${numberPhoneContent.text}"))
                activity?.let {
                    startActivity(intent)
                }
            }
            durationPathInfo.setOnClickListener {
                openMaps()
            }
            shareInfo.setOnClickListener {
                context?.startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_event))
                            .setType("text/plain"), "Поделиться"
                    )
                )
            }
            reviewsMatBtn.setOnClickListener {

                showDialogReviews()

            }
            showAllTv.setOnClickListener {
                findNavController().navigate(
                    PlaygInfoRedesignFragDirections.actionPlaygInfoRedesignFragToReviewFullScreenRedesFrag(
                        args.id
                    )
                )
            }
            createEvent.setOnClickListener {
                findNavController().navigate(
                    PlaygInfoRedesignFragDirections.actionPlaygInfoRedesignFragToEventInDetailRedesFrag2(
                        latLng, binding.contentFieldAddressMain.text.toString(), id
                    )
                )
            }
            copyNumberPhone.setOnClickListener {
                val clipboard: ClipboardManager? =
                    activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText("Copy Number", numberPhoneContent.text)
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(context, "Номер скопирован", Toast.LENGTH_SHORT).show()
            }

            copyAddress.setOnClickListener {
                val clipboard: ClipboardManager? =
                    activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                val clip = ClipData.newPlainText("Copy Address", generalAddress.text)
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(context, "Адрес скопирован", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun openMaps() {
        val location = "${latLng?.latitude},${latLng?.longitude}"
        val uri = Uri.parse("geo:$location?q=$location")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)

        // Проверяем наличие приложений карт на устройстве
        context?.let {
            if (mapIntent.resolveActivity(it.packageManager) != null) {
                // Открываем другое приложение карт
                startActivity(mapIntent)
            } else {
                // Если приложений карт нет, открываем сайт карт
                val websiteIntent = Intent(Intent.ACTION_VIEW)
                val url = "https://maps.google.com/?q=$location"
                websiteIntent.data = Uri.parse(url)
                startActivity(websiteIntent)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupAnimationForElements() {
        binding.appBar.addOnOffsetChangedListener(OnOffsetChangedListener { _, verticalOffset ->
            if (verticalOffset == 0) {
                with(binding) {
//                    constraintEvent.animate().translationY(include.indicator.height.toFloat())
//                        .alpha(0.0f).duration = 190
                    toolbar.setTitleTextAppearance(requireContext(), R.style.Toolbar_TitleText)
//                    include.indicator.animate().translationY(include.indicator.height.toFloat())
//                        .alpha(1.0f).duration = 190
//                    include.indicator.isVisible = true
                }
            } else {
                with(binding) {
//                    constraintEvent.animate().translationY(include.indicator.height.toFloat())
//                        .alpha(1.0f).duration = 190
                    toolbar.setTitleTextAppearance(
                        requireContext(),
                        R.style.Toolbar_TitleText_Regular
                    )
//                    include.indicator.animate().translationY(include.indicator.height.toFloat())
//                        .alpha(0.0f).duration = 190
//                    include.indicator.isVisible = false
                }
            }
        })
    }

    private fun setupRecyclerView() {
        binding.include.apply {
            viewModel.general.observe(viewLifecycleOwner) {
                if (it.ratingsCount == 0) {
                    rbRatingBar.inVisible()
                    nullStartsImg.visible()
                } else {
                    rbRatingBar.visible()
                    nullStartsImg.inVisible()
                    rbRatingBar.rating = it.ratingsCount.toFloat()
                }
                rbRatingBar.rating = it.rating.toFloat()
                titleReviewsCountContent.text = it.rating
                countReviews.text = "(${it.ratingsCount})"
                priceContentScroll.text = "от ${it.price.trim()}"
                numberPhoneContent.text = it.phone
                generalAddress.text = it.address
                naturalTv.text = when (it.coating) {
                    0 -> {
                        "Асфальт"
                    }

                    1 -> {
                        "Натуральное"
                    }

                    else -> {
                        "Искусственное"
                    }
                }
                val adapter = ShowTrafficAdapter {

                }
                showTrafficRv.layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                showTrafficRv.setHasFixedSize(true)
                showTrafficRv.adapter = adapter
                if (it.schedule.isNotEmpty()) {
                    adapter.submitList(it.schedule)
                    emptyList.gone()
                } else {
                    emptyList.visible()
                }
//                showTrafficSpinner.adapter = adapterSpinner
//                showTrafficSpinner.setOnItemSelectedListener(object :
//                    AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                        val pos = p2 + 1
//                        position = pos
//                    }
//
//                    override fun onNothingSelected(p0: AdapterView<*>?) {
//                    }
//                })
//                showTrafficSpinner.setSelection(0)
                constLayPhone2.setOnClickListener {
                    if (showTrafficRv.isVisible) {
                        showTrafficTvArrow.setImageResource(R.drawable.ic_arrow_bottom)
                    } else {
                        showTrafficTvArrow.setImageResource(R.drawable.ic_arrow_top)
                    }
                    showTrafficRv.isVisible = !showTrafficRv.isVisible
                }
                if (it.ratingsCount == 0) {
                    binding.rbRatingBar.inVisible()
                    binding.nullStartsImg.visible()
                } else {
                    binding.rbRatingBar.visible()
                    binding.nullStartsImg.inVisible()
                    binding.rbRatingBar.rating = it.ratingsCount.toFloat()
                }

                binding.countReviews.text = "(${it.ratingsCount})"
                binding.titleReviewsCount.text = it.rating
                binding.rbRatingBar.rating = it.rating.toFloat()
            }
        }
        val infoRecyclerView = binding.infoRecyclerView
        infoRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        infoRecyclerView.setHasFixedSize(true)
        val adapter = PlaygroundInfoAdapter { type, item, position ->
            when (type) {
                CallBackInfoAdapter.CLICK_ITEM -> {
                    findNavController().navigate(
                        PlaygInfoRedesignFragDirections.actionPlaygroundInfoFragmentToPlaygInfoFullScreenFrag(
                            args.id,
                            item!!,
                        )
                    )
                }

                CallBackInfoAdapter.SCROLL_ITEM -> {
                    binding.infoPositionRv.text = position
                }
            }
        }
        viewModel.fetchPhotos(args.id.toInt())
        viewModel.photos.observe(viewLifecycleOwner) {
            adapter.setData(
                if (it.isNotEmpty()) it else listOf(
                    null
                )
            )
        }
        infoRecyclerView.adapter = adapter
        infoRecyclerView.setOnFlingListener(null);
        PagerSnapHelper().attachToRecyclerView(infoRecyclerView)
    }

    private fun onBack() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}