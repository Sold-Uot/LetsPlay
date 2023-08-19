package ru.radixit.letsplay.presentation.ui.fragments.tabs.event

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.maps.android.ui.IconGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_pointer.distance
import ru.radixit.letsplay.R
import ru.radixit.letsplay.data.network.response.EventForMap
import ru.radixit.letsplay.data.network.response.MapsResponse
import ru.radixit.letsplay.databinding.EventBottomMapRedesignBinding
import ru.radixit.letsplay.databinding.FragmentMapEventsBinding
import ru.radixit.letsplay.presentation.global.BaseFragment
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments.MapsFragment
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.showToast
import ru.radixit.letsplay.utils.visible
import java.util.*


@AndroidEntryPoint
class MapEventsFragment : BaseFragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapEventsBinding
    private val viewModel: MapListEventsViewModel by viewModels()
    private var latLng: LatLng? = null
    private var isFirstLocation = false
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var client: FusedLocationProviderClient
    private var googleMapMain: GoogleMap? = null
    private var id: Int? = null
    private val builder = SpannableStringBuilder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapEventsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.myGpsImg.setOnClickListener {
            settigGps()
        }
        binding.listEventsMapBtn.setOnClickListener {
            findNavController().navigate(MapEventsFragmentDirections.actionMapEventsFragmentToListEventsRedesFrag())
        }
        binding.constTopBar.addPlaygroundsEvent.setOnClickListener {
            findNavController().navigate(
                MapEventsFragmentDirections.actionMapEventsFragmentToEventInDetailRedesFrag22(
                    null,
                    null,
                    null
                )
            )
        }
        switchBtn()
        with(binding.constTopBar) {
            searchFieldsEvent.gone()
        }
        mapFragment =
            (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        client = LocationServices.getFusedLocationProviderClient(requireContext())
        mapFragment.getMapAsync(this)
        goneSystemGpsBtn()
        settigGps()
    }

    private fun settigGps() {
        if (isGpsEnabled()) {
            // GPS включен
            getCurrentLocation()
            binding.myGpsImg.setImageResource(R.drawable.circle_white)
            binding.gpsProgressBar.visible()
        } else {
            // GPS выключен, делаем запрос на включение
            showEnableGpsDialog()
        }
    }
    private fun goneSystemGpsBtn() {
        val locationButton =
            (mapFragment.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(
                Integer.parseInt("2")
            )
        val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        layoutParams.setMargins(0, 0, 60, -850)
        locationButton.layoutParams = layoutParams
        locationButton.visibility = View.GONE
    }

    private fun switchBtn() {
        with(binding.constTopBar) {
            allPeopleEvent.setOnClickListener {
                allPeopleEvent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.back_color
                    )
                )
                allPeopleEvent.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))

                onlyOnePersonEvent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                onlyOnePersonEvent.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background_for_edittext
                    )
                )
            }
            onlyOnePersonEvent.setOnClickListener {
                allPeopleEvent.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                allPeopleEvent.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.background_for_edittext
                    )
                )

                onlyOnePersonEvent.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                onlyOnePersonEvent.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.back_color
                    )
                )
            }
        }
    }


    fun isGpsEnabled(): Boolean {
        val locationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun showEnableGpsDialog() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Включить GPS")
        dialogBuilder.setMessage("Для полноценного использования карты, рекомендуется включить GPS")
        dialogBuilder.setPositiveButton("Включить") { dialog: DialogInterface, _: Int ->
            // Переход в настройки для включения GPS
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context?.startActivity(intent)
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton("Отмена") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun locationRequest() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10 * 1000 // 10 seconds
        locationRequest.fastestInterval = 5 * 1000 // 5 seconds

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            // GPS включен, можно выполнять операции с геолокацией
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Показываем пользователю диалоговое окно для включения GPS
                    exception.startResolutionForResult(
                        requireActivity(),
                        MapsFragment.REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ошибка открытия диалогового окна
                }
            } else {
                // GPS выключен и его нельзя включить программно
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val cancellationTokenSource = CancellationTokenSource()
        val currentLocationTask: Task<Location> = client.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        )
        currentLocationTask.addOnCompleteListener { location ->
            mapFragment.getMapAsync {
                it.isMyLocationEnabled = true
                latLng = LatLng(location.result.latitude, location.result.longitude)
                latLng?.let { latLng ->
                    it.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12F))
                    binding.gpsProgressBar.gone()
                    binding.myGpsImg.setImageResource(R.drawable.gps_btn)
                }
            }
            mapFragment.getMapAsync(this)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 44) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                46
            )
        }
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap) {
        googleMapMain = googleMap
        if (!isFirstLocation) {
            val regionLatLng = LatLng(43.057222, 46.915278)

            val zoomLevel = 8f
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(regionLatLng, zoomLevel))
            isFirstLocation = !isFirstLocation
        }
        viewModel.maps()
        viewModel.maps.observe(viewLifecycleOwner) {
            for (i in it) {
                val position = LatLng(i.lat.toDouble(), i.lng.toDouble())
                var marker: Marker? = null
                latLng?.let { latLng ->
                val iconGenerator = IconGenerator(context)
                val result = FloatArray(1)
                Location.distanceBetween(
                    latLng.latitude,
                    latLng.longitude,
                    position.latitude,
                    position.longitude,
                    result
                )
                val inflatedView =
                    View.inflate(context, R.layout.custom_pointer, null)
                val distance = result[0] / 1000
                inflatedView.findViewById<TextView>(R.id.distance).text =
                    String.format("%.1f", distance).replace(",", ".")
                iconGenerator.setContentView(inflatedView)
                iconGenerator.setBackground(null)
                marker = googleMap.addMarker(
                    MarkerOptions().position(position).icon(
                        BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
                    ).snippet(i.id.toString())
                )
                } ?: run {
                    val iconGenerator = IconGenerator(context)
                    val inflatedView =
                        View.inflate(context, R.layout.custom_point_map_redes, null)
                    iconGenerator.setContentView(inflatedView)
                    iconGenerator.setBackground(null)
                    marker = googleMap.addMarker(
                        MarkerOptions().position(position).icon(
                            BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
                        ).snippet(String.format("%.1f", distance) + " " + i.id)
                    )
                }
                viewModel.getEventForMap(marker?.snippet.toString())
                googleMap.setOnMarkerClickListener { marker ->
                    viewModel.getEventForMap(marker.snippet.toString())
                    val bindingDialog = EventBottomMapRedesignBinding.inflate(layoutInflater)
                    val bottomSheetDialog = BottomSheetDialog(requireContext())
                    bottomSheetDialog.setContentView(bindingDialog.root)
                    viewModel.eventForMap.observe(viewLifecycleOwner) { event ->
                        id = event.id
                        bindingDialog.titleEvent.text =
                            event.title
                        bindingDialog.placeEvent.text =
                            event.address
                        bindingDialog.levelGameMatBtn.text =
                            event.gameLevel
                        val str1 = SpannableString(event.players)
                        str1.setSpan(ForegroundColorSpan(Color.WHITE), 0, 1, 0)
                        builder.append(str1)
                        bindingDialog.countPlayers.text = str1
                        bindingDialog.timeEvent.text = event.start
//                        bottomSheetDialog.findViewById<TextView>(R.id.bottom_sheet_dialog)!!.text =
//                            event.address
//                        bottomSheetDialog.findViewById<TextView>(R.id.bottom_sheet_hours)!!.text =
//                            event.start
                        if (event.preview != null) {
                            bindingDialog.eventPhoto.isVisible =
                                true
                            Glide.with(bottomSheetDialog.context).load(event.preview)
                                .into(bindingDialog.eventPhoto)
                        } else {
                            bindingDialog.eventPhoto.isVisible =
                                false
                        }
                    }
                    if (marker.position != latLng) {
                        bottomSheetDialog.show()
                    }
                    bindingDialog.root.setOnClickListener {
                        if (findNavController().currentDestination?.id == R.id.mapEventsFragment) {
                            findNavController().navigate(
                                MapEventsFragmentDirections.actionMapEventsFragmentToEventDescRedesignFrag(
                                    null, id.toString()
                                )
                            )
                        }
                    }
//                    bottomSheetDialog.findViewById<MaterialCardView>(R.id.bottom_sheet_button)!!
//                        .setOnClickListener {
//                            bottomSheetDialog.dismiss()
//                        }
                    false
                }
            }
        }
//        listPlayg.add(EventForMap("test","test",100,"playesra",40,"preview","start","status","title"))
//        listGeocodes.add(MapsResponse.Geocodes(100, "55.74842349196731", "37.66367559814453"))
    }

    override fun onStop() {
        super.onStop()

        with(binding.constTopBar) {
            val params = searchFieldsEvent.layoutParams as ConstraintLayout.LayoutParams
            params.startToStart = ConstraintLayout.LayoutParams.UNSET
            searchFieldsEvent.layoutParams = params
            addPlaygroundsEvent.visible()
            allPeopleEvent.visible()
            searchFieldsEvent.setIconified(true)
            onlyOnePersonEvent.visible()
        }
    }
}