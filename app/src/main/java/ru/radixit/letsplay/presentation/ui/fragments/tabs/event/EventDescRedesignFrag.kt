package ru.radixit.letsplay.presentation.ui.fragments.tabs.event

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_pointer.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.radixit.letsplay.R
import ru.radixit.letsplay.databinding.FragEventDescRedesignBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.CreateEventViewModel
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.adapter.PlayerRedesAdapter
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.visible
import kotlin.math.abs

@AndroidEntryPoint
class EventDescRedesignFrag : DialogFragment(), OnMapReadyCallback {

    private val builder = SpannableStringBuilder()
    private var _binding: FragEventDescRedesignBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var client: FusedLocationProviderClient
    private val viewModel: EventDescriptionViewModel by viewModels()
    private val viewModelMap: CreateEventViewModel by viewModels()
    private var myLatLng: LatLng? = null
    private val args by navArgs<EventDescRedesignFragArgs>()
    private val id by lazy {
        Log.d("id =", "event.id = ${args.event?.id}, id = ${args.id}")
        if (args.event != null) {
            args.event?.id.toString()
        } else {
            args.id
        }
    }

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
        _binding = FragEventDescRedesignBinding.inflate(inflater, container, false)

        mapFragment =
            (childFragmentManager.findFragmentById(R.id.map_frag_event) as SupportMapFragment?)!!
        client = LocationServices.getFusedLocationProviderClient(requireContext())

//        binding.goToPlayground.setOnClickListener {
//            viewModel.eventDescription.observe(viewLifecycleOwner) {
//                findNavController().navigate(
//                    EventDescriptionFragmentDirections.actionEventDescriptionFragmentToPlaygroundInfoFragment2(
//                        it.playgroundId.toString()
//                    )
//                )
//            }
//        }
//        binding.share.setOnClickListener {
//            context?.startActivity(
//                Intent.createChooser(
//                    Intent(Intent.ACTION_SEND)
//                        .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_event))
//                        .setType("text/plain"), "Поделиться"
//                )
//            )
//        }
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingView()
    }

    @SuppressLint("ResourceType")
    private fun settingView() {
        Log.w("id_event",id.toString())

        settingAppBar()
        id?.let { id ->
/*
            viewModel.getEvent(id)
*/
            viewModel.newGetEventDesc(id)
            Log.w("start_newGetEv",id.toString())
            binding.include.apply {
                binding.include.shareTv.setOnClickListener {
//                    val intent = Intent(Intent.ACTION_SEND)
//                    intent.type = "text/plain"
//                    intent.putExtra(Intent.EXTRA_TEXT, "${getString(R.string.base_url)}")
//
//                    startActivity(Intent.createChooser(intent, "Поделиться ссылкой"))
                }
                binding.include.arrowEndImg.setOnClickListener {
                    findNavController().navigate(
                        EventDescRedesignFragDirections.actionEventDescRedesignFragToItemListPlaying(
                            id
                        )
                    )
                }
                binding.include.createEvent.setOnClickListener {
                    viewModel.eventDescription.observe(viewLifecycleOwner) {
                        if (findNavController().currentDestination?.id == R.id.eventDescRedesignFrag) {
                            val bundle = Bundle()
                            bundle.putString("id", it.id.toString())
                            findNavController().navigate(
                                R.id.action_eventDescriptionFragment_to_changePositionFragment,
                                bundle
                            )
                        }
                    }
                }


                playingListRv.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                playingListRv.setHasFixedSize(true)
                val adapterPlaying = PlayerRedesAdapter{

                }
                playingListRv.adapter = adapterPlaying
                viewModel.listEventsMembers(id)
                viewModel.eventMember.observe(viewLifecycleOwner) {
                    adapterPlaying.submitList(it.list)
                }
            }
        }
        with(binding.include) {
            viewModel.successLoading.observe(viewLifecycleOwner) {
                binding.infoProgressBar.loadingProgressLayout.isVisible = it
            }
            viewModel.newEventDescription.observe(viewLifecycleOwner) {
                it.playgroundId?.let {
                    setMarkerInMap(it)
                }
                it.start?.let { start ->
                    dateEventDesc.text = start.split(" ")[0]
                    clockEventDesc.text = start.split(" ")[1]
                } ?: run {
                    dateEventDesc.text = "Неизв."
                    clockEventDesc.text = "Неизв."
                }
                playingCountEvent.text =
                    "${it.players ?: " Неизв"}"
                descCommentEvent.text = it.comment ?: "Комментарий отсутствует"
                levelGame.text = "${it.gameLevel ?: "Неизв"}"
                titleEventDesc.text = it.title
                descAddressInMap.text = it.address
                titleAddressInMap.text = it.playgroundTitle
                if (it.isOwner == true) {
                    createEvent.isEnabled = false
                    createEvent.visibility = GONE
                }
                if (it.isRequestSended == true) {
                    createEvent.isEnabled = false
                    createEvent.setBackgroundColor(Color.TRANSPARENT)
                    createEvent.text = "ЗАЯВКА ОТПРАВЛЕНА"
                }
            }
        }
        with(binding) {

/*
            viewModel.getEvent(args.event!!.id.toString())
*/
            viewModel.newGetEventDesc(args.event!!.id.toString())

            viewModel.newEventDescription.observe(viewLifecycleOwner){

                it.createdBy?.photo?.let {
                    Log.w("photo",it.url.toString())
                    Glide.with(root).load(it.url).into(avatarEvent)
                }

                it.createdBy?.surname?.let { surname ->
                    nameAvatarEvent.text = "${it.createdBy?.name} ${surname}"
                } ?: run {
                    nameAvatarEvent.text = "${it.createdBy?.name}"
                }
                it.preview?.let {
                    if(it.url !== "" || !it.url.isEmpty())
                        Glide.with(root).load(it.url).into(bigImgEvent)
                    else{
                        binding.bigImgEvent.gone()
                        binding.notFdImgEvent.visible()
                    }
                }?: run {
                    bigImgEvent.gone()
                    notFdImgEvent.visible()
                }
            }

        }
    }
    private fun setMarkerInMap(id: Int) {
        viewModelMap.maps()
        viewModelMap.maps.observe(viewLifecycleOwner) {
            for (i in it.geocodes) {
                if (i.id == id) {
                    myLatLng = LatLng(i.lat.toDouble(), i.lng.toDouble())
                    mapFragment.getMapAsync(this)
                }
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.clear()
        myLatLng?.let { myLatLng ->
            val iconGenerator = IconGenerator(context)
            val inflatedView =
                View.inflate(context, R.layout.custom_point_map_redes, null)
            iconGenerator.setContentView(inflatedView)
            iconGenerator.setBackground(null)
            googleMap.addMarker(
                MarkerOptions().position(myLatLng).icon(
                    BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
                )
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 12F))
        }
    }

    private fun settingAppBar() {
        with(binding) {
//            val listCardViews = listOf(include.firstFriend,include.secondFriend,include.thirdFriend)

//            if(list.size > 0 && list.size < 4){
//                for(i in 0..list.size-1){
//                    Glide.with(binding.root).load(list[i].photo.url).into(listCardViews[i])
//                }
//            }else if(list.size > 3){
//                for(i in 0..list.size-1){
//                    if(i < listCardViews.size){
//                        Glide.with(binding.root).load(list[i].photo.url).into(listCardViews[i])
//                    }
//                }
//                include.countFriends.text = "+ ${list.size - listCardViews.size}"
//            }
            toolbar2.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            appBar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                if (abs(verticalOffset) - appBarLayout!!.totalScrollRange == 0) {
                    titleEventDescToolbar.visible()
                    materialCardView.gone()
                    nameAvatarEvent.gone()
                    appBar.setBackgroundColor(Color.parseColor("#232325"))
                } else {
                    titleEventDescToolbar.gone()
                    materialCardView.visible()
                    nameAvatarEvent.visible()
                    include.playingCountEvent.visible()
                    include.playingEvent.visible()
                    appBar.setBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }
}