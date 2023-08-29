package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.utils.calendar
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
import com.google.android.material.textfield.TextInputLayout
import com.google.maps.android.ui.IconGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_pointer.*
import kotlinx.android.synthetic.main.frag_event_in_detail_redes.*
import kotlinx.android.synthetic.main.item_notifications.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.network.request.CreateEventRequest
import ru.radixit.letsplay.databinding.FragEventInDetailRedesBinding
import ru.radixit.letsplay.databinding.ProgressDialogBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.adapter.DropDownRvRedesAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.info.adapter.SpinnerCreatePlaygAdapter
import ru.radixit.letsplay.presentation.ui.fragments.tabs.profile.friends.RemoveFromFriendsFragmentArgs
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.showToast
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@AndroidEntryPoint
class EventInDetailRedesFrag : Fragment(), OnMapReadyCallback {

    private var _binding: FragEventInDetailRedesBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<EventInDetailRedesFragArgs>()
    private lateinit var viewModel: CreateEventViewModel
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var client: FusedLocationProviderClient
    private var googleMapMain: GoogleMap? = null
    private var dialogCustom: Dialog? = null
    private var playgId: Int? = null
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uri: List<Uri> ->
            uploadPhoto(uri)
        }

    private fun uploadPhoto(uri: List<Uri>) {
        Glide.with(binding.root).load(uri[uri.lastIndex]).into(binding.photoImg)
    }

    private var myLatLng: LatLng? = null

    override fun onMapReady(googleMap: GoogleMap) {
        googleMapMain = googleMap
        setMarkerInMap(googleMap)
    }

    private fun setMarkerInMap(googleMap: GoogleMap) {
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
                ).snippet(String.format("%.1f", distance) + " " + playgId)
            )
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 12F))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragEventInDetailRedesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CreateEventViewModel::class.java]
        mapFragment =
            (childFragmentManager.findFragmentById(R.id.map_frag_create_playg) as SupportMapFragment?)!!
        client = LocationServices.getFusedLocationProviderClient(requireContext())
        mapFragment.getMapAsync(this)
        onBack()
        viewModel.int(0)
        changeGameStatus()
        changeGameLevel()

        viewModel.playgId.observe(viewLifecycleOwner) {
            playgId = it
            viewModel.maps()
            viewModel.maps.observe(viewLifecycleOwner) {
                playgId?.let { playgId ->
                    for (i in it.geocodes) {
                        if (i.id == playgId) {
                            myLatLng = LatLng(i.lat.toDouble(), i.lng.toDouble())
                            googleMapMain?.let {
                                setMarkerInMap(it)
                            }
                        }
                    }
                }
            }
        }
        binding.timeInputEd.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.eventInDetailRedesFrag2) {
                createEventCalendar()
            } else {
                createEventCalendarPlayg()
            }
        }
        binding.birthYearInputEd.setOnClickListener {
            if (findNavController().currentDestination?.id == R.id.eventInDetailRedesFrag2) {
                createEventCalendar()
            } else {
                createEventCalendarPlayg()
            }
        }
        binding.createEventMatBtn.setOnClickListener {
            var gameLevel = 0
            var gameStatus = 0
            viewModel.gameLevel.observe(viewLifecycleOwner) {
                gameLevel = it
            }
            viewModel.gameStatus.observe(viewLifecycleOwner) {
                gameStatus = it
            }
            if (binding.nameField.text.toString().equals("")) {
                clearError(binding.materialCardView)
            } else if (binding.commentEventPlaceTv.text.toString().equals("")) {
                clearError(binding.commentEventPlaceTv)
            } else if (binding.timeInputEd.text.toString().equals("")) {
                clearError(binding.timeMatCard)
            } else if (binding.birthYearInputEd.text.toString().equals("")) {
                clearError(binding.birthYearMatCard)
            } else if (binding.addressMapTv.text.toString().equals("")) {
                clearError(binding.addressMapTv)
            } else if (binding.allCountPlayers.text.toString().equals("")) {
                clearError(binding.allCountPlayers)
            } else if (binding.playerWithoutApp.text.toString().equals("")) {
                clearError(binding.playerWithoutApp)
            } else {
                clearError(null)
                playgId?.let { playgId ->
//                    dialogProgress {
//                        it.show()
//                    }
                    viewModel.createEvent(
                        CreateEventRequest(
                            id = playgId,
                            startKey = viewModel.startAndEnd.value?.first?.key.toString().toInt(),
                            endKey = viewModel.startAndEnd.value?.second?.key.toString().toInt(),
                            gameLevel = gameLevel,
                            offlinePlayers = binding.playerWithoutApp.text.toString().toInt(),
                            comment = binding.commentEventPlaceTv.text.toString(),
                            players = binding.allCountPlayers.text.toString().toInt(),
                            status = gameStatus,
                            title = binding.nameField.text.toString(),
                            date = viewModel.date.value.toString(),
                            privacy = true

                        )
                    ) { isResult, eventId ->
//                        if (isResult) {
//                            findNavController().popBackStack()
//                        }
                        lifecycleScope.launch(Dispatchers.IO) {
                            withContext(Dispatchers.Main) {
                                dialogCustom?.dismiss()
                                Log.v(this.javaClass.name,isResult.toString())
                                if (isResult) {
                                    context?.showToast("ОК")
                                    if (findNavController().currentDestination?.id == R.id.eventInDetailRedesFrag2) {
                                        val bundle = Bundle()
                                        bundle.putString("event", null)
                                        bundle.putString("id", eventId.toString())
                                        findNavController().navigate(
                                            R.id.action_eventInDetailRedesFrag2_to_eventDescRedesignFrag,
                                            bundle
                                        )
                                    }
                                } else {
                                    context?.showToast("Empty response")
                                }
                            }
                        }
                    }
                }

            }
        }
        viewModel.startAndEnd.observe(viewLifecycleOwner) {
            binding.timeInputEd.error = null
            it.second?.let { second ->
                binding.timeInputEd.setText("${it.first.value} - ${it.second?.value}")
            } ?: kotlin.run {
                binding.timeInputEd.setText("${it.first.value}")
            }
        }
        binding.editAddressMapTv.setOnClickListener {
            findNavController().navigate(
                EventInDetailRedesFragDirections.actionEventInDetailRedesFragToListMapsRedesFrag(
                )
            )
        }
        binding.shirtsIs.setOnClickListener {
            findNavController().navigate(EventInDetailRedesFragDirections.actionEventInDetailRedesFragToPlayersEventRedesFrag())
        }
        binding.ballsIs.setOnClickListener {
        }
        viewModel.startString.observe(viewLifecycleOwner) {
            binding.birthYearInputEd.setText(it)
        }

        binding.addPhoto.setOnClickListener {
            selectImageFromGallery()
        }
        val slots = UnderscoreDigitSlotsParser().parseSlots("__-__-____")
        val mask = MaskImpl.createTerminated(slots)
        mask.isHideHardcodedHead = true
        val formatWatcher: FormatWatcher = MaskFormatWatcher(
            mask
        )
        formatWatcher.installOn(binding.birthYearInputEd)
        binding.birthYearInputEd.doAfterTextChanged {
            if (it.toString().length != 10) {
                binding.birthYearMatCard.endIconMode = TextInputLayout.END_ICON_NONE
                binding.birthYearMatCard.error = "Дата не действительна"
            } else {
                binding.birthYearMatCard.endIconMode = TextInputLayout.END_ICON_CUSTOM
                binding.birthYearMatCard.setEndIconTintList(
                    ContextCompat.getColorStateList(
                        requireContext(),
                        R.color.main_gray
                    )
                )
                binding.birthYearMatCard.endIconDrawable =
                    ResourcesCompat.getDrawable(resources, R.drawable.baseline_calendar_month, null)
                binding.birthYearMatCard.error = null
                try {
                    val sdf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    LocalDate.parse(it.toString(), sdf)
                } catch (e: DateTimeParseException) {
                    binding.birthYearMatCard.error = "Дата не действительна"
                }
            }
        }
        observerViewModels()
        return binding.root
    }

    private fun createEventCalendarPlayg() {
        if (binding.addressMapTv.text.toString() != "") {
            findNavController().navigate(
                EventInDetailRedesFragDirections.actionEventInDetailRedesFragPlaygToCreateEventRedesFrag(
                    playgId.toString()
                )
            )
        } else {
            Toast.makeText(
                context,
                "Сначала выберите площадку на карте",
                Toast.LENGTH_SHORT
            ).show()
            binding.addressMapTv.error = "Выберите площадку"
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.latLng == null) {
            viewModel.maps()
            viewModel.maps.observe(viewLifecycleOwner) {
                playgId?.let { playgId ->
                    for (i in it.geocodes) {
                        if (i.id == playgId) {
                            myLatLng = LatLng(i.lat.toDouble(), i.lng.toDouble())
                        }
                    }
                }
            }
        } else {
            playgId = args.playgId?.toInt()
            myLatLng = args.latLng
            binding.addressMapTv.text = args.addressTitle
        }
    }

    fun dialogProgress(createDialog: (Dialog) -> Unit) {
        context?.let { context ->
            dialogCustom = Dialog(context)
            dialogCustom?.let { dialog ->
                val binding: ProgressDialogBinding =
                    ProgressDialogBinding.inflate(LayoutInflater.from(context))
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.setContentView(binding.root)
                dialog.setCancelable(false)
                createDialog.invoke(dialog)
            }
        }
    }

    private fun clearError(idView: View?) {
        val list = listOf(
            binding.materialCardView, binding.commentEventPlaceTv,
            binding.timeMatCard, binding.birthYearMatCard, binding.addressMapTv,
            binding.allCountPlayers, binding.playerWithoutApp
        )
        for (i in list.indices) {
            idView?.let { idView ->
                if (idView.id == list[i].id) {
                    if (list[i] is TextView) {
                        (list[i] as TextView).error = "Заполните поле"
                        binding.scrollView.smoothScrollTo(0, (list[i] as TextView).top)
                    } else {
                        (list[i] as TextInputLayout).error = "Заполните поле"
                        binding.scrollView.smoothScrollTo(0, (list[i] as TextInputLayout).top)
                    }
                } else {
                    if (list[i] is TextView) {
                        (list[i] as TextView).error = null
                    } else {
                        (list[i] as TextInputLayout).error = null
                    }
                }
            } ?: run {
                if (list[i] is TextView) {
                    (list[i] as TextView).error = null
                } else {
                    (list[i] as TextInputLayout).error = null
                }
            }
        }
    }

    private fun createEventCalendar() {
        playgId?.let { playgId ->
            val bundle = Bundle()
            bundle.putString("id", playgId.toString())
            findNavController().navigate(
                R.id.action_eventInDetailRedesFrag_to_createEventRedesFrag,
                bundle
            )
        } ?: run {
            Toast.makeText(context, "Сначала выберите площадку на карте", Toast.LENGTH_SHORT).show()
            binding.addressMapTv.error = "Выберите площадку"
        }
    }

    private fun observerViewModels() {
        viewModel.titleAddress.observe(viewLifecycleOwner) {
            binding.addressMapTv.text = it
            binding.addressMapTv.error = null
        }
        viewModel.addressLatLng.observe(viewLifecycleOwner) {
            myLatLng = it
            googleMapMain?.let {
                setMarkerInMap(it)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun changeGameLevel() {
        val listFieldSize = listOf(
            getString(R.string.level_game_2),
            getString(R.string.level_game_1), getString(R.string.level_game)
        )
        binding.levelGameRv.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.levelGameRv.setHasFixedSize(true)
        val adapterCoating = DropDownRvRedesAdapter { obj, pos, str ->
            str?.let {
                hideKeyboard()
            }
            viewModel.changeGameLevelRedes(pos)
            obj.updateData()
            binding.levelGameSpinner.setSelection(pos)
            binding.levelGameRv.gone()
        }
        binding.levelGameRv.adapter = adapterCoating
        adapterCoating.submitList(listFieldSize)
        val adapterSpinner =
            SpinnerCreatePlaygAdapter(requireContext(), listFieldSize, "Уровень игры") {
                binding.levelGameRv.isVisible = !binding.levelGameRv.isVisible
            }
        binding.levelGameSpinner.adapter = adapterSpinner
        binding.levelGameSpinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })
    }

    private fun changeGameStatus() {
        val listFieldSize = listOf(
            getString(R.string.game_status),
            getString(R.string.game_status_private),
        )
        binding.statusGameRv.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.statusGameRv.setHasFixedSize(true)
        val adapterCoating = DropDownRvRedesAdapter { obj, pos, str ->
            str?.let {
                hideKeyboard()
            }
            viewModel.changeGameStatusRedes(pos)
            obj.updateData()
            binding.statusGameSpinner.setSelection(pos)
            binding.statusGameRv.gone()
        }
        binding.statusGameRv.adapter = adapterCoating
        adapterCoating.submitList(listFieldSize)
        val adapterSpinner =
            SpinnerCreatePlaygAdapter(requireContext(), listFieldSize, "Статус игры") {
                binding.statusGameRv.isVisible = !binding.statusGameRv.isVisible
            }
        binding.statusGameSpinner.adapter = adapterSpinner
        binding.statusGameSpinner.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        })
    }

    private fun onBack() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager? =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputManager?.hideSoftInputFromWindow(
            activity?.currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}