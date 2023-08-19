package ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.play_list

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
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
import ru.radixit.letsplay.databinding.BottomSheetDialogBinding
import ru.radixit.letsplay.databinding.FragListMapsRedesBinding
import ru.radixit.letsplay.presentation.ui.fragments.tabs.event.create.CreateEventViewModel
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments.MapsFragmentDirections
import ru.radixit.letsplay.presentation.ui.fragments.tabs.playgrounds.fragments.MapsViewModel
import ru.radixit.letsplay.utils.gone
import ru.radixit.letsplay.utils.inVisible
import ru.radixit.letsplay.utils.showToast
import ru.radixit.letsplay.utils.visible

@AndroidEntryPoint
class ListMapsRedesFrag : DialogFragment(), OnMapReadyCallback {

    private var isFirstLocation = false
    private var latLng: LatLng? = null
    private val viewModel: MapsViewModel by viewModels()
    private var viewModelEvent: CreateEventViewModel? = null
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var client: FusedLocationProviderClient
    private lateinit var binding: FragListMapsRedesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialogStyle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragListMapsRedesBinding.inflate(layoutInflater)
        viewModelEvent = ViewModelProvider(requireActivity())[CreateEventViewModel::class.java]
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapFragment =
            (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        client = LocationServices.getFusedLocationProviderClient(requireContext())
        mapFragment.getMapAsync(this)
        goneSystemGpsBtn()
        binding.myGpsImg.setOnClickListener {
            settigGps()
        }

        binding.listImg.setOnClickListener {
            findNavController().navigate(ListMapsRedesFragDirections.actionListMapsRedesFragToListPlaygRedesFrag())
        }
        binding.cancelBtn.setOnClickListener {
            binding.constraintLayout7.gone()
        }
        onBack()
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

    private fun goneSystemGpsBtn() {
        val locationButton =
            (mapFragment.view?.findViewById<View>(Integer.parseInt("1"))?.parent as View).findViewById<View>(
                Integer.parseInt("2")
            )
        val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        layoutParams.setMargins(0, 0, 60, 920)
        locationButton.layoutParams = layoutParams
        locationButton.visibility = View.GONE
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 46) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }

    private fun onBack() {
        binding.toolbar2.setNavigationOnClickListener {
            findNavController().popBackStack()
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
            mapFragment.getMapAsync(this@ListMapsRedesFrag)
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
        if (!isFirstLocation) {
            val regionLatLng = LatLng(43.057222, 46.915278)

            val zoomLevel = 8f
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(regionLatLng, zoomLevel))
            isFirstLocation = !isFirstLocation
        }
        viewModel.maps()
        if (view != null) {
            viewModel.maps.observe(viewLifecycleOwner) {
                for (i in it.geocodes) {
                    val position = LatLng(i.lat.toDouble(), i.lng.toDouble())
                    Log.d(
                        "position2",
                        "latitude = ${i.lat.toDouble()}, longitude = ${i.lng.toDouble()}"
                    )
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
                        googleMap.addMarker(
                            MarkerOptions().position(position).icon(
                                BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
                            ).snippet(String.format("%.1f", distance) + " " + i.id)
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
                    googleMap.setOnMarkerClickListener { marker ->
                        viewModel.getPlaygroundForMap(marker.snippet!!.split(" ")[1].toInt())
                        viewModel.playgroundForMap.observe(viewLifecycleOwner) { playground ->
                            binding.constraintLayout7.visible()
                            binding.saveDataBtn.setOnClickListener {
                                viewModelEvent?.savePlaygId(playground.id)
                                viewModelEvent?.setLatLng(
                                    LatLng(
                                        marker.position.latitude,
                                        marker.position.longitude
                                    )
                                )
                                viewModelEvent?.setTitle(playground.title)
                                findNavController().popBackStack()
                            }
                        }
//                    bottomSheetDialog.findViewById<TextView>(R.id.bottom_sheet_distance)!!.text =
//                        "Расстояние: ${marker.snippet!!.split(" ")[0]} км"
                        viewModel.playgroundForMap.observe(viewLifecycleOwner) { playground ->
                            binding.titleToolbar.text =
                                playground.address

                        }
                        false
                    }
                }
            }
        }
    }
}